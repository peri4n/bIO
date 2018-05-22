package at.bioinform.webapp.model.index

import java.nio.file.{Path, Paths}

case class LuceneIndex(id: Int, name: String, path: Path) {

}

object LuceneIndex {

  def fromPath(id: Int, name: String, path: String) = LuceneIndex(id, name, Paths.get(path))
}
