import breeze.linalg.DenseMatrix
import breeze.numerics.sigmoid

/**
  * Created by vagrant on 04.04.16.
  */
object ActivationFunction {
  def nonLin(x: DenseMatrix[Double]): DenseMatrix[Double] = {
    return nonLin(x, false)
  }

  def nonLin(x: DenseMatrix[Double], deriv: Boolean): DenseMatrix[Double] = {
    if (deriv == true){
      return sigmoidDerivative(x)
      }
    return sigmoid(x)
  }
}


