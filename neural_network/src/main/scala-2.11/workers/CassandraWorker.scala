package workers

import akka.actor.Actor
import akka.util.ByteString
import cassandra.CassandraConnection

/**
  * Created by Frederic on 25/04/2016.
  */
class CassandraWorker extends Actor {

  def receive = {
    //case data: ByteString => CassandraConnection(new String(data.toByteBuffer.array()))
    case default => println(default)
  }

}
