package controllers

import javax.inject.{ Inject, Singleton }

import actors.HelloActor.SayHello
import actors._
import akka.actor.ActorSystem
import akka.pattern.ask
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.{ SparkConf, SparkContext }
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

//using plays own actor system
@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {

  val helloActor = system.actorOf(HelloActor.props, "hello-actor")
  implicit val timeout = akka.util.Timeout(5.seconds)
  implicit val app = play.api.Play.current
  val logger = LoggerFactory.getLogger(this.getClass)

  val sparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("playground")

  val sparkContext = createSparkContext
  val ssc: StreamingContext = createStreamingContext(sparkContext)

  def createSparkContext: SparkContext = new SparkContext(sparkConf)

  def createStreamingContext(sparkContext: SparkContext): StreamingContext =
    new StreamingContext(sparkContext = sparkContext, batchDuration = Seconds(2))

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

  def socket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    Logger.debug("received socket something  " + out)
    MyWebSocketActor.props(out)
  }

  def appNg = Action {
    Ok(views.html.app())
  }

  def directStreaming = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    Logger.debug("startDirectStream  " + out)
    DirectStreamingActor.props(out, ssc)
  }
}
