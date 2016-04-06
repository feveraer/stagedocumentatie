import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by Lorenz on 6/04/2016.
  */
object DataImporter {
  val conf = new SparkConf().setAppName("Data Importer Streamer Simulator").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val qbusReader = new QbusReader(sqlContext)

  val outputLogsDF = qbusReader.read(QbusReader.outputLogs)
  val outputGraphHourDataDF = qbusReader.read(QbusReader.outputGraphHourData)
  val outputsDF = qbusReader.read(QbusReader.outputs)
  val locationsDF = qbusReader.read(QbusReader.locations)
  val typesDF = qbusReader.read(QbusReader.types)

  outputLogsDF.registerTempTable("OutputLogs")
  outputGraphHourDataDF.registerTempTable("OutputGraphHourData")
  outputsDF.registerTempTable("Outputs")
  locationsDF.registerTempTable("Locations")
  typesDF.registerTempTable("Types")

  def measuredTempsData(): DataFrame = {
    LogManager.getRootLogger.setLevel(Level.OFF)

    sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
      + "from OutputGraphHourData oghd "
      + "join Outputs o on oghd.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO'"
    )
  }
}
