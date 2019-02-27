package at.bioinform.core.alphabet.dna

import at.bioinform.core.alphabet.{Alphabet, BitUtil}
import at.bioinform.seq.Chain

object DNA5 extends Alphabet {

  override type elemType = Nuc5

  override val size = 5

  val elements = List(A, C, G, T, N)

  /** For performance reasons */
  private val nucleotides = Array(A, C, G, T, N)

  /** Converts a symbol to an `Int` */
  override def toInt(symbol: Nuc5): Int = symbol match {
    case A => 0
    case C => 1
    case G => 2
    case T => 3
    case N => 4
  }

  override def toInt(char: Char): Int = char match {
    case 'A' | 'a' => 0
    case 'C' | 'c' => 1
    case 'G' | 'g' => 2
    case 'T' | 't' => 3
    case 'N' | 'n' => 4
    case _ => 0
  }

  override def fromInt(index: Int): Nuc5 = if (0 <= index && index < 5) nucleotides(index) else A

  override def fromChar(char: Char): Nuc5 = char match {
    case 'A' | 'a' => A
    case 'C' | 'c' => C
    case 'G' | 'g' => G
    case 'T' | 't' => T
    case 'N' | 'n' => N
    case _ => A
  }

  override def bitUtil = Dna5BitUtil

  /** Custom string interpolation */
  implicit class Dna5Helper(val sc: StringContext) extends AnyVal {

    def dna5(args: Any*): Chain[DNA5.type] = Chain(sc.parts.mkString)(DNA5)

  }

  implicit def dna5Implicit = DNA5

  object Dna5BitUtil extends BitUtil(DNA5) {

    @inline def positionInChunk(pos: Int) = pos % 32

    @inline def indexOfChunk(pos: Int) = pos / 32

    @inline def symbolsPerChunk() = 21

    @inline override def bitsPerSymbol: Int = 3
  }

}
