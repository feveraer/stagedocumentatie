import java.sql.Timestamp

import org.apache.spark.sql.{DataFrame, SQLContext}

import scala.io.StdIn
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts._

/**
  * Created by frederic on 6/04/16.
  */
class CLI(sqlContext: SQLContext) {

  def run() {
    var stop = false
    var chooseOtherUserID = false
    var userID: Int = 0

    do {
      showAllUserIDs()
      do {
        userID = selectUserID()
        showAllLocations(userID)

        val input = StdIn.readLine("What to do? continue(1), choose other user id(2): ").toInt
        chooseOtherUserID = input == 2
      } while (chooseOtherUserID)

      val location = selectLocation()
      val dfs = buildDataFrames(userID, location)
      drawPlot(dfs)
      val input = StdIn.readLine("Stop? (y or n): ")
      stopWispServer
      stop = input == "y"
    } while (!stop)
  }

  private def showAllUserIDs() {
    val usersDF = sqlContext.sql(
      "select distinct Userid "
        + "from SetTemps"
    )

    val allUserIDs = usersDF.rdd.map(x => x(0) + ", ").collect()
    val lastIndex = allUserIDs.length - 1
    allUserIDs.update(lastIndex, allUserIDs(lastIndex).replace(", ", ""))

    allUserIDs.foreach(print)
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

    locationsDF.rdd.map(x => x(0)).foreach(println)
  }

  private def selectLocation(): String = {
    StdIn.readLine("Enter a location: ")
  }

  private def buildDataFrames(userID: Int, location: String): Array[DataFrame] = {
    val measuredTempsForUserAndLocationDF = sqlContext.sql(
      "select Time, Value "
        + "from MeasuredTemps "
        + "where Userid = " + userID + " "
        + "and LocationName = '" + location + "' "
        + "and Time > '2016-02-22' "
        + "order by Time"
    )

    val setTempsForUserAndLocationDF = sqlContext.sql(
      "select Time, Value "
        + "from SetTemps "
        + "where Userid = " + userID + " "
        + "and LocationName = '" + location +"' "
        + "and Time > '2016-02-22' "
        + "order by Time"
    )

    Array(measuredTempsForUserAndLocationDF, setTempsForUserAndLocationDF)
  }

  private def drawPlot(dfs: Array[DataFrame]) {
    val measuredTempsTimes = Utils.getSeqFromDF[Timestamp](dfs(0), "Time")
      .map(t => t.getTime)
    val measuredTempsValues = Utils.getSeqFromDF[String](dfs(0), "Value")
      .map(v => v.replace(",", "."))
      .map(v => v.toDouble)

    val setTempsTimes = Utils.getSeqFromDF[Timestamp](dfs(1), "Time")
      .map(t => t.getTime)
    val setTempsValues = Utils.getSeqFromDF[String](dfs(1), "Value")
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
