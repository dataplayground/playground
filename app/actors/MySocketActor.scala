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
      Logger.debug("actor something  " + out)
      Logger.debug("message  " + Json.prettyPrint(msg))

      //      val value =
      //        """
      //      {
      //          "fooReply" : "$message"
      //      }
      //        """

      val msgVal = msg \ "foo"
      val json: JsValue = JsObject(Seq("message" -> msgVal.get))

      //      val jValue = new JsString("")
      //      val json: JsValue = Json.parse(value)
      out ! (json)
  }
}
