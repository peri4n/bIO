package at.bioinform.webapp

import at.bioinform.webapp.repository.SequenceRepository

trait Repositories {

  def sequenceRepository: SequenceRepository

}

object Repositories {

  import Env.repositories

  val sequenceRepository = repositories map {
    _.sequenceRepository
  }

}
