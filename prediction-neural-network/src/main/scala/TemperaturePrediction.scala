import breeze.linalg.DenseMatrix

/**
  * Created by Lorenz on 5/04/2016.
  */
class TemperaturePrediction {
  private val ANN: NeuralNetwork = new NeuralNetwork()
  private val MIN_TEMPERATURE = -50
  private val MAX_TEMPERATURE = 150


  def trainNetwork(x: DenseMatrix[Double], y: DenseMatrix[Double]): Unit ={
    ANN.train(x,y)
  }

  def predict(x: DenseMatrix[Double]): DenseMatrix[Double] ={
    ANN.predict(x)
  }

  def normalizeData(x:DenseMatrix[Double]): DenseMatrix[Double] = {
    x mapValues { x => (x - MIN_TEMPERATURE)/(MAX_TEMPERATURE - MIN_TEMPERATURE)}
  }

  def denormalizeData(x:DenseMatrix[Double]): DenseMatrix[Double] = {
    x mapValues {_ * (MAX_TEMPERATURE - MIN_TEMPERATURE) + MIN_TEMPERATURE}
  }

}