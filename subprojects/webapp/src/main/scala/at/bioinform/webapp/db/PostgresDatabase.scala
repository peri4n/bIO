package at.bioinform.webapp.db

import slick.jdbc.PostgresProfile.api._

class PostgresDatabase {

  val profile = slick.jdbc.PostgresProfile.api

  val db = Database.forConfig("database.h2mem")

}
