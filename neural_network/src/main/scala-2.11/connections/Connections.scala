package connections

import cassandra.CassandraConnection

/**
  * Created by Lorenz on 25/04/2016.
  */
object Connections {
  // Connect to Mesos and Cassandra
  def connect(): Unit ={
    SSHTunnel.connect("root", "Ugent2015")
    CassandraConnection.connect
  }

  // Close all open connections
  def close(): Unit ={
    CassandraConnection.close
    SSHTunnel.disconnect
  }
}
