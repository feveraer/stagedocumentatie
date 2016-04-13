/**
  * Created by Frederic on 30/03/2016.
  */
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SQLContext}
import com.datastax.spark.connector._

object ScalaApp {

  def main(args: Array[String]) {
    // BUG FIX WINDOWS: spark needs to be able to locate winutils.exe
    // http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
    // System.setProperty("hadoop.home.dir", "d:\\winutil\\")

    // SSHTunnel.connect("root", "Ugent2015")

    val conf = new SparkConf()
      // .set("spark.cassandra.connection.host", "10.11.12.110")
      .setAppName("Simple Application")
      .setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    //val keyspace = "testqbus"

    // Turn of logging
    LogManager.getRootLogger.setLevel(Level.OFF)

    setupQbusDataFrames(sqlContext)

    val cli = new CLI(sqlContext)
    cli.run()
  }

  def testSparkCassandra(sqlContext: SQLContext, keyspace: String) {
    val qbusReader = new QbusReader(sqlContext)
    var qbusData: Vector[(DataFrame, String)] = Vector.empty

    QbusReader.qbusData.foreach({
      tableInfo =>
        qbusData = qbusData :+ (qbusReader.readCsv(tableInfo),
          Utils.trimExtension(tableInfo._1).toLowerCase)
    })

    val df = sqlContext.read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "types", "keyspace" -> keyspace))
      .load()
    println(df.show())
  }

  def setupQbusDataFrames(sqlContext: SQLContext) {
    val qbusReader = new QbusReader(sqlContext)
    var qbusData: Vector[(DataFrame, String)] = Vector.empty

    QbusReader.qbusData.foreach({
      tableInfo =>
        qbusData = qbusData :+ (qbusReader.readCsv(tableInfo),
          Utils.trimExtension(tableInfo._1))
    })

    qbusData.foreach({
      data => data._1.registerTempTable(data._2)
    })

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