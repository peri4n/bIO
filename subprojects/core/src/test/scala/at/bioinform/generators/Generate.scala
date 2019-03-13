package at.bioinform.generators

import at.bioinform.core.alphabet.amino._
import at.bioinform.core.alphabet.dna._
import at.bioinform.core.alphabet.Symbol
import org.scalacheck.Gen

object Generate {

  /**
    * Single residue generators.
    */
  val nuc4: Gen[Nuc4] = Gen.oneOf(DNA4.elements).label("DNA4 nucleotide")

  val nuc5: Gen[Nuc5] = Gen.oneOf(DNA5.elements).label("DNA5 nucleotide")

  val amino20: Gen[AA20] = Gen.oneOf(Amino20.elements).label("Amino20 amino acid")

  /**
    * Single residue index generators.
    */
  val nuc4Index: Gen[Int] = Gen.oneOf(Range(0, DNA4.size)).label("DNA4 symbol index")

  val nuc5Index: Gen[Int] = Gen.oneOf(Range(0, DNA5.size)).label("DNA5 nucleotide")

  val amino20Index: Gen[Int] = Gen.oneOf(Range(0, Amino20.size)).label("Amino20 amino acid")

  /**
    * Character of single residue generators.
    */
  val nuc4Char: Gen[Char] = shuffleCaseOf(nuc4)

  val nuc5Char: Gen[Char] = shuffleCaseOf(nuc5)

  val amino20Char: Gen[Char] = shuffleCaseOf(amino20)

  private def shuffleCaseOf[A <: Symbol](alphabet: Gen[A]) = {
    for {
      coin <- Gen.oneOf(true, false)
      symbol <- alphabet.map(_.char)
    } yield if (coin) Character.toUpperCase(symbol) else Character.toLowerCase(symbol)
  }

  /**
    * String of residues generators.
    */
  val dna4 = Gen.listOf(nuc4).map(_.mkString(""))

  val dna5 = Gen.listOf(nuc5).map(_.mkString(""))

  /**
    * String of residues and their lengths generators.
    */
  val dna4WithLength = for {
    sequence <- dna4
    length <- Gen.choose(0, sequence.length)
  } yield (sequence, length)

  val dna5WithLength = for {
    sequence <- dna5
    length <- Gen.choose(0, sequence.length)
  } yield (sequence, length)
}
