package ann

import java.io.{File, FileInputStream, ObjectInputStream}

import org.encog.ml.MLRegression
import org.encog.ml.data.MLData
import org.encog.ml.data.versatile.NormalizationHelper
import org.encog.persist.EncogDirectoryPersistence
import org.encog.util.arrayutil.VectorWindow
import org.encog.util.csv.ReadCSV

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

  def predictFromCSV(filename: String) {
    // Iterator for csv data:
    // filename - headers - format
    val csv: ReadCSV = new ReadCSV(filename, true, EncogConstantsOld.FORMAT)

    // Create empty arrays for later usage.
    // This will be needed to store the columns of a row in the csv.
    val line: Array[String] = new Array[String](numberOfColumns)
    val slice: Array[Double] = new Array[Double](numberOfColumns)

    // Create a vector to hold each time−slice , as we build them.
    // These will be grouped together into windows.
    val window: VectorWindow = new VectorWindow(EncogConstantsOld.WINDOW_SIZE + 1)
    val input: MLData = helper.allocateInputVector(EncogConstantsOld.WINDOW_SIZE + 1)

    // Only take the first 100 to predict.
    var stopAfter: Int = 100

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
        val result: StringBuilder = new StringBuilder

        // Copy the window.
        // data - start position
        window.copyWindow(input.getData, 0)
        val correct: String = csv.get(numberOfColumns)

        // Predict output.
        val output: MLData = bestMethod.compute(input)

        // Denormalize prediction.
        val predicted: String = helper.denormalizeOutputVectorToString(output)(0)
        result.append("Predicted:\t" + predicted + "\n")
        result.append("Correct:\t" + correct + "\n")
        result.append("\n")
        System.out.println(result.toString)
      }

      // Add data to windows.
      window.add(slice)
      stopAfter -= 1
    }
  }
}
