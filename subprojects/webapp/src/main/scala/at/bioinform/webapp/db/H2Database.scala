package at.bioinform.webapp.db

import slick.jdbc.H2Profile.api._

trait H2Database {

  val api = slick.jdbc.H2Profile.api

  val db = Database.forConfig("database.h2mem")

}

