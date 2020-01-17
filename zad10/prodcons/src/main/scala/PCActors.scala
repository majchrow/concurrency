package prodcons

import scala.util.Random

import akka.actor.Actor
import akka.actor.Stash
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.LoggingReceive
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Await

// Assignment: 
// - implement solution to the producers/consumers problem 
//   using the actor model / Akka
// - test the correctness of the created solution for multiple
//   producers and consumers
// Hint: use akka.actor.Stash

// object PC contains messages for all actors -- add new if you need them 
object PC {

  case object Init

  case class Put(x: Long)

  case object Get

  case object ProduceDone

  case object CheckCountdown

  case class ConsumeDone(x: Long)
}

class Producer(name: String, buf: ActorRef) extends Actor {
  import PC._
  val produce: Int = Random.nextInt(12345)

  def receive:Receive = {
    case Init =>

        println(s"Producer $name waiting to place in buffer for product $produce")
        buf ! Put(produce)
    case ProduceDone=>
      println(s"Producer $name product $produce already in buffer" )
      buf ! CheckCountdown
  }
}

class Consumer(name: String, buf: ActorRef) extends Actor {
  import PC._

  def receive: Receive = {
    case Init =>
        println(s"Consumer $name waiting to consume" )
        buf ! Get

    case ConsumeDone(x)=>
      println(s"Consumer $name consumed $x" )
      buf ! CheckCountdown
  }
}


class Buffer(n: Int, countdown: Int) extends Actor with Stash {
  import PC._

  private val buf = new Array[Long](n)
  private var count = 0
  private var actual_count = countdown // keep track of how many producers/consumers have already done iterations

  def receive = LoggingReceive {
    case Put(x) if count < n =>
      unstashAll()
      buf.update(count, x)
      sender() ! ProduceDone
      count += 1
    case Get if (count > 0) =>
      unstashAll()
      count -= 1
      val rs = buf(count)
      sender() ! ConsumeDone(rs)
    case CheckCountdown =>
      actual_count -= 1
      if(actual_count == 0){
        context.system.terminate
      }
    case _ =>
      stash()
  }
}


object ProdConsMain extends App {
  import PC.Init
  
  val system = ActorSystem("ProdKons")
  val n = 3;
  val producers = 10;
  val consumers = 8; // producers > consumers


  val buffer = system.actorOf(Props(new Buffer(n, producers + consumers)))

  (1 to producers).foreach(producer => {
    val prod = system.actorOf(Props(new Producer(s"$producer", buffer)))
    prod ! Init
  })

  (1 to consumers).foreach(consumer => {
    val cons = system.actorOf(Props(new Consumer(s"$consumer", buffer)))
    cons ! Init
  })

  Await.result(system.whenTerminated, Duration.Inf)
} 
