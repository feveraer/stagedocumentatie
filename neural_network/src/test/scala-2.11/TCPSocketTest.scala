import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}
import connections.Connections
import tcp.{Client, QbusConstants}
import workers.CassandraWorker

/**
  * Created by Frederic on 25/04/2016.
  */
object TCPSocketTest {

  def main(args: Array[String]) {
    Connections.connect

    val actorSystem = ActorSystem("ActorSystem")

    val handler = actorSystem.actorOf(Props[CassandraWorker], name = "handler")

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
