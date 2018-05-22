package at.bioinform.webapp.db

trait H2Database extends Database {

  val profile = slick.jdbc.H2Profile

  import slick.jdbc.H2Profile.api._

  val db = Database.forConfig("database.test")

}

