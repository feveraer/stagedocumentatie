package ann

import java.io.{File, FileInputStream, ObjectInputStream}
import java.util.Date

import cassandra.SensorLog
import org.encog.ml.MLRegression
import org.encog.ml.data.MLData
import org.encog.ml.data.versatile.NormalizationHelper
import org.encog.persist.EncogDirectoryPersistence
import org.encog.util.arrayutil.VectorWindow
import org.encog.util.csv.ReadCSV
import testsetann.{DateTime, DateTimeDifference}

/**
  * Created by Lorenz on 19/04/2016.
  */
class NeuralNetwork {
  private val numberOfColumns: Int = 6
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

  // Predict next temperature from Cassandra
  // Table header for sensor_logs:
  // outputid - date - time - measuredtemperature - regime - settemperature
  // 346922   - dd/mm/yyyy - hh:mm:ss.ms - 21.5 - Comfort - 22.0

  def predict(log: SensorLog): Double = {
    // take last x values from Cassandra where x = Constants.WINDOW_SIZE

    // make list of those values + current SensorLog

    // predict...

    -1
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
        val datetime: Long = startDate + new DateTimeDifference(
          csv.get(2).toInt, csv.get(3).toInt, csv.get(4).toInt, csv.get(5).toInt
        ).toMillis()
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

    outputVector :+= (times, predictedValues)
    outputVector :+= (times, measuredValues)
    outputVector :+= (times, setValues)
    outputVector
  }
}
