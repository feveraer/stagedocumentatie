package import_package

import java.io.File

import breeze.linalg.DenseMatrix

/**
  * Created by Lorenz on 7/04/2016.
  */
object MatrixImporter {
  def createMatrixFromFile(path: String): DenseMatrix[Double] ={
    breeze.linalg.csvread(new File(path))
  }
}
