package controllers

import javax.inject.{Inject, Singleton}

import actors.HelloActor.SayHello
import actors.{HelloActor, MyWebSocketActor}
import akka.actor.ActorSystem
import akka.pattern.ask
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.duration._

//using plays own actor system
@Singleton
class Application @Inject()(system: ActorSystem) extends Controller {

  val helloActor = system.actorOf(HelloActor.props, "hello-actor")
  implicit val timeout = akka.util.Timeout(5.seconds)
  implicit val app = play.api.Play.current

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

  def socket = WebSocket.acceptWithActor[String, String] { request => out =>
    MyWebSocketActor.props(out)
  }

  def appNg = Action {
    Ok(views.html.app())
  }

}
