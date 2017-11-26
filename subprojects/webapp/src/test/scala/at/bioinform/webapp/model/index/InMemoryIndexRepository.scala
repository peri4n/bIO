package at.bioinform.webapp.model.index

import java.nio.file.Path

object InMemoryIndexRepository extends IndexRepository {

  override def create(name: String, path: Path) = ???

  override def get(index: Int) = ???

  override def update(index: LuceneIndex) = ???

  override def findAll() = ???
}
