package at.bioinform.webapp.directive

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import at.bioinform.webapp.Env
import at.bioinform.webapp.ui.IndexPage
import cats.data.Reader

object Main {

  val routes: Reader[Env, Route] = Reader { env =>
    path("") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, IndexPage.apply()))
      }
    } ~ Sequences.routes.run(env)
  }

}
