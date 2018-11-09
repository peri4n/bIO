package at.bioinform.core

abstract class BitUtil(private val alphabet: Alphabet) {

  val BitsPerLong = 64

  @inline def positionInChunk(pos: Int): Int

  @inline def indexOfChunk(pos: Int): Int

  @inline def symbolsPerChunk(): Int

  /** Number of bits to store a symbol of this alphabet */
  @inline def bitsPerSymbol: Int = (math.log(alphabet.size) / math.log(2)).toInt

  @inline def bitsToStore(numberOfSymbols: Int) = numberOfSymbols * bitsPerSymbol

  def bufferForSymbols(numberOfSymbols: Int): Array[Long] = new Array[Long]( math.ceil(numberOfSymbols.toDouble / symbolsPerChunk).toInt)

  def setPatternAtPosition(pattern: Long, position: Int, buffer: Array[Long]) = {
    val chunkIndex = indexOfChunk(position)
    buffer(chunkIndex) |= pattern << positionInChunk(position) * bitsPerSymbol
  }

  def getPatternAtPosition(pos: Int, buffer: Array[Long]): Long = {
    val chunk = buffer(indexOfChunk(pos))
    val shift = positionInChunk(pos) * bitsPerSymbol
    val mask = bitMask(bitsPerSymbol) << shift
    (chunk & mask) >>> shift
  }

  private def bitMask(bitsPerSymbol: Int) = (1L << bitsPerSymbol) - 1

}
