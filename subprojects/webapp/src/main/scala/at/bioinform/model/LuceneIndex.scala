package at.bioinform.model

import slick.lifted.Tag
import slick.jdbc.H2Profile.api._

case class LuceneIndex(tag: Tag) extends Table[(Int, String, String)](tag, "LUCENE_INDICES") {

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def path = column[String]("PATH")

  def * = (id, name, path)

}
