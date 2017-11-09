package at.bioinform.webapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import at.bioinform.lucene.segment.Segment
import at.bioinform.model.db.TableDefinitions
import at.bioinform.stream.fasta.{FastaFlow, FastaSegmenter}
import at.bioinform.webapp.directives.LuceneIndexResource
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object Application extends TableDefinitions with LuceneIndexResource {

  val Logger = LoggerFactory.getLogger(Application.getClass.getName)

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    val conf = ConfigFactory.load()

    val db = Database.forConfig("database.test").asInstanceOf[JdbcBackend.Database]
    Logger.info("Database settings:")
    Logger.info("- Database url: {}", conf.getString("database.test.url"))

    Logger.info("Sequence indexing options:")
    Logger.info("- Kmer size for DNA indexing is set to: {}", conf.getInt("bio.indexing.dna.kmer-size"))
    Logger.info("- Kmer overlap for DNA indexing is set to: {}", conf.getInt("bio.indexing.dna.kmer-overlap"))

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
                .via(FastaSegmenter(conf.getInt("bio.indexing.dna.kmer-size"), conf.getInt("bio.indexing.dna.kmer-overlap")))
                .via(Flow[Segment].map(seg => ByteString(seg.id.value)))
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, source))
            }
          }
        } ~ luceneIndexRoutes(db)

    Await.result(createDbSchema(db), Duration.Inf)

    Logger.info(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => {
      system.terminate()
      db.close()
    }) // and shutdown when done
  }

  def createDbSchema(db: JdbcBackend.Database): Future[Unit] = {
    Logger.info("Creating database schema")
    db.run(schema)
  }
}
