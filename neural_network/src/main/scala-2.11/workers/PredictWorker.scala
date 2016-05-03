package workers

import akka.actor.Actor
import ann.{Constants, NeuralNetwork}
import cassandra.{CassandraConnection, SensorLog, SensorPrediction}
import org.apache.log4j.Logger
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper
import testsetann.DateTime

/**
  * Created by Frederic on 2/05/2016.
  */
class PredictWorker extends Actor{
  private val logger = Logger.getLogger("PredictWorker")

  override def receive = {
    case sensorLog: SensorLog => {
      predict(sensorLog)
      context.stop(self)
    }
  }

  private def predict(sensorLog: SensorLog) {
    val ann = new NeuralNetwork
    var model: (NormalizationHelper, MLRegression) = null
    try {
      model = CassandraConnection.getANNModelsForOutput(sensorLog.sensorId)
    } catch {
      case e: RuntimeException => {
        model = CassandraConnection.getANNModelsForOutput(Constants.DEFAULT_USER_ID)
      }
    }
    ann.loadModel(model._1, model._2)
    logger.debug("Prediction test for next temperature")
    logger.debug("Current log: " + sensorLog.toString)
    var prediction = ann.predict(sensorLog)
    logger.info("Prediction: " + prediction)
    if (prediction > sensorLog.setTemp) {
      // If our prediction is higher than the desired temperature, set the set temperature
      // equal to the measured temperature.
      // Next perform another prediction using the modified SensorLog.
      val adjustedSensorLog = new SensorLog(
        sensorLog.sensorId,
        sensorLog.date,
        sensorLog.time,
        sensorLog.regime,
        sensorLog.measuredTemp,
        sensorLog.measuredTemp
      )
      prediction = ann.predict(adjustedSensorLog)
      logger.debug("Adjusted log: " + adjustedSensorLog.toString)
      logger.info("Prediction 2: " + prediction)
    }

    logger.info("Write log to Cassandra")
    writePredictionToCassandra(sensorLog, prediction)
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
