package ann

import java.io.{File, FileInputStream, ObjectInputStream}
import java.util.Date

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
    // expected sequence
    var expectedValues: Seq[Double] = Seq.empty

    // Iterator for csv data:
    // filename - headers - format
    val csv: ReadCSV = new ReadCSV(filename, true, EncogConstants.FORMAT)

    // Create empty arrays for later usage.
    // This will be needed to store the columns of a row in the csv.
    val line: Array[String] = new Array[String](numberOfColumns)
    val slice: Array[Double] = new Array[Double](numberOfColumns)

    // Create a vector to hold each time−slice , as we build them.
    // These will be grouped together into windows.
    val window: VectorWindow = new VectorWindow(EncogConstants.WINDOW_SIZE + 1)
    val input: MLData = helper.allocateInputVector(EncogConstants.WINDOW_SIZE + 1)

    // Only take the first 100 to predict.
    // var stopAfter: Int = 100

    while (csv.next) {

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
        // Expected output.
        val correct: Double = csv.get(numberOfColumns).toDouble

        // Predict output.
        val output: MLData = bestMethod.compute(input)

        // Denormalize prediction.
        val predicted: Double = helper.denormalizeOutputVectorToString(output)(0).toDouble

        times = times :+ datetime
        predictedValues = predictedValues :+ predicted
        expectedValues = expectedValues :+ correct
      }

      // Add data to windows.
      window.add(slice)
      // stopAfter -= 1
    }

    outputVector = outputVector :+ (times, expectedValues)
    outputVector = outputVector :+ (times, predictedValues)
    outputVector
  }
}
