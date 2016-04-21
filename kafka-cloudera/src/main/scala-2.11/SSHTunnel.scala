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
}
