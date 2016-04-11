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
    var chooseOtherLocation = false
    var userID: Int = 0
    var input: Int = 0
    var correctInput = false

    // outer loop for application until user kills it
    do {
      showAllUserIDs()
      // loop so user can change user id if there wasn't any data at
      // a certain location
      do {
        // loop so user can change the user id after viewing the locations
        do {
          // loop until user provides valid number
          do {
            try {
              correctInput = false
              userID = selectUserID()
              correctInput = true
            } catch {
              case ex: NumberFormatException => {
                System.err.println("Not a valid user id.")
                System.err.flush()
                correctInput = false
              }
            }
          } while (!correctInput)
          showAllLocations(userID)
          println()
          // loop until user provides valid number
          do {
            try {
              correctInput = false
              input = StdIn.readLine("Options: confirm user id " + userID + " (1), choose a different user (2): ").toInt
              chooseOtherUserID = input == 2
              correctInput = true
            } catch {
              case ex: NumberFormatException => {
                System.err.println("Enter 1 to continue, 2 to choose a different user.")
                System.err.flush()
                correctInput = false
              }
            }
          } while (!correctInput)
        } while (chooseOtherUserID)

        do {
          val location = selectLocation()
          val measuredDF = buildDataFrame(userID, location, "MeasuredTemps")
          if (measuredDF.count() == 0) {
            System.err.println("No data found. Either the location name was misspelled or there is no temperature data at all.")
            System.err.flush()
            do {
              try {
                correctInput = false
                input = StdIn.readLine("Options: enter location again (1), choose a different user (2), kill the application (3): ").toInt
                chooseOtherLocation = input == 1
                chooseOtherUserID = input == 2
                if (input == 3)
                  System.exit(0)
                correctInput = true
              } catch {
                case ex: NumberFormatException => {
                  System.err.println("Options: enter location again (1), choose a different user (2), kill the application (3): ")
                  System.err.flush()
                  correctInput = false
                }
              }
            } while (!correctInput)
          } else {
            // reset loop flags
            chooseOtherLocation = false
            chooseOtherUserID = false
            val setDF = buildDataFrame(userID, location, "SetTemps")
            drawPlot(measuredDF, setDF)
            println()
            stop = StdIn.readLine("Stop application? (y or n): ")
            // stop Wisp server and delete local .html file
            stopWispServer
            // server maintains history, so make sure the plot is deleted
            deleteAll
          }
        } while (chooseOtherLocation)
      } while (chooseOtherUserID)
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
