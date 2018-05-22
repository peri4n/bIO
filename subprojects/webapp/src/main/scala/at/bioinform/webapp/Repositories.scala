package at.bioinform.webapp

import at.bioinform.webapp.model.index.IndexRepository

trait Repositories {

  def indexRepository: IndexRepository

}

object Repositories {

  import Env.repositories

  val indexRepository = repositories map { _.indexRepository }

}
