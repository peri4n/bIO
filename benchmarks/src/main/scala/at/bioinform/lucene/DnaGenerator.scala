package at.bioinform.lucene

import scala.util.Random

object DnaGenerator {

  def randomSequence(random: Random, length: Int): String = {
    val builder = new StringBuilder(length)
    for (_ <- 0 until length) {
      builder += randomNuc(random)
    }

    builder.result()
  }

  def randomNuc(random: Random): Char = {
    random.nextInt(4) match {
      case 0 => 'a'
      case 1 => 'c'
      case 2 => 'g'
      case 3 => 't'
    }
  }

}
