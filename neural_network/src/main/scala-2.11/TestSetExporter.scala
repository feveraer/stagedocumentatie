import testsetann.Transformer

/**
  * Created by Lorenz on 13/04/2016.
  */
object TestSetExporter {
  def main(args: Array[String]) {
    val entries = Transformer.tsvToDataEntries("src/main/resources/input_eetkamer_LaMa.TSV")
    Transformer.DataEntryVectorToTSV(entries)
  }
}
