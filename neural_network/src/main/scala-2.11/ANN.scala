import java.nio.file.{Files, Paths}

import ann.{Constants, NetworkTrainer, NeuralNetwork}
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts.AxisType
import connections.Connections
import org.apache.commons.io.FileExistsException

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
    if (StdIn.readLine("Train network? (y or n): ").startsWith("y")) {
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
    // predictFromCsv
    // draw
    // style
    // stopWisp
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

    println("Prediction test for next temperature")
    // Make connection with Mesos and Cassandra
    Connections.connect()
    val output = ann.predict(206252, null)
    Connections.close()
    println("Output: " + output)
  }

  def predictFromCsv {
    ann = new NeuralNetwork
    ann.loadModel(
      Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH,
      Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH)
    if (!shouldTrain) {
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
    }
    data = ann.predictFromCSV(Constants.RESOURCES_PATH + trainingSetFileName)
  }

  def draw {
    try {
      // Predicted values
      line(data(0)._1.zip(data(0)._2))
      hold()
      // Measured values
      line(data(1)._1.zip(data(1)._2))
      hold()
      // Set values
      line(data(2)._1.zip(data(2)._2))
    } catch {
      case ex: FileExistsException =>
      // Wisp throws this inadvertently, can be safely ignored.
    }
  }

  def style {
    try {
      title("Temperature prediction for " + trainingSetFileName)
      xAxisType(AxisType.datetime)
      xAxis("Time")
      yAxis("Temperature in °C")
      legend(Seq("Predicted", "Measured", "Set"))
    } catch {
      case ex: FileExistsException =>
      // Wisp throws this inadvertently, can be safely ignored.
    }
  }

  def stopWisp {
    println("Enter one of the following options:")
    println("  - restyle (try a page refresh first)")
    println("  - stop")
    val input = StdIn.readLine("> ")
    input match {
      case "restyle" => style; stopWisp
      case "stop" => stopWispServer
      case default => stopWispServer
    }
    stopWispServer
  }
}
