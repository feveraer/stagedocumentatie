package workers

import akka.actor.Actor
import ann.Constants
import cassandra.{CassandraConnection, SensorLog, SensorPrediction}
import org.apache.log4j.Logger
import testsetann.DateTime

/**
  * Created by Frederic on 3/05/2016.
  */
class CoolDownWorker extends Actor {
  private val logger = Logger.getLogger("CoolDownWorker")

  override def receive = {
    case sensorLog: SensorLog => {
      // TODO: Correct set temperature of sensorLog
      context.stop(self)
    }
  }

  private def correctSetTemp {

  }
}
