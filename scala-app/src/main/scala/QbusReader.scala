import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by Frederic on 30/03/2016.
  */
object QbusReader {

  // on Windows:
  // val baseDir = "d:\\Qbus\\"
  // on Linux:
  val baseDir = "/home/frederic/ASUS_D_Drive/Qbus/"

  private val locations = ("Locations.csv", StructType(Array(
    StructField("Id", StringType),
    StructField("Name", StringType),
    StructField("Userid", StringType),
    StructField("OriginalID", StringType),
    StructField("ControllerID", StringType),
    StructField("ParentID", StringType)
  )))

  private val controllers = ("Controllers.csv", StructType(Array(
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
  )))

  private val outputGraphHourData = ("OutputGraphHourData.csv", StructType(Array(
    StructField("OutputID", StringType),
    StructField("Time", TimestampType),
    StructField("StatusProperty", IntegerType),
    StructField("Value", StringType)
  )))

  private val outputLogs = ("OutputLogs.csv", StructType(Array(
    StructField("ID", StringType),
    StructField("OutputID", StringType),
    StructField("Time", TimestampType),
    StructField("StatusProperty", IntegerType),
    StructField("Value", StringType)
  )))

  private val outputs = ("Outputs.csv", StructType(Array(
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
  )))

  private val types = ("Types.csv", StructType(Array(
    StructField("Id", StringType),
    StructField("Name", StringType)
  )))

  val qbusData = Vector(outputLogs, outputGraphHourData, outputs, locations, types)
  //val qbusData = Vector(types)
}

class QbusReader(private val sqlContext: SQLContext) {

  def readCsv(source: (String, StructType)): DataFrame = {
    val df = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("delimiter", ";")
      .option("quote", null)
      .option("header", "true") // Use first line of all files as header
      .schema(source._2)
      .load(QbusReader.baseDir + source._1)
    df
  }


}
