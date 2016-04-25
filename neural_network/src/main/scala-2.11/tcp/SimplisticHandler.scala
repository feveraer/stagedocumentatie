package tcp

import akka.actor.Actor
import akka.io.Tcp
import akka.io.Tcp.{PeerClosed, Received, Write}

class SimplisticHandler extends Actor {

  import Tcp._

  def receive = {
    case Received(data) => sender() ! Write(data)
    case PeerClosed => context stop self
  }
}