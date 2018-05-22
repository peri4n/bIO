package at.bioinform.webapp

import at.bioinform.webapp.components.{ConfigurationComponent, RepositoriesComponent}
import cats.data.Reader

trait Env extends ConfigurationComponent with RepositoriesComponent

object Env {

  val env = Reader[Env, Env](identity)

  val config = env map (_.config)

  val repositories = env map (_.repositories)

}
