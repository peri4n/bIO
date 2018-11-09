package at.bioinform.core

import at.bioinform.seq.Chain

object DNA4 extends Alphabet {

  override type elemType = Nuc

  override val size = 4

  /** Converts a symbol to an `Int` */
  override def toInt(symbol: Nuc): Int = symbol match {
    case A => 0
    case C => 1
    case G => 2
    case T => 3
  }

  val elements = List(A, C, G, T)

  override def toInt(char: Char): Int = char match {
    case 'A' | 'a' => 0
    case 'C' | 'c' => 1
    case 'G' | 'g' => 2
    case 'T' | 't' => 3
    case _ => 0
  }

  /** For performance reasons */
  private val nucleotides = Array(A, C, G, T)

  override def fromInt(index: Int): Nuc = if (0 <= index && index < 4) nucleotides(index) else A

  override def fromChar(char: Char): Nuc = char match {
    case 'A' | 'a' => A
    case 'C' | 'c' => C
    case 'G' | 'g' => G
    case 'T' | 't' => T
    case _ => A
  }

  override def bitUtil = Dna4BitUtil

  /** Custom string interpolation */
  implicit class Dna4Helper(val sc: StringContext) extends AnyVal {

    def dna4(args: Any*): Chain[DNA4.type] = Chain(sc.parts.mkString)(DNA4)

  }

  implicit def dna4Implicit = DNA4

  object Dna4BitUtil extends BitUtil(DNA4) {

    @inline def positionInChunk(pos: Int) = pos % 32

    @inline def indexOfChunk(pos: Int) = pos / 32

    @inline def symbolsPerChunk() = 32

    @inline override def bitsPerSymbol: Int = 2
  }

}
