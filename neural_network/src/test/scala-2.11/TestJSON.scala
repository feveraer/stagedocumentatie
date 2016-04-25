import json.TcpJsonLog.Decoder

/**
  * Created by Lorenz on 25/04/2016.
  */
object TestJSON {
  def main(args: Array[String]) {
    val string = "{\"Id\":206354,\"Status\":{\"Regime\":\"Economy\",\"Temperature\":18.0,\"SetTemp\":18.0,\"Mode\":15,\"Max\":35.0,\"Min\":5.0},\"Location\":\"Keuken\",\"User\":\"pieter@aaltra.eu\"}"
    Decoder.decodeLogJson(string)
  }
}
