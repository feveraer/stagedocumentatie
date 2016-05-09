package ann

import java.io.{File, FileInputStream, ObjectInputStream}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import java.util.Date

import cassandra.{CassandraConnection, SensorLog}
import org.encog.ml.MLRegression
import org.encog.ml.data.MLData
import org.encog.ml.data.versatile.NormalizationHelper
import org.encog.persist.EncogDirectoryPersistence
import org.encog.util.arrayutil.VectorWindow
import org.encog.util.csv.ReadCSV
import simulator.cassandraTestPackage.CassandraTestConnection
import time.{DateTime, DateTimeDifference, TimeHelper}

/**
  * Created by Lorenz on 19/04/2016.
  */
class NeuralNetworkV2 {
  // number of columns in training_set - 1
  // wished output doesn't count as input
  private val numberOfColumns: Int = 3
  private var helper: NormalizationHelper = null
  private var bestMethod: MLRegression = null

  def loadModel(pathToNormalizationHelper: String, pathToBestModel: String) {
    try {
      val fin: FileInputStream = new FileInputStream(pathToNormalizationHelper)
      val ois: ObjectInputStream = new ObjectInputStream(fin)
      helper = ois.readObject.asInstanceOf[NormalizationHelper]
      ois.close
    }
    catch {
      case e: Any => {
        e.printStackTrace
      }
    }
    bestMethod = EncogDirectoryPersistence.loadObject(new File(pathToBestModel)).asInstanceOf[MLRegression]
  }

  def loadModel(normalizer: NormalizationHelper, mLRegression: MLRegression): Unit = {
    helper = normalizer
    bestMethod = mLRegression
  }

  // Predict next temperature for SensorLogs from Cassandra with a specific output id.
  // Table header for sensor_logs:
  // outputid - date - time - measuredtemperature - regime - settemperature
  // 346922   - yyyy-MM-dd - HH:mm:ss.SSS - 21.5 - Comfort - 22.0

  def predict(currentLog: SensorLog): Double = {
    // Initialize empty prediction output.
    var output: Double = 0.0

    val sensorLogs = fetchSensorLogs(currentLog)

    val convertedLogs = formatSensorLogs(sensorLogs)

    // Create empty arrays for later usage.
    // This will be needed to store the columns of a row.
    val line: Array[String] = new Array[String](convertedLogs(0).size)
    val slice: Array[Double] = new Array[Double](convertedLogs(0).size)

    // Create a vector to hold each time−slice , as we build them.
    // These will be grouped together into windows.
    val window: VectorWindow = new VectorWindow(Constants.WINDOW_SIZE)
    val input: MLData = helper.allocateInputVector(Constants.WINDOW_SIZE)

    convertedLogs.foreach(log => {
      for (i <- log.indices) {
        line(i) = log(i).toString
      }

      // Normalize the input.
      // input array - output array - shuffle
      // Never shuffle time series.
      helper.normalizeInputVector(line, slice, false)

      // Check if window is ready and there is enough data to build a full window.
      if (window.isReady) {
        // Copy the window.
        // data - start position
        window.copyWindow(input.getData, 0)

        // Compute prediction
        val prediction: MLData = bestMethod.compute(input)

        // Denormalize prediction, save as output.
        output = helper.denormalizeOutputVectorToString(prediction)(0).toDouble
      }

      // Add data to window.
      window.add(slice)
    })

    output
  }

  private def fetchSensorLogs(currentLog: SensorLog): Vector[SensorLog] = {
    // Take last x values from Cassandra where x = Constants.WINDOW_SIZE.
    var sensorLogs = CassandraTestConnection.getMostRecentTemperatureEntries(
      currentLog.sensorId, Constants.WINDOW_SIZE + 1)
    // Only add currentLog if it's not yet stored in Cassandra
    if (sensorLogs.last != currentLog) {
      sensorLogs = sensorLogs.slice(1, sensorLogs.size)
      sensorLogs :+= currentLog
    }

    // If the current log is a modified version of the last log,
    // then replace the last log with this modified version.
    // Occurs when a second prediction is needed.
    if (sensorLogs.last.isSetTempModified(currentLog)) {
      sensorLogs = sensorLogs.slice(0, sensorLogs.size - 1)
      sensorLogs :+= currentLog
    }
    sensorLogs
  }

  // Format SensorLogs:
  // TimeDiff - MeasuredTemp - SetTemp
  private def formatSensorLogs(logs: Vector[SensorLog]): Vector[Vector[Double]] = {
    var output: Vector[Vector[Double]] = Vector.empty
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    for (i <- logs.indices) {

      val dateTime = LocalDateTime.parse(logs(i).date + " " + logs(i).time, dateTimeFormatter)


      var diffToNext: Long = 0
      if (i < logs.size - 1) {
        val dateTimeNext = LocalDateTime.parse(logs(i + 1).date + " " + logs(i + 1).time, dateTimeFormatter)
        diffToNext = TimeHelper.differenceBetweenInMinutes(dateTime, dateTimeNext)
      } else {
        diffToNext = Constants.DIFF_TO_PREDICTION_MINUTES
      }
      output :+= Vector(
        diffToNext, logs(i).measuredTemp, logs(i).setTemp
      )
    }
    output
  }

  def predictFromCSV(filename: String): Vector[(Seq[Long], Seq[Double])] = {
    // Initialize empty Vector for output:
    // Time series data - predicted or expected values
    var outputVector: Vector[(Seq[Long], Seq[Double])] = Vector.empty
    // time series sequence
    var times: Seq[Long] = Seq.empty
    // start date for time series
    var startDate = new Date().getTime
    // predicted sequence
    var predictedValues: Seq[Double] = Seq.empty
    // set sequence
    var setValues: Seq[Double] = Seq.empty
    // measured sequence
    var measuredValues: Seq[Double] = Seq.empty

    // Iterator for csv data:
    // filename - headers - format
    val csv: ReadCSV = new ReadCSV(filename, true, Constants.FORMAT)

    // Create empty arrays for later usage.
    // This will be needed to store the columns of a row in the csv.
    val line: Array[String] = new Array[String](numberOfColumns)
    val slice: Array[Double] = new Array[Double](numberOfColumns)

    // Create a vector to hold each time−slice , as we build them.
    // These will be grouped together into windows.
    val window: VectorWindow = new VectorWindow(Constants.WINDOW_SIZE + 1)
    val input: MLData = helper.allocateInputVector(Constants.WINDOW_SIZE + 1)

    // Only take the first x to predict
    var stopAfter: Int = 1000

    while (csv.next && stopAfter > 0) {

      // parse the csv row to an array
      for (i <- 0 until numberOfColumns) {
        line(i) = csv.get(i)
      }

      // Normalize the input.
      // input array - output array - shuffle
      // Never shuffle time series.
      helper.normalizeInputVector(line, slice, false)

      // Check if window is ready and there is enough data to build a full window.
      if (window.isReady) {
        // Copy the window.
        // data - start position
        window.copyWindow(input.getData, 0)

        // DateTime in ms based on time difference
        val datetime: Long = startDate + TimeHelper.differenceInMinutesToMillis(csv.get(2).toLong)
        startDate = datetime
        // NextMeasured output.
        val measuredValue: Double = csv.get(numberOfColumns).toDouble
        // Set value
        val setValue: Double = csv.get(0).toDouble

        // Predict output.
        val output: MLData = bestMethod.compute(input)

        // Denormalize prediction.
        val predicted: Double = helper.denormalizeOutputVectorToString(output)(0).toDouble

        times :+= datetime
        predictedValues :+= predicted
        measuredValues :+= measuredValue
        setValues :+= setValue
      }

      // Add data to windows.
      window.add(slice)
      stopAfter -= 1
    }

    // Correct set values sequence by prepending default value to the sequence
    // and thus moving every value one over to the right. Discard the last value.
    setValues +:= 21.0
    setValues = setValues.slice(0, setValues.size - 1)

    outputVector :+=(times, predictedValues)
    outputVector :+=(times, measuredValues)
    outputVector :+=(times, setValues)
    outputVector
  }

  def predictFromDataSequence(seq: Vector[SensorLog]): Double = {
    var output: Double = 0.0

    val line: Array[String] = new Array[String](numberOfColumns)
    val slice: Array[Double] = new Array[Double](numberOfColumns)
    val window: VectorWindow = new VectorWindow(Constants.WINDOW_SIZE + 1)
    val input: MLData = helper.allocateInputVector(Constants.WINDOW_SIZE + 1)

    val convertedLogs = formatSensorLogs(seq)

    convertedLogs.foreach(log => {
      for (i <- log.indices) {
        line(i) = log(i).toString
      }

      // Normalize the input.
      // input array - output array - shuffle
      // Never shuffle time series.
      helper.normalizeInputVector(line, slice, false)

      // Check if window is ready and there is enough data to build a full window.
      if (window.isReady) {
        // Copy the window.
        // data - start position
        window.copyWindow(input.getData, 0)

        // Compute prediction
        val prediction: MLData = bestMethod.compute(input)

        // Denormalize prediction, save as output.
        output = helper.denormalizeOutputVectorToString(prediction)(0).toDouble
      }

      // Add data to window.
      window.add(slice)
    })

    output
  }
}
