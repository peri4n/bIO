package at.bioinform.webapp.model.index

import java.nio.file.Path

import at.bioinform.webapp.Repositories

trait IndexRepository {

  def create(name: String, path: Path): LuceneIndex

  def get(index: Int): LuceneIndex

  def update(index: LuceneIndex): LuceneIndex

  def findAll(): Seq[LuceneIndex]
}

object IndexRepository {

  import Repositories.indexRepository

  def create(name: String, path: Path) = indexRepository map {
    _.create(name, path)
  }

  def get(index: Int) = indexRepository map {
    _.get(index)
  }

  def update(index: LuceneIndex) = indexRepository map {
    _.update(index)
  }
}
