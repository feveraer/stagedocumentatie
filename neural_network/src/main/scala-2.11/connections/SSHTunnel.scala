package connections

import com.jcraft.jsch.{JSch, Session}
import org.apache.log4j.Logger

/**
  * Created by Lorenz on 11/04/2016.
  */
object SSHTunnel {
  private val logger = Logger.getLogger("SSHTunnel")
  private val host = "157.193.228.108"

  val lportCassandra = 9042
  val rhostCassandra = "10.11.12.120"
  val rportCassandra = 9042

  private var sessionCassandra: Session = null

  def connect(user: String, password: String): Unit = {
      // create ssh connection
      val jsch = new JSch()
      sessionCassandra = jsch.getSession(user, host, 22)
      sessionCassandra.setPassword(password)

      // Accept key file
      sessionCassandra.setConfig("StrictHostKeyChecking", "no")
      logger.info("Establishing SSH Connection...")
      sessionCassandra.connect()

      // port forwarding van rhost naar localhost
      val assinged_port = sessionCassandra.setPortForwardingL("localhost" , lportCassandra, rhostCassandra, rportCassandra)
      logger.info("localhost:" + assinged_port + " -> " + rhostCassandra + ":" + rportCassandra)
  }

  def disconnect(): Unit = {
    println("Disconnect SSH")
    sessionCassandra.disconnect
  }
}
