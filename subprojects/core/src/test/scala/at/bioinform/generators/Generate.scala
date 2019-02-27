package at.bioinform.generators

import at.bioinform.core.alphabet.dna._
import org.scalacheck.Gen

object Generate {

  val nuc4 = Gen.oneOf(A, C, G, T)

  val nuc5 = Gen.oneOf(A, C, G, T, N)

  val nuc4Char = Gen.oneOf('A', 'a', 'C', 'c', 'G', 'g', 'T', 't')

  val nuc5Char = Gen.oneOf('A', 'a', 'C', 'c', 'G', 'g', 'T', 't', 'N', 'n')

  val dna4 = Gen.listOf(nuc4).map(_.mkString(""))

  val dna5 = Gen.listOf(nuc5).map(_.mkString(""))

  val dna4WithLength = for {
    sequence <- dna4
    length <- Gen.choose(0, sequence.length)
  } yield (sequence, length)

  val dna5WithLength = for {
    sequence <- dna5
    length <- Gen.choose(0, sequence.length)
  } yield (sequence, length)
}
