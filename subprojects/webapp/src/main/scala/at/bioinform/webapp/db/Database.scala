package at.bioinform.webapp.db

import slick.jdbc.JdbcProfile

trait Database {

  val profile: JdbcProfile

}
