package at.bioinform.webapp.directives.index

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

object IndexRoute extends TableDefinitions {

  def index: Reader[Env, Route] = Reader { env =>
    path("index") {
      post {
        complete {
          val id = env.repositories.indexRepository.create("first", Paths.get("path")).id.toString
          Marshal(id).to[HttpResponse]
        }
      }
    }
  }

}
