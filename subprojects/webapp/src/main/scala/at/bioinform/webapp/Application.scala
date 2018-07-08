package at.bioinform.webapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import at.bioinform.webapp.config.Config
import at.bioinform.webapp.db.{DatabaseProvider, TableDefinitions}
import at.bioinform.webapp.repository.InMemorySequenceRepository
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
      _ <- DatabaseProvider.test()
      _ <- Config.printConfiguration()
      _ <- createDbSchema(schema)
      route <- directive.Main.routes
    } yield route

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

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

  object production extends Env {
    override def config = ConfigFactory.load()

    override def repositories = new Repositories {
      def sequenceRepository = InMemorySequenceRepository
    }
  }

}
