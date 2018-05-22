package at.bioinform.webapp.services.index

import at.bioinform.webapp.model.index.IndexRepository

object IndexService {

  def getPath(indexId: Int) = for {
    index <- IndexRepository.get(indexId)
  } yield index.path

}
