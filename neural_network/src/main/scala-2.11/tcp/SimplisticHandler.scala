package tcp

import akka.actor.Actor
import akka.util.ByteString

class SimplisticHandler extends Actor {

  def receive = {
    case data: ByteString => println(new String(data.toByteBuffer.array()))
    case default => println(default)
  }
}