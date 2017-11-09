package at.bioinform.webapp.directives

import at.bioinform.model.db.TableDefinitions
import cats.data.Reader
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object LuceneIndexRepository extends TableDefinitions {

  def createIndex(name: String, path: String): Reader[JdbcBackend.Database, Future[Int]] = Reader { db =>
    val insert = insertLuceneIndex += (0, name, path)
    db.run(insert.map(_._1))
  }

}
