import java.nio.file.{Files, Paths}

import ann.{Constants, NetworkTrainer, NeuralNetwork}
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts.AxisType

import scala.io.StdIn

/**
  * Created by Frederic on 19/04/2016.
  */
object ANN {
  private var networkTrainer: NetworkTrainer = null
  private var shouldTrain = false
  private var trainingSetFileName: String = null
  private var ann: NeuralNetwork = null
  private var data: Vector[(Seq[Long], Seq[Double])] = Vector.empty

  def main(args: Array[String]) {
    if (StdIn.readLine("Train network? (y or n)").startsWith("y")) {
      shouldTrain = true
      var correctName = false
      do {
        trainingSetFileName = StdIn.readLine(
          "Enter training set file name (located in " + Constants.RESOURCES_PATH + "): ")
        if (Files.exists(Paths.get(Constants.RESOURCES_PATH + trainingSetFileName))) {
          correctName = true
        } else {
          println("File " + Constants.RESOURCES_PATH + trainingSetFileName + " not found. Try again.")
        }
      } while (!correctName)
      trainNetwork
    }
    predict
    draw
    stopWisp
  }

  def trainNetwork {
    networkTrainer = new NetworkTrainer(Constants.RESOURCES_PATH + trainingSetFileName)
    networkTrainer.train
    networkTrainer.exportModel
  }

  def predict {
    ann = new NeuralNetwork
    ann.loadModel(
      Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH,
      Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH)
    if (!shouldTrain) {
      trainingSetFileName = StdIn.readLine("Enter file name for training set: ")
    }
    data = ann.predictFromCSV(Constants.RESOURCES_PATH + trainingSetFileName)
  }

  def draw {
    // Expected data
    line(data(0)._1.zip(data(0)._2))
    hold()
    // Predicted data
    line(data(1)._1.zip(data(1)._2))
    title("Neural network test")
    xAxisType(AxisType.datetime)
    xAxis("Time")
    yAxis("Temperature in °C")
    legend(Seq("Expected", "Predicted"))
  }

  def stopWisp {
    StdIn.readLine("Press enter to stop Wisp: ")
    stopWispServer
  }
}
