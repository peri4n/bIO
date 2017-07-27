import java.nio.file.{Path, Paths}

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object bIO extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  import akka.stream.scaladsl._

//  val file = Paths.get("/Users/fbull/test.fa")

//  private val source = FileIO.fromPath(file)
//
//  val foreach: Future[Done] = source
//    .runForeach(println)

  private val source: Source[Int, NotUsed] = Source(1 to 100)
  private val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1)) { (acc, elem) => acc * elem}


  private val future = FileIO.fromPath(Paths.get("/Users/fbull/test.fa"))
    .via(new FastaProcessor())
    .runWith(Sink.foreach(println))

  future
    .onComplete(_ => system.terminate())
}
