package workers

import akka.actor.Actor
import cassandra.CassandraConnection
import json.TcpJsonLog.Log
import org.apache.log4j.Logger

/**
  * Created by Frederic on 25/04/2016.
  */
class CassandraWorker extends Actor {
  private val logger = Logger.getLogger("CassandraWorker")

  def receive = {
    // Data received over TCP socket is of type ByteString
    case log: Log => {
      // Insert SensorLog in Cassandra DB
      CassandraConnection.insertSensorLog(log.toSensorLog())
      // Insert SensorInfo in Cassandra DB
      CassandraConnection.insertSensorInfo(log.toSensorInfo())
      CassandraConnection.insertSetTemperature(log.toSetTemperatures())
      logger.info("Wrote log to Cassandra")
      context.stop(self)
    }
    case default => {
      println(default)
      context.stop(self)
    }
  }
}
