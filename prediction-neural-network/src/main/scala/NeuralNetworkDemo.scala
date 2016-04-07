import breeze.linalg.DenseMatrix
import import_package.MatrixImporter

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

    ann.trainNetwork(x, y)

    val input = DenseMatrix(
      (0.0, 0.0, 1.0)
    )

    println("Prediction for:")
    println(input)
    println("prediction: " + ann.predict(input))
    println

    val ann2 = new NeuralNetwork("src/main/resources/synapsesTest/syn0-test", "src/main/resources/synapsesTest/syn1-test")
    ann2.loadSynapses()
    println("Prediction for:")
    println(input)
    println("prediction: " + ann.predict(input))
    println
    println

    /**
      * Temperature prediction
      */
    // training
    val x1 = MatrixImporter.createMatrixFromFile("src/main/resources/valuesSet.txt")
    val y1 = MatrixImporter.createMatrixFromFile("src/main/resources/ExpectedSet.txt")
    val input1 = MatrixImporter.createMatrixFromFile("src/main/resources/input")

//    val x1 = DenseMatrix(
//      (16.0, 16.0, 16.0),
//      (21.0, 21.0, 21.0),
//      (16.0, 18.0, 21.0),
//      (21.0, 18.0, 16.0)
//    )
//
//    val y1 = DenseMatrix(
//      (18.0),
//      (18.0),
//      (21.0),
//      (16.0)
//    )
//
//    val input1 = DenseMatrix(
//      (16.0, 18.0, 21.0)
//    )

    val tempPred = new TemperaturePrediction

    println("Start normalizing")
    val normX = tempPred.normalizeData(x1)
    val normY = tempPred.normalizeData(y1)
    val normInput = tempPred.normalizeData(input1)

    println("Start training")
    tempPred.trainNetwork(normX, normY)

    println("Export Synapses")
    tempPred.exportSynapses()

    println("Calculate Prediction")
    val prediction1 = tempPred.predict(normInput)

    println("Denormalize")
    val denormPrediction1 = tempPred.denormalizeData(prediction1)

    println
    println("Temperature prediction")
    println(denormPrediction1)
  }

  val array = Array(20.0, 30.0)
  val matrix = DenseMatrix(array)
}
