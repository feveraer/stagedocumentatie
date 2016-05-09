package simulator

import java.io.{File, FileInputStream, ObjectInputStream}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import ann.{Constants, NeuralNetwork, NeuralNetworkV2}
import cassandra.SensorLog
import connections.SSHTunnel
import helpers.Helpers.{Quartile, Season}
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper
import org.encog.persist.EncogDirectoryPersistence
import simulator.cassandraTestPackage.CassandraTestConnection
import time.DateTime

/**
  * Created by Frederic on 4/05/2016.
  */
object TestSmoothing {
  private val sensorID = 0
  private val ann = new NeuralNetworkV2

  //    val (normalizationhelper, model) = CassandraTestConnection.getANNModelsForOutput(sensorID)
  val normalizationhelper = getNormHelper()
  val model = getModel()

  def main(args: Array[String]) {
    SSHTunnel.connect("root", "Ugent2015")
    CassandraTestConnection.connect()

    println("Starting")

    ann.loadModel(normalizationhelper, model)

    //    withAdjustments
    withoutAdjustments

    CassandraTestConnection.close()
    SSHTunnel.disconnect()
  }

  private def withoutAdjustments: Unit = {
    val logs = CassandraTestConnection.getAllLogsForSensor(0)
    var currentLog = logs(0)
    var counter = 1

    while (counter < 10) {
      println("Start iteration: " + counter)
      val prediction = ann.predictFromDataSequence(logs.slice(counter-1, counter+3))
      val lastLog = logs(counter+2)
      println("Date: " + lastLog.date + "\tTime: " + lastLog.time + "\tPrediction in " +
        Constants.DIFF_TO_PREDICTION_MINUTES + ": " + prediction)
      println("Measured: " + currentLog.measuredTemp + "\tSet: " + currentLog.setTemp)
      currentLog = logs(counter)
      counter += 1
    }
  }

  private def withAdjustments(): Unit = {
    var currentLog = CassandraTestConnection.getMostRecentTemperatureEntries(sensorID, 1)(0)
    var counter = 1

    while (currentLog.date < "2015-03-20") {
      println("Start iteration: " + counter)
      println("Date: " + currentLog.date + "\tTime: " + currentLog.time)
      if (currentLog.measuredTemp >= currentLog.setTemp) {
        coolDown(currentLog)
      } else {
        warmUp(currentLog)
      }
      currentLog = CassandraTestConnection.getMostRecentTemperatureEntries(sensorID, 1)(0)
      counter += 1
    }
  }

  private def warmUp(sensorLog: SensorLog): Unit = {
    var prediction = ann.predict(sensorLog)
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
      writeToCassandra(adjustedSensorLog)
    }
    writePredictionLog(sensorLog, prediction)
  }

  private def coolDown(sensorLog: SensorLog): Unit = {
    val correctSetTemp = CassandraTestConnection.getSetTempFor(sensorLog)
    val adjustedSensorLog = new SensorLog(
      sensorLog.sensorId,
      sensorLog.date,
      sensorLog.time,
      sensorLog.regime,
      sensorLog.measuredTemp,
      correctSetTemp
    )
    writeToCassandra(adjustedSensorLog)
    val prediction = ann.predict(adjustedSensorLog)
    writePredictionLog(adjustedSensorLog, prediction)
  }

  private def writePredictionLog(log: SensorLog, prediction: Double): Unit = {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val predictedDateTime = LocalDateTime.parse(log.date + " " + log.time, dateTimeFormatter)

    val season = Season.getSeason(predictedDateTime.toLocalDate)
    val quartile = Quartile.getQuartile(predictedDateTime.toLocalTime)

    val predictedSetTemp = CassandraTestConnection.getSetTempFor(
      sensorID,
      season,
      predictedDateTime.toLocalDate.getDayOfWeek.toString,
      predictedDateTime.toLocalTime.getHour,
      quartile
    )

    val predictionSensorLog =
      new SensorLog(
        sensorID,
        predictedDateTime.toLocalDate.toString,
        predictedDateTime.toLocalTime.toString + ".000",
        "SOME_REGIME",
        prediction,
        predictedSetTemp
      )

    writeToCassandra(predictionSensorLog)
  }

  private def writeToCassandra(log: SensorLog): Unit = {
    CassandraTestConnection.insertSensorLog(log)
  }

  private def getNormHelper(): NormalizationHelper = {
    val pathToNormalizationHelper = Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH

    val fin: FileInputStream = new FileInputStream(pathToNormalizationHelper)
    val ois: ObjectInputStream = new ObjectInputStream(fin)
    val helper = ois.readObject.asInstanceOf[NormalizationHelper]
    ois.close
    helper
  }

  private def getModel(): MLRegression = {
    val pathToModel = Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH
    EncogDirectoryPersistence.loadObject(new File(pathToModel)).asInstanceOf[MLRegression]
  }


}
