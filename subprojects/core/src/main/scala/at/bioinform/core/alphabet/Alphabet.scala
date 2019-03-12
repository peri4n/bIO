package at.bioinform.core.alphabet

/**
  * Fixed size alphabet
  */
trait Alphabet {

  self =>

  type elemType <: Symbol

  /** Elements of the alphabet */
  def elements: List[elemType]

  /** Are symbols case sensitive? */
  def isCaseSensitive: Boolean

  /** Converts a symbol to an `Int` */
  def toInt(symbol: elemType): Int = elements.indexOf(symbol)

  def toInt(char: Char): Int = elements.zipWithIndex.find(e => compareChars(e._1.char, char)).map(_._2).getOrElse(0)

  /** Converts an `Int` to a symbol */
  def fromInt(index: Int): elemType = elements(index)

  def fromChar(char: Char): elemType = elements.find(e => compareChars(e.char, char)).getOrElse(elements.head)

  def bitUtil: BitUtil = new BitUtil(self) {

    override def positionInChunk(pos: Int): Int = pos % symbolsPerChunk

    override def indexOfChunk(pos: Int): Int = pos / symbolsPerChunk

    override def symbolsPerChunk(): Int = BitsPerLong / bitsPerSymbol

  }

  /** Size of the alphabet */
  def size: Int = elements.size

  private def compareChars(char1: Char, char2: Char) = {
    if (isCaseSensitive) {
      char1 == char2
    } else {
      Character.toUpperCase(char1) == Character.toUpperCase(char2)
    }
  }

}
