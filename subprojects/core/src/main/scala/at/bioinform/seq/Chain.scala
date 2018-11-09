package at.bioinform.seq

import java.util.Arrays
import java.util.Objects

import at.bioinform.core.alphabet.Alphabet
import at.bioinform.core.alphabet.dna.DNA4

class Chain[A <: Alphabet] private[seq](private[seq] val bits: Array[Long], val length: Int)(implicit val alphabet: A) {

  val util = alphabet.bitUtil
  import util._

  def substring(start: Int, length: Int): Chain[A] = {
    val buffer = bufferForSymbols(length)
    for ((position, positionInSubString) <- (start until start + length).zipWithIndex) {
      val symbolBits = getPatternAtPosition(position, bits)
      setPatternAtPosition(symbolBits, positionInSubString, buffer)
    }
    new Chain(buffer, length)(alphabet)
  }

  def substring(start: Int): Chain[A] = {
    substring(start, length - start)
  }

  def apply(pos: Int): alphabet.elemType = {
    val bitMaskOfSymbol = getPatternAtPosition(pos, bits)
    alphabet.fromInt(bitMaskOfSymbol.toInt)
  }

  def qGrams(q: Int, step: Int = 1): Iterable[Chain[A]] = q match {
    case 0 => List(Chain(""))
    case _ => Iterable.range(0, length - q + 1, step).map { i => substring(i, q) }
  }

  def qGrams(qs: Iterable[Int], step: Int): Iterable[Chain[A]] = {
    qs.flatMap(q => qGrams(q, step))
  }

  def qGrams(qMin: Int, qMax: Int, step: Int): Iterable[Chain[A]] = qGrams(qMin to qMax, step)

  def bitPattern: String = bits.map( long => String.format(s"%${bitsToStore(length)}s", java.lang.Long.toBinaryString(long)).replace(' ', '0')).mkString("")

  override def equals(obj: scala.Any): Boolean = {
     if (obj == null) {
        return false
    }
    if (!getClass.isAssignableFrom(obj.getClass)) {
        return false
    }
    val other = obj.asInstanceOf[Chain[_]]
    if (other.length != length) {
      return false
    }
    if (!Arrays.equals(bits, other.bits)) {
      return false
    }
    true
  }

  override def hashCode(): Int = Objects.hash(Int.box(Arrays.hashCode(bits)), Int.box(length))

  override def toString: String = (0 until length).foldLeft(new StringBuilder(length)) { (builder, position) =>
    val symbolPattern = getPatternAtPosition(position,bits)
    builder += alphabet.fromInt(symbolPattern.toInt).char
  }.result()

}

object Chain {

  def apply[A <: Alphabet](value: String )(implicit alphabet: A ): Chain[A] = {
    val util = alphabet.bitUtil
    import util._

    val numberOfCharacters = value.length
    val buffer = bufferForSymbols(numberOfCharacters)

    for((symbol, pos) <- value.zipWithIndex) {
      val symbolBits = alphabet.toInt(symbol).toLong
      setPatternAtPosition(symbolBits, pos, buffer)
    }
    new Chain(buffer, numberOfCharacters)(alphabet)
  }


  implicit def fromString[A <: Alphabet](x: String)(implicit alphabet: A): Chain[A] = Chain(x)(alphabet)

  def fromDna4(x: String) = Chain(x)(DNA4)

}
