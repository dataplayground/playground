package actors

import akka.actor.{ Actor, ActorRef, Props }
import play.api.Logger
import play.api.libs.json.JsValue

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: JsValue =>
      Logger.debug("actor something  " + out)

      out ! ("I received your message: " + msg)
  }
}
