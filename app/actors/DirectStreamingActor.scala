package actors

import akka.actor.{ Actor, ActorRef, Props }
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import services.TwitterStreamingService

object DirectStreamingActor {
  def props(out: ActorRef, ssc: StreamingContext) = Props(new DirectStreamingActor(out, ssc))
}

class DirectStreamingActor(out: ActorRef, ssc: StreamingContext) extends Actor {
  def receive = {
    case twitter: String =>

      //TODO change
      //      val topics = twitter
      val topics = "spark, angular"
      val brokers = "192.168.99.100:9092"

      val topicsSet = topics.split(",").toSet
      val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

      // start twitter util
      val streamCCC: StreamingContext = ssc
      val twitterStreamingServices = new TwitterStreamingService {}

      twitterStreamingServices.ingestTweets(
        topics = topicsSet,
        windowSize = Seconds(10),
        slideDuration = Seconds(10),
        ssc = streamCCC
      ) _

      val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
        ssc, kafkaParams, topicsSet
      )
      ssc.start()

      // Get the lines, split them into words, count the words and print
      val lines = messages.map(_._2)
      val words = lines.flatMap(_.split(" "))
      val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
      wordCounts.print()
      out ! (wordCounts.toString)
  }
}
