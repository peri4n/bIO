package at.bioinform.webapp.db

trait TableDefinitions {

  import slick.jdbc.H2Profile.api._
  import slick.lifted.Tag

  case class LuceneIndices(tag: Tag) extends Table[(Int, String, String)](tag, "LUCENE_INDICES") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def path = column[String]("PATH")

    def * = (id, name, path)

  }

  val luceneIndices = TableQuery[LuceneIndices]

  val insertLuceneIndex = luceneIndices returning luceneIndices.map(e => e.id) into ((index, id) => index.copy(_1 = id))

  val schema = DBIO.seq(
    luceneIndices.schema.create)
}
