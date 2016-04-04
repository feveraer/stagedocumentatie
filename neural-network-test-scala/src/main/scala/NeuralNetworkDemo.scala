import breeze.linalg.DenseMatrix

/**
  * Created by vagrant on 04.04.16.
  */
object NeuralNetworkDemo {

  def main(args: Array[String]) {
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

    val syn0 = DenseMatrix.rand(3, 4)
    val syn1 = DenseMatrix.rand(4, 1)

    for( i <- 0 to 1000){

      // Feed forward
      val l0 = x
      println ("l0")
      println (l0)
      val l1 = ActivationFunction.nonLin(l0 * syn0)
      println ("l1")
      println(l1)

      val l2 = ActivationFunction.nonLin(l1 * syn1)
      println ("l2")
      println (l2)

      // error in layer 2
      val l2_error = y - l2
      println ("l2_error")
      println (l2_error)

      // delta in layer 2
      val l2_delta = l2_error :* ActivationFunction.nonLin(l2, true)
      println ("l2_delta")
      println (l2_delta)

      // error in layer 1
      val l1_error = l2_delta * (syn1.t)
      println ("l1_error")
      println (l1_error)


      // delta in layer 1
      val l1_delta = l1_error :* ActivationFunction.nonLin(l1, true)

     syn1 += l1.t * l2_delta
     syn0 += l0.t * l1_delta

      println("Output After Training " + i + ":")
      println(l2)
    }


  }
}
