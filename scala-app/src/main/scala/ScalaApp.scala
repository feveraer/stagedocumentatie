/**
  * Created by Frederic on 30/03/2016.
  */
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SQLContext}

object ScalaApp {

  def main(args: Array[String]) {
    // BUG FIX WINDOWS: spark needs to be able to locate winutils.exe
    // http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
    // System.setProperty("hadoop.home.dir", "d:\\winutil\\")

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    // Turn of logging
    LogManager.getRootLogger.setLevel(Level.OFF)

    setupQbusDataFrames(sqlContext)

    val cli = new CLI(sqlContext)
    cli.run()
  }

  def setupQbusDataFrames(sqlContext: SQLContext) {
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

    val setTempsDF = sqlContext.sql("select ol.Time, ol.Value, l.Userid, l.Name as LocationName "
      + "from OutputLogs ol "
      + "join Outputs o on ol.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO'"
    )

    setTempsDF.registerTempTable("SetTemps")
    setTempsDF.cache()

    val measuredTempsDF = sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
      + "from OutputGraphHourData oghd "
      + "join Outputs o on oghd.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO'"
    )

    measuredTempsDF.registerTempTable("MeasuredTemps")
    measuredTempsDF.cache()
  }

}