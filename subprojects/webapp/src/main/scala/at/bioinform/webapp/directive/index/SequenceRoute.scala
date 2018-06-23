package at.bioinform.webapp.directive.index

import akka.http.scaladsl.marshalling.GenericMarshallers._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import at.bioinform.webapp.Env
import at.bioinform.webapp.db.TableDefinitions
import cats.data.Reader

import scala.concurrent.ExecutionContext.Implicits.global

object SequenceRoute extends TableDefinitions {

  def index: Reader[Env, Route] = Reader { env =>
    path("sequence") {
      post {
        complete {
          val id = env.repositories.sequenceRepository.create(("first", "path"))
          Marshal(id.toString()).to[HttpResponse]
        }
      }
    }
  }

}
