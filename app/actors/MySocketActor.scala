package actors

import akka.actor.{ Actor, ActorRef, Props }
import play.api.Logger
import play.api.libs.json.{ JsObject, JsValue, Json }

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: JsValue =>
      Logger.debug("message  " + Json.prettyPrint(msg))

      val msgVal = msg \ "foo"
      val json: JsValue = JsObject(Seq("message" -> msgVal.get))

      out ! (json)
  }
}
