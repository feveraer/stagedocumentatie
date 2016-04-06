/**
  * Created by Frederic on 30/03/2016.
  */

import java.sql.Timestamp

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts._

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

    setTempsDF.registerTempTable("SetTemps")

    val measuredTempsDF = sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
      + "from OutputGraphHourData oghd "
      + "join Outputs o on oghd.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO'"
    )

    measuredTempsDF.registerTempTable("MeasuredTemps")

    val measuredTempsForUserAndLocationDF = sqlContext.sql(
      "select Time, Value "
        + "from MeasuredTemps "
        + "where Userid = 53 "
        + "and LocationName = 'Badkamer' "
        + "and Time > '2016-02-22' "
        + "order by Time"
    )

    val setTempsForUserAndLocationDF = sqlContext.sql(
      "select Time, Value "
        + "from SetTemps "
        + "where Userid = 53 "
        + "and LocationName = 'Badkamer' "
        + "and Time > '2016-02-22' "
        + "order by Time"
    )

    val measuredTempsTimes = Utils.getSeqFromDF[Timestamp](measuredTempsForUserAndLocationDF, "Time")
      .map(t => t.getTime)
    val measuredTempsValues = Utils.getSeqFromDF[String](measuredTempsForUserAndLocationDF, "Value")
      .map(v => v.replace(",", "."))
      .map(v => v.toDouble)

    val setTempsTimes = Utils.getSeqFromDF[Timestamp](setTempsForUserAndLocationDF, "Time")
      .map(t => t.getTime)
    val setTempsValues = Utils.getSeqFromDF[String](setTempsForUserAndLocationDF, "Value")
      .map(v => v.replace(",", "."))
      .map(v => v.toDouble)

    line(measuredTempsTimes.zip(measuredTempsValues))
    hold()
    line(setTempsTimes.zip(setTempsValues))
    stack()
    title("Measured vs Set Temperatures")
    xAxisType(AxisType.datetime)
    xAxis("Time")
    yAxis("Temperature in °C")
    legend(List("Measured", "Set"))
  }

}