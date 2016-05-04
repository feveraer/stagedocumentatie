package workers

import akka.actor.Actor
import ann.Constants
import cassandra.{CassandraConnection, SensorLog, SensorPrediction}
import org.apache.log4j.Logger
import time.DateTime

/**
  * Created by Frederic on 3/05/2016.
  */
class CoolDownWorker extends Actor {
  private val logger = Logger.getLogger("CoolDownWorker")

  override def receive = {
    case sensorLog: SensorLog => {
      correctSetTemp(sensorLog)
      context.stop(self)
    }
  }

  private def correctSetTemp(sensorLog: SensorLog) {
    val correctSetTemp = CassandraConnection.getSetTempFor(sensorLog)
    logger.info("Send correct set temperature to sensor")
    println("Set " + correctSetTemp + " as set temperature in sensor with id: " + sensorLog.sensorId)
  }
}
