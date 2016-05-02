package workers

import akka.actor.Actor
import akka.util.ByteString
import cassandra.CassandraConnection
import json.TcpJsonLog.{Decoder, Log}

/**
  * Created by Frederic on 25/04/2016.
  */
class CassandraWorker extends Actor {

  def receive = {
    // Data received over TCP socket is of type ByteString
    case log: Log => {
      // Insert SensorLog in Cassandra DB
      CassandraConnection.insertSensorLog(log.toSensorLog())
      // Insert SensorInfo in Cassandra DB
      CassandraConnection.insertSensorInfo(log.toSensorInfo())
      CassandraConnection.insertSetTemperature(log.toSetTemperatures())
      context.stop(self)
    }
    case default => {
      println(default)
      context.stop(self)
    }
  }
}
