/**
  * Created by Frederic on 30/03/2016.
  */

import java.sql.Timestamp

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import com.quantifind.charts.{Highcharts, highcharts}
import com.quantifind.charts.highcharts.Highchart._
import com.quantifind.charts.highcharts._

object ScalaApp {

  def main(args: Array[String]) {
    // BUG FIX WINDOWS: spark needs to be able to locate winutils.exe
    // http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
    // System.setProperty("hadoop.home.dir", "d:\\winutil\\")

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    // Turn of logging
    LogManager.getRootLogger.setLevel(Level.OFF)

    val qbusReader = new QbusReader(sqlContext)

    val outputLogsDF = qbusReader.read(QbusReader.outputLogs, QbusReader.outputLogsSchema)
    val outputGraphHourDataDF = qbusReader.read(QbusReader.outputGraphHourData, QbusReader.outputGraphHourDataSchema)
    val outputsDF = qbusReader.read(QbusReader.outputs, QbusReader.outputsSchema)
    val locationsDF = qbusReader.read(QbusReader.locations, QbusReader.locationsSchema)
    val typesDF = qbusReader.read(QbusReader.types, QbusReader.typesSchema)

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

    val measuredTempsDF = sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
      + "from OutputGraphHourData oghd "
      + "join Outputs o on oghd.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO' and oghd.Time > '2016-02-22'"
    )

    //setTempsDF.printSchema()
    //setTempsDF.take(10).foreach(println)

    //measuredTempsDF.printSchema()
    //measuredTempsDF.take(10).foreach(println)

    //val chart = Highchart(Seq(Series(Seq(Data(1, 2)))), chart = Chart(zoomType = Zoom.xy), yAxis = None)
    //Highcharts.plot(chart)

    val measuredTempsTimes = qbusReader.getSeqFromDF[Timestamp](measuredTempsDF, "Time")
    val measuredTempsValues = qbusReader.getSeqFromDF[String](measuredTempsDF, "Value")
    val setTempsTimes = qbusReader.getSeqFromDF[Timestamp](setTempsDF, "Time")
    val setTempsValues = qbusReader.getSeqFromDF[String](setTempsDF, "Value")

    Highcharts.line(
      qbusReader.convertTemperatureValues(measuredTempsValues)
    )
  }
}