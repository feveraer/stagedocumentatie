import java.sql.Timestamp

import org.apache.spark.sql.{DataFrame, SQLContext}

import scala.io.StdIn
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts._

/**
  * Created by frederic on 6/04/16.
  */
class CLI(sqlContext: SQLContext) {

  private var userIDsCache: Array[Int] = Array.empty

  def run() {
    var stop: String = null
    var chooseOtherUserID = false
    var userID: Int = 0

    do {
      showAllUserIDs()
      do {
        userID = selectUserID()
        showAllLocations(userID)
        println()
        // provide option to choose user id again if there isn't a desired location
        val input = StdIn.readLine("What to do? continue(1), choose other user id(2): ").toInt
        chooseOtherUserID = input == 2
      } while (chooseOtherUserID)

      val location = selectLocation()
      val measuredDF = buildDataFrame(userID, location, "MeasuredTemps")
      val setDF = buildDataFrame(userID, location, "SetTemps")
      drawPlot(measuredDF, setDF)
      println()
      stop = StdIn.readLine("Stop application? (y or n): ")
      // stop Wisp server and delete local .html file
      stopWispServer
      // server maintains history, so make sure the plot is deleted
      deleteAll
    } while (!stop.startsWith("y"))
  }

  private def showAllUserIDs() {
    if (userIDsCache.isEmpty) {
      val usersDF = sqlContext.sql(
        "select distinct Userid "
          + "from SetTemps"
      )
      userIDsCache = usersDF.rdd.map(x => x(0).toString.toInt).collect()
      // sort ids numerically
      userIDsCache = userIDsCache.sorted
    }
    println("AVAILABLE USER IDS:")
    println("===================")
    for(i <- userIDsCache.indices) {
      if ((i+1) % 10 == 0) {
        println(userIDsCache(i) + ",")
      } else {
        print(userIDsCache(i) + ", ")
      }
    }
    println()
  }

  private def selectUserID(): Int = {
    StdIn.readLine("Enter a user id: ").toInt
  }

  private def showAllLocations(userID: Int) {
    val locationsDF = sqlContext.sql(
      "select distinct LocationName "
        + "from SetTemps "
        + "where Userid = " + userID
    )

    println()
    println("AVAILABLE LOCATIONS:")
    println("====================")
    locationsDF.rdd.map(x => x(0)).foreach(println)
  }

  private def selectLocation(): String = {
    StdIn.readLine("Enter a location: ")
  }

  private def buildDataFrame(userID: Int, location: String, rootDF: String): DataFrame = {
    val tempsForUserAndLocationDF = sqlContext.sql(
      "select Time, Value "
        + "from " + rootDF + " "
        + "where Userid = " + userID + " "
        + "and LocationName = '" + location + "' "
        + "and Time > '2016-02-22' "
        + "order by Time"
    )

    tempsForUserAndLocationDF
  }

  private def drawPlot(dfs: DataFrame*) {
    var temps: Vector[(Seq[Long], Seq[Double])] = Vector.empty
    dfs.foreach(df => {
      val tempsTimes = Utils.getSeqFromDF[Timestamp](df, "Time")
        .map(t => t.getTime)
      val tempsValues = Utils.getSeqFromDF[String](df, "Value")
        .map(v => v.replace(",", "."))
        .map(v => v.toDouble)
      temps = temps :+ (tempsTimes, tempsValues)
    })

    line(temps(0)._1.zip(temps(1)._2))
    hold()
    line(temps(1)._1.zip(temps(1)._2))
    stack()
    title("Measured vs Set Temperatures")
    xAxisType(AxisType.datetime)
    xAxis("Time")
    yAxis("Temperature in °C")
    legend(List("Measured", "Set"))
  }
}
