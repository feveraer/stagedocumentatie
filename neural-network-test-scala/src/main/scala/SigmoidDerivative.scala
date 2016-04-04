import breeze.generic.{MappingUFunc, UFunc}

/**
  * Created by vagrant on 04.04.16.
  */
object sigmoidDerivative extends UFunc with MappingUFunc {
  implicit object sigmoidDerivativeImplInt extends Impl[Int, Double] {
    def apply(x:Int) = x*(1-x)
  }
  implicit object sigmoidDerivativeImplDouble extends Impl[Double, Double] {
    def apply(x:Double) = x*(1-x)
  }

  implicit object sigmoidDerivativeImplFloat extends Impl[Float, Float] {
    def apply(x:Float) = x*(1-x)
  }
}
