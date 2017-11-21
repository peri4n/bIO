package at.bioinform.webapp.repository.index

import java.nio.file.Path

import at.bioinform.webapp.model.LuceneIndex
import at.bioinform.webapp.repository.Repositories

import scala.concurrent.Future

trait IndexRepository {

  def create(name: String, path: Path): Future[LuceneIndex]

  def get(index: Int): LuceneIndex

  def update(index: LuceneIndex): LuceneIndex
}

object IndexRepository {

  import Repositories.indexRepository

  def create(name: String, path: Path) = indexRepository map { _.create(name, path) }

  def get(index: Int) = indexRepository map { _.get(index)}

  def update(index: LuceneIndex) = indexRepository map { _.update(index)}
}
