import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by Frederic on 30/03/2016.
  */
object QbusReader {

  // on Windows:
  // val baseDir = "d:\\Qbus\\"
  // on Linux:
  val baseDir = "/media/frederic/Data1/Qbus/"

  val locations = "Locations.csv"
  val locationsSchema = StructType(Array(
    StructField("Id", StringType),
    StructField("Name", StringType),
    StructField("Userid", StringType),
    StructField("OriginalID", StringType),
    StructField("ControllerID", StringType),
    StructField("ParentID", StringType)
  ))

  val controllers = "Controllers.csv"
  val controllersSchema = StructType(Array(
    StructField("Id", StringType),
    StructField("SerialNumber", StringType),
    StructField("ApiKey", StringType),
    StructField("VerificationKey", StringType),
    StructField("Name", StringType),
    StructField("MacAddress", StringType),
    StructField("FirmwareVersion", StringType),
    StructField("RegisteredAt", TimestampType),
    StructField("LastAccess", TimestampType),
    StructField("Activated", IntegerType),
    StructField("IPAddress", StringType),
    StructField("ReceivingRate", DoubleType),
    StructField("Timezone", StringType)
  ))

  val outputGraphHourData = "OutputGraphHourData.csv"
  val outputGraphHourDataSchema = StructType(Array(
    StructField("OutputID", StringType),
    StructField("Time", TimestampType),
    StructField("StatusProperty", IntegerType),
    StructField("Value", StringType)
  ))

  val outputLogs = "OutputLogs.csv"
  val outputLogsSchema = StructType(Array(
    StructField("ID", StringType),
    StructField("OutputID", StringType),
    StructField("Time", TimestampType),
    StructField("StatusProperty", IntegerType),
    StructField("Value", StringType)
  ))

  val outputs = "Outputs.csv"
  val outputsSchema = StructType(Array(
    StructField("Id", StringType),
    StructField("Uid", StringType),
    StructField("Active", IntegerType),
    StructField("Address", StringType),
    StructField("SubAddress", StringType),
    StructField("ControllerId", StringType),
    StructField("TypeId", StringType),
    StructField("LocationId", StringType),
    StructField("OriginalName", StringType),
    StructField("CustomName", StringType),
    StructField("ProgramId", StringType),
    StructField("Status", StringType),
    StructField("CreatedAt", TimestampType),
    StructField("UpdatedAt", TimestampType),
    StructField("CustomIcon", StringType),
    StructField("ReadOnly", StringType),
    StructField("VirtualOutput", StringType),
    StructField("Multiplier", StringType)
  ))

  val types = "Types.csv"
  val typesSchema = StructType(Array(
    StructField("Id", StringType),
    StructField("Name", StringType)
  ))
}

class QbusReader(private val sqlContext: SQLContext) {

  def read(file: String): DataFrame = {
    val df = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("delimiter", ";")
      .option("quote", null)
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load(QbusReader.baseDir + file)
    df
  }
}
