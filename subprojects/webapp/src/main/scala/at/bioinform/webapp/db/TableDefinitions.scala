package at.bioinform.webapp.db

trait TableDefinitions {

  import slick.jdbc.H2Profile.api._
  import slick.lifted.Tag

  val sequences = TableQuery[Sequences]

  val insertLuceneIndex = sequences returning sequences.map(e => e.id) into ((index, id) => index.copy(_1 = id))

  val schema = DBIO.seq(sequences.schema.create)

  case class Sequences(tag: Tag) extends Table[(Int, String, String)](tag, "SEQUENCES") {

    def * = (id, name, sequence)

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def sequence = column[String]("SEQUENCE")

  }

}
