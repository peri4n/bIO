package at.bioinform.lucene

import org.apache.lucene.analysis.Tokenizer
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, OffsetAttribute, PositionIncrementAttribute}
import org.apache.lucene.util.AttributeFactory

case class DnaTokenizer(factory: AttributeFactory, minKmerSize: Int, maxKmerSize: Int, strands: Strands) extends Tokenizer(factory) {

  private val termAtt = addAttribute(classOf[CharTermAttribute])
  private val offsetAtt = addAttribute(classOf[OffsetAttribute])
  private val posIncAtt = addAttribute(classOf[PositionIncrementAttribute])

  override def incrementToken(): Boolean = {
    clearAttributes()

    ???
  }
}

object DnaTokenizer {

  val DEFAULT_MIN_KMER_SIZE = 3

  val DEFAULT_MAX_KMER_SIZE = 5

  val DEFAULT_STRANDING: Strands = Both

}
