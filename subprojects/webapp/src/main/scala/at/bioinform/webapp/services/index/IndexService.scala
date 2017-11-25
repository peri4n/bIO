package at.bioinform.webapp.services.index

import java.nio.file.Paths

import at.bioinform.webapp.repository.index.IndexRepository

object IndexService {

  def getPath(indexId: Int) = for {
    index <- IndexRepository.get(indexId)
  } yield Paths.get(index.path)

}
