package at.bioinform.webapp.directive

import akka.http.scaladsl.marshalling.GenericMarshallers._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import at.bioinform.webapp.Env
import at.bioinform.webapp.db.TableDefinitions
import cats.data.Reader

import scala.concurrent.ExecutionContext.Implicits.global

object Sequences extends TableDefinitions {

  val routes: Reader[Env, Route] = Reader { env =>
    val sequenceRepository = env.repositories.sequenceRepository

    pathPrefix("sequence" ) {
      get {
        complete {
          Marshal("Hello world!").to[HttpResponse]
        }
      }
      post {
        complete {
          val id = sequenceRepository.create(("first", "path"))
          Marshal(id.toString()).to[HttpResponse]
        }
      } ~
      path(IntNumber) { id =>
        get {
          complete {
            Marshal(s"$id").to[HttpResponse]
          }
        }
      }
    }
  }

}
