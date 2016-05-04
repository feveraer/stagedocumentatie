package simulator.cassandra.importers

import java.io.{File, FileInputStream, ObjectInputStream}

import ann.Constants
import cassandra.{SensorLog, SensorModel, SetTemperatureLog}
import connections.SSHTunnel
import helpers.Helpers.{Quartile, Season}
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper
import org.encog.persist.EncogDirectoryPersistence
import simulator.cassandra.CassandraTestConnection
import time.DateTime

import scala.io.Source

/**
  * Created by Lorenz on 4/05/2016.
  */
object CassandraTestDataImporter {
  val sensorID = 0
  val regime = "SOME_REGIME"

  val pathToNormalizationHelper = Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH
  val pathToModel = Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH

  def main(args: Array[String]) {
    SSHTunnel.connect("root", "Ugent2015")
    CassandraTestConnection.connect()
    importData()
    CassandraTestConnection.close()
    SSHTunnel.disconnect()
  }


  private def importData() {
    val pathToTSV = "src/main/resources/input_eetkamer_LaMa.TSV"
    val lines = Source.fromFile(pathToTSV).getLines()

    // This is the header
    lines.next()

    // For all other lines create logs en insert in Cassandra
    while (lines.hasNext) {
      val line = lines.next
      val (sensorLog, setTempLog) = Mapper.map(line)
      CassandraTestConnection.insertHistoricSensorLog(sensorLog)
      CassandraTestConnection.insertSetTemperature(setTempLog)
    }

    try {
      // put model in Cassandra
      val fin: FileInputStream = new FileInputStream(pathToNormalizationHelper)
      val ois: ObjectInputStream = new ObjectInputStream(fin)
      val helper = ois.readObject.asInstanceOf[NormalizationHelper]
      ois.close()

      val model = EncogDirectoryPersistence.loadObject(new File(pathToModel)).asInstanceOf[MLRegression]

      val entry = SensorModel.build(sensorID, model, helper)

      CassandraTestConnection.insertSensorModels(entry)
    } catch {
      case e: Any => {
        e.printStackTrace
      }
    }
  }

  object Mapper {
    // Map line to sensorLog en SetTemperatureLog
    def map(line: String): (SensorLog, SetTemperatureLog) = {
      val parts = line.split("\t")

      // Create dateTime from the parts of the line
      val dateTime = new DateTime(parts(0).toInt, parts(1).toInt,
        parts(2).toInt, parts(3).toInt, parts(4).toInt, parts(5).toInt)

      // Create params for SensorLog
      val date = dateTime.date.toString
      val time = dateTime.time.toString
      val setTemp = parts(9).toInt
      val measured = parts(10).toInt

      // Create params for SetTemperatureLog
      val season = Season.getSeason(dateTime.date)
      val day = dateTime.date.getDayOfWeek.toString
      val hour = dateTime.time.getHour
      val quartile = Quartile.getQuartile(dateTime.time)

      // Create Logs
      val sensorLog = new SensorLog(sensorID, date, time, regime, measured, setTemp)
      val setTemperatureLog = new SetTemperatureLog(sensorID, season, day, hour, quartile, setTemp)

      (sensorLog, setTemperatureLog)
    }
  }

}
