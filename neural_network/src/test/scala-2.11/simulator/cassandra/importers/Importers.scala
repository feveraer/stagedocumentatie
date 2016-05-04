package simulator.cassandra.importers

import cassandra.{SensorLog, SetTemperatureLog}
import helpers.Helpers.{Quartile, Season}
import time.DateTime

import scala.io.Source

/**
  * Created by Lorenz on 4/05/2016.
  */
object Importers {
  val sensorID = 0
  val regime = "SOME_REGIME"

  object DataReader {
    def read() {
      val pathToTSV = "src/main/resources/input_eetkamer_LaMa.TSV"
      val lines = Source.fromFile(pathToTSV).getLines()

      while (lines.hasNext) {

      }
    }
  }

  object Mapper {
    def map(line: String): (SensorLog, SetTemperatureLog) = {
      val parts = line.split("\t")

      val dateTime = new DateTime(parts(0).toInt, parts(1).toInt,
        parts(2).toInt, parts(3).toInt, parts(4).toInt, parts(5).toInt)

      val date = dateTime.date.toString
      val time = dateTime.time.toString
      val setTemp = parts(9).toInt
      val measured = parts(10).toInt

      val season = Season.getSeason(dateTime.date)
      val day = dateTime.date.getDayOfWeek.toString
      val hour = dateTime.time.getHour
      val quartile = Quartile.getQuartile(dateTime.time)


      val sensorLog = new SensorLog(sensorID, date, time, regime, measured, setTemp)
      val setTemperatureLog = new SetTemperatureLog(sensorID, season, day, hour, quartile, setTemp)

    }
  }

}
