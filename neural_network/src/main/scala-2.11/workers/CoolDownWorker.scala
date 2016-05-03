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
      // TODO: Correct set temperature of sensorLog
      context.stop(self)
    }
  }

  private def writePredictionToCassandra(sensorLog: SensorLog, prediction: Double) {
    val predictedDateTime = new DateTime(sensorLog.date, sensorLog.time).plus(Constants.DIFF_TO_PREDICTION)
    // Write prediction output to Cassandra.
    CassandraConnection.insertSensorPrediction(new SensorPrediction(
      sensorLog.sensorId,
      predictedDateTime.date.toString,
      predictedDateTime.time.toString,
      prediction
    ))
    logger.info("Wrote prediction to Cassandra.")
  }
}
