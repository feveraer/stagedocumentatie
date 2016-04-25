package connections

import cassandra.CassandraConnection

/**
  * Created by Lorenz on 25/04/2016.
  */
object Connections {
  def connect(): Unit ={
    SSHTunnel.connect("root", "Ugent2015")
    CassandraConnection.connect
  }

  def close(): Unit ={
    CassandraConnection.close
    SSHTunnel.disconnect
  }
}
