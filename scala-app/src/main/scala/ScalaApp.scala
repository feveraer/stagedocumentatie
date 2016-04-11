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

    SSHTunnel.connect("root", "Ugent2015")

    val conf = new SparkConf()
      .set("spark.cassandra.connection.host", "127.0.0.1")
      .setAppName("Simple Application")
      .setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val keyspace = "testqbus"

    // Turn of logging
    LogManager.getRootLogger.setLevel(Level.OFF)

    setupQbusDataFrames(sqlContext, keyspace)

    //val cli = new CLI(sqlContext)
    //cli.run()

    SSHTunnel.disconnect()
  }

  def setupQbusDataFrames(sqlContext: SQLContext, keyspace: String) {
    val qbusReader = new QbusReader(sqlContext)
    var qbusData: Vector[(DataFrame, String)] = Vector.empty

    QbusReader.qbusData.foreach({
      tableInfo =>
        qbusData = qbusData :+ (qbusReader.readCsv(tableInfo),
          Utils.trimExtension(tableInfo._1).toLowerCase)
    })

    qbusData.foreach({
      data => data._1.write
        .format("org.apache.spark.sql.cassandra")
        .options(Map( "table" -> data._2, "keyspace" -> keyspace))
        .save()
    })

//    val setTempsDF = sqlContext.sql("select ol.Time, ol.Value, l.Userid, l.Name as LocationName "
//      + "from outputlogs ol "
//      + "join outputs o on ol.OutputID = o.Id "
//      + "join locations l on o.LocationId = l.Id "
//      + "join types t on o.TypeId = t.Id "
//      + "where t.Name = 'THERMO'"
//    )
//
//    setTempsDF.write
//      .format("org.apache.spark.sql.cassandra")
//      .options(Map( "table" -> "settemps", "keyspace" -> keyspace))
//      .save()
//    setTempsDF.cache()
//
//    val measuredTempsDF = sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
//      + "from outputGraphHourData oghd "
//      + "join outputs o on oghd.OutputID = o.Id "
//      + "join locations l on o.LocationId = l.Id "
//      + "join types t on o.TypeId = t.Id "
//      + "where t.Name = 'THERMO'"
//    )
//
//    measuredTempsDF.write
//      .format("org.apache.spark.sql.cassandra")
//      .options(Map( "table" -> "measuredtemps", "keyspace" -> keyspace))
//      .save()
//    measuredTempsDF.cache()
  }

}