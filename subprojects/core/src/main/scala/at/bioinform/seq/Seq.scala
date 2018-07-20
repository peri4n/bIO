package at.bioinform.seq

import java.util
import java.util.Objects

import at.bioinform.core.{Alphabet, DNA4}

class Seq[A <: Alphabet[_]] private (private val bits: Array[Long], val length: Int)(implicit val alphabet: A) {

  import Seq._

  def substring(start: Int, length: Int): Seq[A] = {
    val buffer = new Array[Long]( length / symbolsPerLong(alphabet) + 1)
    for ((position, positionInSubString) <- (start until start + length).zipWithIndex) {
      val symbolBits = bitPatternAtPosition(position)
      val chunkIndex = indexOfChunk(positionInSubString, symbolsPerLong(alphabet))
      buffer(chunkIndex) |= symbolBits << (positionInChunk(positionInSubString, alphabet) * alphabet.bitsPerSymbol)
    }
    new Seq(buffer, length)(alphabet)
  }

  def substring(start: Int): Seq[A] = {
    substring(start, length - start)
  }

  private def bitPatternAtPosition(pos: Int) = {
    val chunk = bits(indexOfChunk(pos, symbolsPerLong(alphabet)))
    val shift = positionInChunk(pos, alphabet) * alphabet.bitsPerSymbol
    val mask = bitMask(alphabet.bitsPerSymbol) << shift
    (chunk & mask) >>> shift
  }

  private def bitMask(bitsPerSymbol: Int) = (1L << bitsPerSymbol) - 1

  def apply(pos: Int): alphabet.elementType = {
    val bitMaskOfSymbol = bitPatternAtPosition(pos)
    alphabet.fromInt(bitMaskOfSymbol.toInt)
  }

  def qGrams(q: Int, step: Int = 1): Iterable[Seq[A]] = q match {
    case 0 => List(Seq(""))
    case _ => Iterable.range(0, length - q + 1, step).map { i => substring(i, q) }
  }

  def qGrams(qs: Iterable[Int], step: Int): Iterable[Seq[A]] = {
    qs.flatMap(q => qGrams(q, step))
  }

  def qGrams(qMin: Int, qMax: Int, step: Int): Iterable[Seq[A]] = qGrams(qMin to qMax, step)

  def bitPattern: String = bits.map( long => String.format(s"%${length * alphabet.bitsPerSymbol}s", java.lang.Long.toBinaryString(long)).replace(' ', '0')).mkString("")

  override def equals(obj: scala.Any): Boolean = {
     if (obj == null) {
        return false
    }
    if (!getClass.isAssignableFrom(obj.getClass)) {
        return false
    }
    val other = obj.asInstanceOf[Seq[_]]
    if (other.length != length) {
      return false
    }
    if (!util.Arrays.equals(bits, other.bits)) {
      return false
    }
    true
  }

  override def hashCode(): Int = Objects.hash(Int.box(util.Arrays.hashCode(bits)), Int.box(length))

}

object Seq {

  private val BitsPerLong = 64

  def apply[A <: Alphabet[_]](value: String )(implicit alphabet: A ): Seq[A] = {
    val numberOfCharacters = value.length
    val symbolsPerChunk = symbolsPerLong(alphabet)
    val buffer = new Array[Long]( numberOfCharacters / symbolsPerChunk + 1)

    for((symbol, pos) <- value.zipWithIndex) {
      val chunkIndex = indexOfChunk(pos, symbolsPerChunk)
      val symbolBits = alphabet.toInt(symbol).toLong
      val shift = positionInChunk(pos, alphabet) * alphabet.bitsPerSymbol
      buffer(chunkIndex) |= symbolBits << shift
    }
    new Seq(buffer, numberOfCharacters)(alphabet)
  }

  @inline private def positionInChunk(pos: Int, alphabet: Alphabet[_]) = pos % symbolsPerLong(alphabet)

  @inline private def indexOfChunk(pos: Int, symbolsPerChunk: Int) = pos / symbolsPerChunk

  @inline private def symbolsPerLong(alphabet: Alphabet[_]) = BitsPerLong / alphabet.size

  implicit def fromString[A <: Alphabet[_]](x: String)(implicit alphabet: A): Seq[A] = Seq(x)(alphabet)

  def fromDna4(x: String) = Seq(x)(DNA4)

}
