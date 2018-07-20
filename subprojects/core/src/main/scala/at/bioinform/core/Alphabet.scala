package at.bioinform.core

/**
  * Fixed size alphabet
  * @tparam T type of the elements
  */
trait Alphabet[T <: Symbol] {

  type elementType = T

  /** Size of the alphabet */
  def size: Int = elements.size

  /** Number of bits to store a symbol of this alphabet */
  def bitsPerSymbol: Int = (math.log(size) / math.log(2)).toInt

  /** Elements of the alphabet */
  def elements: List[elementType]

  /** Converts a symbol to an `Int` */
  def toInt( symbol: elementType ): Int = elements.indexOf( symbol )

  def toInt( char: Char): Int = elements.indexWhere( _.char == char)

  /** Converts an `Int` to a symbol */
  def fromInt( index: Int): elementType = elements(index)

  def fromChar( char: Char): elementType = elements.find( _.char == char).getOrElse(elements.head)


}
