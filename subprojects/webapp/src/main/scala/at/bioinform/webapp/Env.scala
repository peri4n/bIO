package at.bioinform.webapp

import at.bioinform.webapp.repository.Repositories
import cats.data.Reader
import com.typesafe.config.Config

trait Env {

  def config: Config

  def repositories: Repositories

}

object Env {

  val env = Reader[Env, Env](identity)

  val config = env map { _.config }

  val repositories = env map { _.repositories }

}
