package at.bioinform.webapp.directives

import akka.http.scaladsl.marshalling.GenericMarshallers._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import at.bioinform.model.db.TableDefinitions
import cats.data.Reader
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContext.Implicits.global

trait LuceneIndexResource extends TableDefinitions {

  def luceneIndexRoutes: Reader[JdbcBackend.Database, Route] = Reader { db =>
    path("index") {
      post {
        complete {
          val id = LuceneIndexRepository.createIndex("first", "path")(db).map(_.toString)
          Marshal(id).to[HttpResponse]
        }
      }
    }
  }

}
