package at.bioinform.webapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import at.bioinform.lucene.segment.Segment
import at.bioinform.stream.fasta.{FastaFlow, FastaSegmenter}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.io.StdIn

object Application {

  val Logger = LoggerFactory.getLogger(Application.getClass.getName)

  def main(args: Array[String]): Unit = {

    Logger.debug("Reading configuration:")
    val conf = ConfigFactory.load()
    val kmerSize = conf.getInt("bio.indexing.dna.kmer-size")
    Logger.debug("Kmer size for DNA indexing is set to: {}", kmerSize)
    val kmerOverlap = conf.getInt("bio.indexing.dna.kmer-overlap")
    Logger.debug("Kmer overlap for DNA indexing is set to: {}", kmerOverlap)

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Index()))
        }
      } ~
        path("fasta") {
          post {
            extractDataBytes { data =>
              val source = data
                .via(FastaFlow())
                .via(FastaSegmenter(kmerSize, kmerOverlap))
                .via(Flow[Segment].map(seg => ByteString(seg.id.value)))
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, source))
            }
          }
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    Logger.info(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
