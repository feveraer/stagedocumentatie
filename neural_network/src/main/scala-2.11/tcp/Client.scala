package tcp

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import java.net.InetSocketAddress

object Client {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {

  import Tcp._
  import context.system

  // Sending a connect message to the TCP manager
  IO(Tcp) ! Connect(remote)

  // The TCP manager replies with either a CommandFailed or it will spawn an internal
  // actor representing the new connection.
  def receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    // If successful, a Connected message is received.
    case c@Connected(remote, local) =>
      // Send Connected message to the listening actor
      listener ! c
      // The reference sender Actor of the last received message.
      val connection = sender()
      // Send Register message to the connection actor,
      // informing that one about who shall receive data from the socket.
      // Before this step is done the connection cannot be used,
      // and there is an internal timeout after which the connection actor will shut itself down.
      connection ! Register(self)
      // Use 'become' to switch from unconnected to connected operation.
      context become {
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          listener ! "write failed"
        case Received(data) =>
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
          context stop self
      }
  }
}
