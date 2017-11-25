package at.bioinform.webapp.repository.index

import java.nio.file.Path

import at.bioinform.webapp.model.LuceneIndex

object InMemoryIndexRepository extends IndexRepository {

  override def create(name: String, path: Path) = ???

  override def get(index: Int) = ???

  override def update(index: LuceneIndex) = ???

  override def findAll() = ???
}
