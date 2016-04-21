import ann.Constants
import testsetann.Transformer

import scala.io.StdIn

/**
  * Created by Lorenz on 13/04/2016.
  */
object TestSetExporter {
  def main(args: Array[String]) {
    val inputFilePath = StdIn.readLine(
      "Enter filename for input tsv data (located in " + Constants.RESOURCES_PATH + "): ")
    val entries = Transformer.getDataEntriesFromTSV(Constants.RESOURCES_PATH + inputFilePath)
    Transformer.writeDataEntriesToTSV(entries)
  }
}
