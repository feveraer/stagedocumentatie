import java.io.File

import breeze.linalg.DenseMatrix
import function_helpers.ActivationFunction

/**
  * Created by Lorenz on 5/04/2016.
  */
class NeuralNetwork(pathToSyn0: String, pathToSyn1: String) {
  private val TRAINING_ITERATIONS = 10000

  /**
    * Synapse 0 and synapse 1 in the network
    */
  var syn0: DenseMatrix[Double] = null
  var syn1: DenseMatrix[Double] = null

  /**
    *
    * @return default constructor
    */
  def this() = this(null, null)

  /**
    * Function that trains the neural network with given input sets
    *
    * @param x : the input set mapped as a value between 0 and 1
    * @param y : the expected output set mapped as a value between 0 and 1
    */
  def train(x: DenseMatrix[Double], y: DenseMatrix[Double]): Unit = {
    /**
      * Initialize the weight matrices
      */
    syn0 = DenseMatrix.rand(x.cols, x.rows)
    syn1 = DenseMatrix.rand(y.rows, y.cols)

    /**
      * Train the network
      */
    for (i <- 0 to TRAINING_ITERATIONS) {

      // Feed forward
      val l0 = x
      val l1 = ActivationFunction.nonLin(l0 * syn0)
      val l2 = ActivationFunction.nonLin(l1 * syn1)

      // error in layer 2
      val l2_error = y - l2

      // delta in layer 2
      val l2_delta = l2_error :* ActivationFunction.nonLin(l2, true)

      // error in layer 1
      val l1_error = l2_delta * (syn1.t)


      // delta in layer 1
      val l1_delta = l1_error :* ActivationFunction.nonLin(l1, true)

      // update weight matrices
      syn1 += l1.t * l2_delta
      syn0 += l0.t * l1_delta
    }
  }

  /**
    *
    * @param x The input matrix
    * @return returns the a prediction based on the input matrix
    */
  def predict(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    if (!areSynapsesLoaded()) {
      throw new RuntimeException("No synapses provided")
    }

    val l0 = x
    val l1 = ActivationFunction.nonLin(l0 * syn0)
    val l2 = ActivationFunction.nonLin(l1 * syn1)
    return l2
  }

  def exportSynapses(): Unit = {
    if (!areSynapsesLoaded()) {
      throw new RuntimeException("No synapses provided")
    }

    breeze.linalg.csvwrite(new File("src/syn0"), syn0)
    breeze.linalg.csvwrite(new File("src/syn1"), syn1)
  }

  def loadSynapses(): Unit ={
    syn0 = breeze.linalg.csvread(new File(pathToSyn0))
    syn1 = breeze.linalg.csvread(new File(pathToSyn1))
  }

  def loadSynapses(pathToSyn0: String, pathToSyn1: String): Unit ={
    syn0 = breeze.linalg.csvread(new File(pathToSyn0))
    syn1 = breeze.linalg.csvread(new File(pathToSyn1))
  }

  private def areSynapsesLoaded(): Boolean = {
    return syn0 != null && syn1 != null
  }
}
