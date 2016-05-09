import ann.Constants
import testsetann.TransformerV2

import scala.io.StdIn

/**
  * Created by Lorenz on 9/05/2016.
  */
object TestSetExporterV2 {
  def main(args: Array[String]) {
    val inputFilePath = StdIn.readLine(
      "Enter filename for input tsv data (located in " + Constants.RESOURCES_PATH + "): ")
    val entries = TransformerV2.getDataEntriesFromTSV(Constants.RESOURCES_PATH + inputFilePath)
    TransformerV2.writeDataEntriesToTSV(entries)
  }
}
