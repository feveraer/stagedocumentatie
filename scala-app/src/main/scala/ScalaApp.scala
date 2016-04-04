/**
  * Created by Frederic on 30/03/2016.
  */

import java.sql.Timestamp

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import breeze.linalg._
import breeze.plot._

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

    //val graphBuilder = new GraphBuilder()
    //graphBuilder.test()

    val f = Figure()
    val p = f.subplot(0)
    val x1 = measuredTempsDF
    val x2 = setTempsDF

    // Breeze works with DenseVectors
    // Mapped the desired column of a DataFrame to an array wrapped in a DenseVector
    val v1 = new DenseVector(x1.select("Time").rdd.map(t => t(0).asInstanceOf[Timestamp]).collect())
    val v2 = new DenseVector(x1.select("Value").rdd.map(t => t(0).asInstanceOf[String]).collect())

    p += plot(v1, v2)

    p.xlabel = "Time"
    p.ylabel = "Value"
    f.saveas("qbus_measured_vs_set.png")
  }
}