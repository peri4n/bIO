package at.bioinform.webapp.repository

trait SequenceRepository {

  type Seq = (String, String)

  def create(sequence: Seq): Seq

  def getById(id: String): Seq
}

object SequenceRepository {

  import at.bioinform.webapp.Repositories.sequenceRepository

  def create(name: String, sequence: String) = sequenceRepository map {
    _.create((name, sequence))
  }

  def getById(id: String) = sequenceRepository map {
    _.getById(id)
  }

}
