package services

import java.util.Properties

import akka.actor.Props
import com.fortysevendeg.sparkon.services.twitter.domain.Conversions._
import common.StaticValues
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{ Duration, StreamingContext }
import org.slf4j.LoggerFactory
import play.Play
import services.domain.{ TweetsByDay, TweetsByTrack }
import twitter4j.Status
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

import scala.language.postfixOps

trait TwitterStreamingService extends Serializable {
  val logger = LoggerFactory.getLogger(this.getClass)

  def createTwitterStream(
    filters: Seq[String] = Seq.empty,
    storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER
  )(implicit ssc: StreamingContext) = {
    val authorization = new OAuthAuthorization(new ConfigurationBuilder()
      .setOAuthConsumerKey(Play.application().configuration().getString("twitter.consumerKey"))
      .setOAuthConsumerSecret(Play.application().configuration().getString("twitter.consumerSecret"))
      .setOAuthAccessToken(Play.application().configuration().getString("twitter.accessToken"))
      .setOAuthAccessTokenSecret(Play.application().configuration().getString("twitter.accessTokenSecret"))
      .build())

    ssc.actorStream[Status](
      Props(
        new TwitterReceiverActorStream[Status](
          twitterAuth = authorization,
          filters = filters
        )
      ),
      "TwitterStreamingReceiverActor",
      storageLevel
    )
  }

  def ingestTweets(
    topics: Set[String],
    windowSize: Duration,
    slideDuration: Duration,
    ssc: StreamingContext
  )(dsStream: DStream[Status]) = {

    val tweetsByDay: DStream[TweetsByDay] = getTweetsByDay(dsStream)

    val tweetsByTrack: DStream[TweetsByTrack] = getTweetsByTrack(dsStream, topics, windowSize, slideDuration)

    // tweetsByTrack -> kafka
    writeToKafka(tweetsByTrack)

    ssc.start()
  }

  def writeToKafka(dStream: DStream[TweetsByTrack]) =
    dStream.map(_.track).foreachRDD { rdd =>
      rdd foreachPartition { partition =>
        lazy val kafkaProducerParams = new Properties()

        val kafkaBootstrapServersFromEnv = sys.env.getOrElse("kafkaBootstrapServers", "")
        val kafkaProducerKeySerializerFromEnv = sys.env.getOrElse("kafkaProducerKeySerializer", "")
        val kafkaProducerValueSerializerFromEnv = sys.env.getOrElse("kafkaProducerValueSerializer", "")

        kafkaProducerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServersFromEnv)
        kafkaProducerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "")
        kafkaProducerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerValueSerializerFromEnv)
        val producer = new KafkaProducer[String, String](kafkaProducerParams)

        partition foreach {
          case m: String =>
            val message = new ProducerRecord[String, String]("playground.raw", StaticValues.javaNull, m)
            producer.send(message)
          case _ => logger.warn("Unknown Partition Message!")
        }
      }
    }

  def getTweetsByDay(dsStream: DStream[Status]): DStream[TweetsByDay] = dsStream.map(toTweetsByDay)

  def getTweetsByTrack(
    dsStream: DStream[Status],
    topics: Set[String],
    windowSize: Duration,
    slideDuration: Duration
  ): DStream[TweetsByTrack] =
    dsStream
      .flatMap(_.getText.toLowerCase.split("""\s+"""))
      .filter(topics.contains)
      .countByValueAndWindow(windowSize, slideDuration)
      .transform {
        (rdd, time) =>
          val dateParts = formatTime(time, "yyyy_MM_dd_HH_mm")
            .split("-")
            .map(_.toInt)
          rdd map {
            case (track, count) =>
              toTweetsByTrack(dateParts, track, count)
          }
      }
}
