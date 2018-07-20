package at.bioinform.generators

import at.bioinform.core._
import org.scalacheck.Gen

object Generate {

  val nuc4 = Gen.oneOf(A, C, G, T)

  val dna4 = Gen.listOf(nuc4).map(_.mkString(""))

  val dna4WithPosition = for {
    sequence <- dna4
    position <- Gen.choose(0, sequence.length)
  } yield (sequence, position)
}
