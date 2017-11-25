package at.bioinform.webapp.repository.index

import java.nio.file.Path

import at.bioinform.webapp.db.{H2Database, TableDefinitions}
import at.bioinform.webapp.model.LuceneIndex

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object LuceneIndexRepository extends IndexRepository with TableDefinitions with H2Database {

  import api._

  override def update(index: LuceneIndex) = ???

  override def create(name: String, path: Path): Future[LuceneIndex] = {
    db.run(insertLuceneIndex += (0, name, path.toString)) map {
      (LuceneIndex.apply _).tupled
    }
  }

  override def get(index: Int) = {
    Await.result(db.run(luceneIndices.filter(_.id === index).result.head.map(LuceneIndex.tupled)), Duration.Inf)
  }

  override def findAll() = ???
}
