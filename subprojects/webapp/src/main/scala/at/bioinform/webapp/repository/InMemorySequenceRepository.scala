package at.bioinform.webapp.repository

import scala.collection.mutable

object InMemorySequenceRepository extends SequenceRepository {

  val db = mutable.HashMap[String, (String, String)]()

  override def create(sequence: Seq): Seq = {
    val pair = sequence._1 -> sequence._2
    db += pair._1 -> pair
    pair
  }

  override def getById(id: String): Seq = {
    db.getOrElse(id, "" -> "")
  }
}
