import ann.{NetworkTrainer, NeuralNetwork}

/**
  * Created by Frederic on 19/04/2016.
  */
object ANN {
  private val networkTrainer: NetworkTrainer = new NetworkTrainer("src/main/resources/TrainingsSet.tsv")
  private var ann: NeuralNetwork = null

  def main(args: Array[String]) {
    run
  }

  def run {
    networkTrainer.train
    networkTrainer.exportModel
    ann = new NeuralNetwork
    ann.loadModel("src/main/resources/network/encogNormalizationHelper.eg", "src/main/resources/network/encogBestMethod.eg")
    ann.predictFromCSV("src/main/resources/TrainingsSet.tsv")
  }

  def draw {
  }
}
