package workers

import akka.actor.Actor
import akka.util.ByteString
import cassandra.CassandraConnection
import json.TcpJsonLog.Decoder

/**
  * Created by Frederic on 25/04/2016.
  */
class CassandraWorker extends Actor {

  def receive = {
    // Data received over TCP socket is of type ByteString
    case data: ByteString => {

      val jsonData = new String(data.toByteBuffer.array())
      val log = Decoder.decodeLogJson(jsonData)

      // Insert SensorLog in Cassandra DB
      CassandraConnection.insertSensorLog(log.toSensorLog())
      // Insert SensorInfo in Cassandra DB
      CassandraConnection.insertSensorInfo(log.toSensorInfo())
    }
    case default => println(default)
  }

}
