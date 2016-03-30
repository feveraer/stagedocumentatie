/**
  * Created by Frederic on 30/03/2016.
  */

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext

object ScalaApp {

  def main(args: Array[String]) {
    // BUG FIX WINDOWS: spark needs to be able to locate winutils.exe
    // http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
    System.setProperty("hadoop.home.dir", "d:\\winutil\\")

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val qbusReader = new QbusReader(sqlContext)

    val outputLogsDF = qbusReader.read(QbusReader.outputLogs).registerTempTable("OutputLogs")
    val outputGraphHourDataDF = qbusReader.read(QbusReader.outputGraphHourData).registerTempTable("OutputGraphHourData")
    val outputsDF = qbusReader.read(QbusReader.outputs).registerTempTable("Outputs")
    val locationsDF = qbusReader.read(QbusReader.locations).registerTempTable("Locations")
    val typesDF = qbusReader.read(QbusReader.types).registerTempTable("Types")

    val setTempsDF = sqlContext.sql("select ol.Time, ol.Value, l.Userid, l.Name as LocationName "
      + "from OutputLogs ol "
      + "join Outputs o on ol.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO'"
    )

    setTempsDF.foreach(println)
  }
}