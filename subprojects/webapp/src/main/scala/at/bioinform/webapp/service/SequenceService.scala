package at.bioinform.webapp.service

import at.bioinform.webapp.repository.SequenceRepository

class SequenceService {

  def getSequence(id: String) = for {
    seq <- SequenceRepository.getById(id)
  } yield seq._2

}
