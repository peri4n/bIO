package at.bioinform.generators

import at.bioinform.core._
import org.scalacheck.Gen

object Generate {

  val nuc4 = Gen.oneOf(A, C, G, T)

  val nuc4Char = Gen.oneOf('A', 'a', 'C', 'c', 'G', 'g', 'T', 't')

  val dna4 = Gen.listOf(nuc4).map(_.mkString(""))

  val dna4WithLength = for {
    sequence <- dna4
    length <- Gen.choose(0, sequence.length)
  } yield (sequence, length)
}
