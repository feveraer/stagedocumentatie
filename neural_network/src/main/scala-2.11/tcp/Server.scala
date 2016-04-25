package tcp

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.io.{IO, Tcp}

class Server(tcpSocket: String, port: Int) extends Actor {

  import Tcp._
  import context.system

  // Send Bind command to TCP manager.
  // This will instruct the TCP manager to listen for TCP connections on a particular InetSocketAddress.
  // The port may be specified as 0 in order to bind to a random port.
  IO(Tcp) ! Bind(self, new InetSocketAddress(tcpSocket, port))

  def receive = {
    case b@Bound(localAddress) =>
    // do some logging or setup ...

    case CommandFailed(_: Bind) => context stop self

    case c@Connected(remote, local) =>
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
  }

}
