import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}
import tcp.{Client, QbusConstants}
import workers.SimplisticHandler

/**
  * Created by Frederic on 25/04/2016.
  */
object TCPSocketTest {

  def main(args: Array[String]) {
    val actorSystem = ActorSystem("ActorSystem")

    val handler = actorSystem.actorOf(Props[SimplisticHandler], name = "handler")

    val client = actorSystem.actorOf(
      Props(
        classOf[Client],
        new InetSocketAddress(QbusConstants.TCP_SOCKET, QbusConstants.TCP_SOCKET_PORT),
        handler
      ),
      name = "client"
    )

  }
}
