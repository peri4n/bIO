package at.bioinform.webapp.db

import at.bioinform.webapp.Env
import cats.data.Reader
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.DatabaseFactoryDef

object DatabaseProvider {

  val Database = new DatabaseFactoryDef {}

  def database(dbName: String) : Reader[Env, JdbcBackend.Database] = Env.config map { config =>
   Database.forConfig(dbName, config)
  }

}
