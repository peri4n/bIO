package at.bioinform.seq

import at.bioinform.core.Alphabet

case class ChainBuilder[A <: Alphabet](initialCapacity: Int)(implicit val alphabet: A) {

  import ChainBuilder._

  private val util = alphabet.bitUtil
  import util._

  private var buffer = bufferForSymbols(initialCapacity)

  private var length = 0

  private var capacity = initialCapacity

  private def ensureCapacity(sequence: Chain[A]) = {
    val occupiedBits = bitsToStore(length)
    val freeBits = buffer.length * 64 - occupiedBits

    if (bitsToStore(sequence.length) > freeBits) {
      // allocate memory
      val newCapacity = capacity * GrowthFactor
      val newBuffer = bufferForSymbols(newCapacity)

      // copy old buffer to new buffer
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length)

      // update state
      buffer = newBuffer
      capacity = newCapacity
    }
  }

  def +=(sequence: Chain[A]): Unit = {
    ensureCapacity(sequence)



    length += sequence.length
  }

  def +=(sequence: String): Unit = this += Chain.fromString(sequence)

  def build(): Chain[A] = new Chain(buffer, length)

  override def toString: String = build().toString

}

object ChainBuilder {

  private val GrowthFactor = 4

}
