package at.bioinform.webapp.model.index

import java.nio.file.Path

import at.bioinform.webapp.db.{H2Database, TableDefinitions}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object LuceneIndexes extends IndexRepository with TableDefinitions with H2Database {

  import slick.jdbc.H2Profile.api._

  override def update(index: LuceneIndex) = ???

  override def create(name: String, path: Path): LuceneIndex = {
    Await.result(db.run(insertLuceneIndex += (0, name, path.toString)).map((LuceneIndex.fromPath _).tupled), Duration.Inf)
  }

  override def get(index: Int) = {
    Await.result(db.run(luceneIndices.filter(_.id === index).result.head).map {
      (LuceneIndex.fromPath _).tupled
    }, Duration.Inf)
  }

  override def findAll() = ???
}
