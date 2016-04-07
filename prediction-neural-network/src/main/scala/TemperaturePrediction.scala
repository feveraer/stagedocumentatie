import breeze.linalg.DenseMatrix

/**
  * Created by Lorenz on 5/04/2016.
  */
class TemperaturePrediction {
  private val MIN_TEMPERATURE = -100
  private val MAX_TEMPERATURE = 150

  private var ann: NeuralNetwork = new NeuralNetwork()

  def loadNetworkWithSynapses(pathToSyn0: String, pathToSyn1: String): Unit = {
    ann = new NeuralNetwork(pathToSyn0, pathToSyn1)
  }

  def trainNetwork(x: DenseMatrix[Double], y: DenseMatrix[Double]): Unit = {
    ann.trainNetwork(x, y)
  }

  def trainNetworkFurther(x: DenseMatrix[Double], y: DenseMatrix[Double], pathToSyn0: String, pathToSyn1: String): Unit = {
    ann.trainNetworkFurther(x, y, pathToSyn0, pathToSyn1)
  }

  def predict(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    ann.predict(x)
  }

  def normalizeData(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    x mapValues { x => (x - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE) }
  }

  def denormalizeData(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    x mapValues {
      _ * (MAX_TEMPERATURE - MIN_TEMPERATURE) + MIN_TEMPERATURE
    }
  }

  def exportSynapses(): Unit ={
    ann.exportSynapses()
  }

}
