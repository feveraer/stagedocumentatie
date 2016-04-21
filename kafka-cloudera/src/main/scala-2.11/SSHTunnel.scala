import com.jcraft.jsch.{JSch, Session}

/**
  * Created by Lorenz on 11/04/2016.
  */
class SSHTunnel {
  //  private var host = "cloudera.ugent.be"
  //
  //  var lport = 9092
  //  var rhost = "cl06.ugent.be"
  //  var rport = 9092

  private var session: Session = null

  def connect(user: String, password: String, host: String, rhost: String, rport: Int, lport: Int): Unit = {
    // create ssh connection
    val jsch = new JSch()
    session = jsch.getSession(user, host, 22)
    session.setPassword(password)
    // Accept key file
    session.setConfig("StrictHostKeyChecking", "no")
    println("Establishing SSH Connection...")
    session.connect()

    // port forwarding van rhost naar localhost
    val assigned_port = session.setPortForwardingL("localhost", lport, rhost, rport)
    println("localhost:" + assigned_port + " -> " + rhost + ":" + rport)
  }

  def disconnect(): Unit = {
    println("Disconnecting SSH...")
    session.disconnect()
  }

  def connectHARDCODE(user: String, password: String): Unit = {
    val host = "cloudera.ugent.be"
    val rhost1 = "cl06.ugent.be"
    val rhost2 = "cl02.ugent.be"
    val rport1 = 9092
    val rport2 = 2181
    val lport1= 9092
    val lport2 = 2181

    // create ssh connection
    val jsch = new JSch()
    session = jsch.getSession(user, host, 22)
    session.setPassword(password)
    // Accept key file
    session.setConfig("StrictHostKeyChecking", "no")
    println("Establishing SSH Connection...")
    session.connect()

    // port forwarding van rhost naar localhost
    val assigned_port1 = session.setPortForwardingL("localhost", lport1, rhost1, rport1)
    println("localhost:" + assigned_port1 + " -> " + rhost1 + ":" + rport1)
    val assigned_port2 = session.setPortForwardingL("localhost", lport2, rhost2, rport2)
    println("localhost:" + assigned_port2 + " -> " + rhost2 + ":" + rport2)
  }
}
