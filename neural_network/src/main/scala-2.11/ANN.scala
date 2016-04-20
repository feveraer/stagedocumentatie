import ann.{NetworkTrainer, NeuralNetwork}
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts.AxisType

/**
  * Created by Frederic on 19/04/2016.
  */
object ANN {
  private val networkTrainer: NetworkTrainer = new NetworkTrainer("src/main/resources/TrainingsSet.tsv")
  private var ann: NeuralNetwork = null
  private var data: Vector[(Seq[Long], Seq[Double])] = Vector.empty

  def main(args: Array[String]) {
    run
  }

  def run {
    networkTrainer.train
    networkTrainer.exportModel
    ann = new NeuralNetwork
    ann.loadModel("src/main/resources/network/encogNormalizationHelper.eg", "src/main/resources/network/encogBestMethod.eg")
    data = ann.predictFromCSV("src/main/resources/TrainingsSet.tsv")
    draw
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
}
