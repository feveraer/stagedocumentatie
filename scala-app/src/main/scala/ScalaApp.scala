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

    val logFile = "src/main/resources/test.txt" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read
      .option("delimiter", ";")
      .format("com.databricks.spark.csv")
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load("d:\\Qbus\\Types.csv")

    val selectedData = df.select("Id", "Name").foreach(println)
  }
}