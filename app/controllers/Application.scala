package controllers

import javax.inject.Inject

import actors.HelloActor
import actors.HelloActor.SayHello
import akka.actor.ActorSystem
import play.api.mvc._

//using plays own actor system
@Singleton
class Application @Inject()(system: ActorSystem) extends Controller {

  val helloActor = system.actorOf(HelloActor.props, "hello-actor")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  import akka.pattern.ask
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  import scala.concurrent.duration._

  implicit val timeout = 5.seconds

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

}
