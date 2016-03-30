import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by Frederic on 30/03/2016.
  */
object QbusReader {

  val baseDir = "d:\\Qbus\\"
  val locations = "Locations.csv"
  val controllers = "Controllers.csv"
  val outputGraphHourData = "OutputGraphHourData.csv"
  val outputLogs = "OutputLogs.csv"
  val outputs = "Outputs.csv"
  val types = "Types.csv"
}

class QbusReader(private val sqlContext: SQLContext) {

  def read(file: String): DataFrame = {
    val df = sqlContext.read
      .option("delimiter", ";")
      .format("com.databricks.spark.csv")
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load(QbusReader.baseDir + file)
    df
  }
}
