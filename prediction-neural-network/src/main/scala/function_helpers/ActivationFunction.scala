package function_helpers

import breeze.linalg.DenseMatrix
import breeze.numerics.sigmoid

/**
  * Created by Lorenz on 5/04/2016.
  */

/**
  * This is the activation function for the neurons in the neural network.
  * In this case we use the sigmoid function which maps every value between 0 and 1
  */
object ActivationFunction {
  /**
    *
    * @param x A matrix of which all values have to been mapped between 0 and 1
    * @return This function maps the all the values in the matrix to a value between 0 and 1
    */
  def nonLin(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    return nonLin(x, false)
  }

  /**
    *
    * @param x The input matrix
    * @param deriv Boolean that indicates if the sigmoid function or its derivative should be applied
    * @return This function maps all the values in the matrix to a new value using the sigmoid function or its derivative.
    */
  def nonLin(x: DenseMatrix[Double], deriv: Boolean): DenseMatrix[Double] = {
    if (deriv == true){
      return sigmoidDerivative(x)
      }
    return sigmoid(x)
  }
}


