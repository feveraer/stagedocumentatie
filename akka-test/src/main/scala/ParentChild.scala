import akka.actor._

case class CreateChild(name: String)
case class Name(name: String)
case class PoisonedCandy()
case class Candy()

class Child extends Actor {
  var name = "No name"

  override def preStart {
    println(s"I'm alive and kicking at ${self.path}")
  }

  override def postStop {
    println(s"D'oh! They killed me ($name): ${self.path}")
  }

  def receive = {
    case Name(name) => this.name = name
    case PoisonedCandy =>
      println(s"$name: Ow a colorful candy")
      println(s"$name: It tastes funny")
      println(s"$name died")
      context.stop(self)
    case Candy =>
      println(s"$name: Ow a delicious candy")
    case _ => println(s"Child $name got message")
  }
}

class Parent extends Actor {
  def receive = {
    case CreateChild(name) =>
      // Parent creates a new Child here
      println(s"Parent about to create Child ($name) ...")
      val child = context.actorOf(Props[Child], name = s"$name")
      child ! Name(name)
    case _ => println(s"Parent got some other message.")
  }
}

object ParentChildDemo extends App {
  val actorSystem = ActorSystem("ParentChildTest")
  val parent = actorSystem.actorOf(Props[Parent], name = "Parent")

  // send messages to Parent to create to child actors
  parent ! CreateChild("Jonathan")
  parent ! CreateChild("Jordan")
  parent ! "something"
  Thread.sleep(2000)

  // lookup Jonathan, then kill it

  val jonathan = actorSystem.actorSelection("/user/Parent/Jonathan")
  val jordan = actorSystem.actorSelection("/user/Parent/Jordan")
  jonathan ! Candy
  jonathan ! PoisonedCandy
  jordan ! PoisonPill

  Thread.sleep(2000)
  println
  println("Closing the application")
  actorSystem.terminate()
}