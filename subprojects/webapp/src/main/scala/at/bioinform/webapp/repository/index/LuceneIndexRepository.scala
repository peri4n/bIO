package at.bioinform.webapp.repository.index

import java.nio.file.Path

import at.bioinform.webapp.db.TableDefinitions
import at.bioinform.webapp.model.LuceneIndex
import slick.jdbc.JdbcBackend.DatabaseFactoryDef

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object LuceneIndexRepository extends IndexRepository with TableDefinitions {

  override def get(index: Int) = ???

  override def update(index: LuceneIndex) = ???

  override def create(name: String, path: Path): Future[LuceneIndex] = {
    val Database = new DatabaseFactoryDef {}
    val db = Database.forConfig("database.test")
    db.run(insertLuceneIndex += (0, name, path.toString)) map {
      (LuceneIndex.apply _).tupled
    }
  }
}
