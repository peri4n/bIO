package bioinformat

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global

object bIO extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  import akka.stream.scaladsl._

  private val future = FileIO.fromPath(Paths.get("/Users/fbull/Downloads/mrna.fa"))
    .via(FastaProcessor)
    .runWith(Sink.foreach( entry => println(entry.id)))

  future
    .onComplete(_ => system.terminate())

}
