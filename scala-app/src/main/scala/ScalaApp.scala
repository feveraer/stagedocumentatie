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
    val df = qbusReader.read(QbusReader.locations)

    val selectedData = df.select("Id", "Name").foreach(println)
  }
}