package at.bioinform.core

/**
  * Fixed size alphabet
  *
  * @tparam T type of the elements
  */
trait Alphabet {

  self =>

  type elemType <: Symbol

  /** Elements of the alphabet */
  def elements: List[elemType]

  /** Converts a symbol to an `Int` */
  def toInt(symbol: elemType): Int = elements.indexOf(symbol)

  def toInt(char: Char): Int = elements.zipWithIndex.find(_._1.char == char).map(_._2).getOrElse(0)

  /** Converts an `Int` to a symbol */
  def fromInt(index: Int): elemType = elements(index)

  def fromChar(char: Char): elemType = elements.find(_.char == char).getOrElse(elements.head)

  def bitUtil: BitUtil = new BitUtil(self) {

    override def positionInChunk(pos: Int): Int = pos % symbolsPerChunk

    override def indexOfChunk(pos: Int): Int = pos / symbolsPerChunk

    override def symbolsPerChunk(): Int = BitsPerLong / bitsPerSymbol

  }

  /** Size of the alphabet */
  def size: Int = elements.size

}
