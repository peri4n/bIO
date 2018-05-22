package at.bioinform.webapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import at.bioinform.webapp.db.{DatabaseProvider, TableDefinitions}
import at.bioinform.webapp.directives.index.IndexRoute
import at.bioinform.webapp.model.index.LuceneIndexes
import cats.data.Reader
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Application extends TableDefinitions {

  val Logger = LoggerFactory.getLogger(Application.getClass.getName)

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    val route = for {
      db <- DatabaseProvider.test()
      _ <- printConfiguration()
      _ <- createDbSchema(schema)
      route <- IndexRoute.index
    } yield route

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    //    val route =
    //      path("") {
    //        get {
    //          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, IndexPage()))
    //        }
    //      } ~
    //        path("fasta") {
    //          post {
    //            extractDataBytes { data =>
    //              val source = data
    //                .via(FastaFlow())
    //                .via(FastaSegmenter(conf.getInt("bio.indexing.dna.kmer-size"), conf.getInt("bio.indexing.dna.kmer-overlap")))
    //                .via(Flow[Segment].map(seg => ByteString(seg.id.value)))
    //              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, source))
    //            }
    //          }
    //        } ~ luceneIndexRoutes(production)

    Logger.info(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    val bindingFuture = Http().bindAndHandle(route.run(production), "localhost", 8080)

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => {
        system.terminate()
      }) // and shutdown when done
  }

  def createDbSchema(schema: DBIOAction[Unit, NoStream, Effect.Schema]): Reader[Env, Unit] = Reader { env =>
    Logger.info("Creating database schema")
    for {
      db <- DatabaseProvider.test()
    } yield Await.result(db.run(schema), Duration.Inf)
  }

  def printConfiguration(): Reader[Env, Unit] = Reader { env =>
    Logger.info("Database settings:")
    Logger.info("  Database url: {}", env.config.getString("database.test.url"))

    Logger.info("Sequence indexing options:")
    Logger.info("  Kmer size for DNA indexing is set to: {}", env.config.getInt("bio.indexing.dna.kmer-size"))
    Logger.info("  Kmer overlap for DNA indexing is set to: {}", env.config.getInt("bio.indexing.dna.kmer-overlap"))
  }

  object production extends Env {
    override def config = ConfigFactory.load()

    override def repositories = new Repositories {
      override def indexRepository = LuceneIndexes
    }
  }
}
