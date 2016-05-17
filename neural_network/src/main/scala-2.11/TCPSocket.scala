import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}
import connections.Connections
import logging.LoggingConstants
import org.apache.log4j.{BasicConfigurator, Logger}
import tcp.{Client, QbusConstants}
import workers.ParentWorker

/**
  * Created by Frederic on 25/04/2016.
  */
object TCPSocket {

  def main(args: Array[String]) {
    BasicConfigurator.configure()
    Logger.getRootLogger().setLevel(LoggingConstants.level)

    Connections.connect

    val actorSystem = ActorSystem("ActorSystem")

    val handler = actorSystem.actorOf(Props[ParentWorker], name = "handler")

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
