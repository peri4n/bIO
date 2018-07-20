package at.bioinform.core

import at.bioinform.seq.Seq

object DNA4 extends Alphabet[Nuc] {

  override val size= 4

  override val bitsPerSymbol = 2

  val elements = List(A, C, G, T)

  /** For performance reasons */
  private val nucleotides = Array(A, C, G, T)

  override def fromInt(index: Int): Nuc = nucleotides(index)

  /** Custom string interpolation */
  implicit class Dna4Helper(val sc: StringContext) extends AnyVal {

    def dna4(args: Any*): Seq[DNA4.type] = Seq(sc.parts.mkString)(DNA4)

  }

  implicit def dna4Implicit  = DNA4

}
