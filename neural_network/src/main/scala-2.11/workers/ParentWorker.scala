package workers

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.ByteString
import json.TcpJsonLog.{Decoder, Log}

/**
  * Created by Frederic on 2/05/2016.
  */
class ParentWorker extends Actor {

  override def receive = {
    case data: ByteString => {
      val cassandraWorker = context.actorOf(Props[CassandraWorker])
      val log = decode(data)
      val sensorLog = log.toSensorLog()

      // Send Log to CassandraWorker.
      cassandraWorker ! log

      // If measured temperature is less than the set temperature, let
      // WarmUpWorker handle the SensorLog, else CoolDownWorker.
      if (sensorLog.measuredTemp < sensorLog.setTemp) {
        val warmUpWorker = context.actorOf(Props[WarmUpWorker])
        warmUpWorker ! sensorLog
      } else {
        val coolDownWorker = context.actorOf(Props[CoolDownWorker])
        coolDownWorker ! sensorLog
      }
    }
  }

  def decode(data: ByteString): Log = {
    val jsonData = new String(data.toByteBuffer.array())
    Decoder.decodeLogJson(jsonData)
  }
}
