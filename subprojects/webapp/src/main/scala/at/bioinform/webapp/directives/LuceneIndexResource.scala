package at.bioinform.webapp.directives

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import at.bioinform.model.db.TableDefinitions
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

trait LuceneIndexResource extends TableDefinitions {

  def luceneIndexRoutes =
    path("index") {
      post {
        complete {
          import akka.http.scaladsl.marshalling.GenericMarshallers._
          val db = Database.forConfig("h2mem1")
          try {
            val insert = insertLuceneIndex += (0, "first", "path1")
            Marshal(db.run(insert).map(_.toString())).to[HttpResponse]
          } finally db.close
        }
      }
    }

}
