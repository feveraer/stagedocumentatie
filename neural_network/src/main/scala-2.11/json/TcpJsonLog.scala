package json

import java.time.{LocalDate, LocalTime}

import argonaut.{CodecJson, DecodeJson, Parse}
import argonaut.Argonaut.{casecodec4, casecodec6}
import cassandra.{SensorInfo, SensorLog}


/**
  * Created by Lorenz on 25/04/2016.
  */
object TcpJsonLog {

  object Status {
    implicit def StatusCodecJson: CodecJson[Status] =
      casecodec6(Status.apply, Status.unapply)("Regime", "Temperature", "SetTemp", "Mode", "Max", "Min")
  }

  case class Status(Regime: String, Temperature: Double, SetTemp: Double, Mode: Int, Max: Double, Min: Double)

  object Log {
    implicit def LogCodecJson: CodecJson[Log] =
      casecodec4(Log.apply, Log.unapply)("Id", "Status", "Location", "User")
  }

  case class Log(Id: Int, Status: Status, Location: String, User: String) {
    def toSensorLog(): SensorLog = {
      val date = LocalDate.now()
      val time = LocalTime.now()

      new SensorLog(Id, date.toString, time.toString, Status.Regime, Status.Temperature, Status.SetTemp)
    }

    def toSensorInfo(): SensorInfo = {
      new SensorInfo(Id, Location, User)
    }
  }

  object Decoder {
    def decodeLogJson(jsonString: String): Log = {
      val logOption = Parse.decodeOption[Log](jsonString)
      logOption.get
    }
  }
}