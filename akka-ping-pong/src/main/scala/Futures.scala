import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import akka.dispatch.ExecutionContexts._

import scala.concurrent.Await

case class HeavyWork()

class HeavyWorker() extends Actor {
  private var msgSender: Option[ActorRef] = None
  def receive = {
    case HeavyWork() =>
      msgSender = Some(sender)
      println("Starting the heavy work")
      Thread.sleep(5000)
      val result = "Some heavy work has been done"
      msgSender.map(_ ! result)
    case _ => println("Message not recognized")
  }
}

object FutureDemo extends App {
  implicit val ec = global

  override def main(args: Array[String]) {
    val system = ActorSystem("System")
    val actor = system.actorOf(Props[HeavyWorker], name = "worker")
    implicit val timeout = Timeout(25 seconds)
    val future = actor ? HeavyWork()

    val result =  Await.result(future, timeout.duration).asInstanceOf[String]
    println(result)

    system.terminate()
  }
}