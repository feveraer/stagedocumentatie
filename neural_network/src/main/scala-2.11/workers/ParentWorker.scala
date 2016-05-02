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
      val predictWorker = context.actorOf(Props[PredictWorker])

      val log = decode(data)
      cassandraWorker ! log
      predictWorker ! log.toSensorLog()
    }
  }

  def decode(data: ByteString): Log = {
    val jsonData = new String(data.toByteBuffer.array())
    Decoder.decodeLogJson(jsonData)
  }
}
