package at.bioinform.webapp.repository

import at.bioinform.webapp.repository.index.IndexRepository
import at.bioinform.webapp.Env

trait Repositories {

  def indexRepository: IndexRepository

}

object Repositories {

  import Env.repositories

  val indexRepository = repositories map { _.indexRepository }

}
