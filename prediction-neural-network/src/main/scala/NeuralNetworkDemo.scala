import breeze.linalg.DenseMatrix

/**
  * Created by Lorenz on 5/04/2016.
  */
object NeuralNetworkDemo {

  def main(args: Array[String]) {

    /**
      * Neural network
      */
    val x = DenseMatrix(
      (0.0, 0.0, 1.0),
      (0.0, 1.0, 1.0),
      (1.0, 0.0, 1.0),
      (1.0, 1.0, 1.0)
    )

    val y = DenseMatrix(
      (0.0),
      (1.0),
      (1.0),
      (0.0)
    )

    val ann = new NeuralNetwork()

    ann.train(x,y)

    val input = DenseMatrix(
      (0.0, 0.0, 1.0)
    )

    println("Prediction for:")
    println(input)
    println("prediction: " + ann.predict(input))
    println

    val ann2 = new NeuralNetwork("src/syn0", "src/syn1")
    ann2.loadSynapses()
    println("Prediction for:")
    println(input)
    println("prediction: " + ann.predict(input))
    println
    println

    /**
      * Temperature prediction
      */

    val x1 = DenseMatrix(
      (16.0, 16.0, 16.0),
      (21.0, 21.0, 21.0),
      (16.0, 18.0, 21.0),
      (21.0, 18.0, 16.0)
    )

    val y1 = DenseMatrix(
      (18.0),
      (18.0),
      (21.0),
      (16.0)
    )

    val input1 = DenseMatrix(
      (16.0, 18.0, 21.0)
    )

    val tempPred = new TemperaturePrediction

    val normX = tempPred.normalizeData(x1)
    println(normX)
    val normY = tempPred.normalizeData(y1)
    val normInput = tempPred.normalizeData(input1)

    tempPred.trainNetwork(normX,normY)

    val prediction1 = tempPred.predict(normInput)
    val denormPrediction1 = tempPred.denormalizeData(prediction1)

    println("Temperature prediction")
    println(denormPrediction1)
  }
}
