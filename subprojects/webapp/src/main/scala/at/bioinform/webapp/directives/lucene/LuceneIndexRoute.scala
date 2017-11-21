package at.bioinform.webapp.directives.lucene

import java.nio.file.Paths

import akka.http.scaladsl.marshalling.GenericMarshallers._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import at.bioinform.webapp.Env
import at.bioinform.webapp.db.TableDefinitions
import cats.data.Reader

import scala.concurrent.ExecutionContext.Implicits.global

trait LuceneIndexRoute extends TableDefinitions {

  import at.bioinform.webapp.repository.Repositories.indexRepository

  def luceneIndexRoutes: Reader[Env, Route] =
    for {
      repo <- indexRepository
    } yield path("index") {
      post {
        complete {
          val id = repo.create("first", Paths.get("path")).map(_.toString)
          Marshal(id).to[HttpResponse]
        }
      }
    }

}
