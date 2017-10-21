package at.bioinform.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable

object Analyzer {

  def ngram(minNGramSize: Int, maxNGramSize: Int) = {
    val nGramTokenizerParameter = mutable.Map(
      "minGramSize" -> minNGramSize.toString,
      "maxGramSize" -> maxNGramSize.toString).asJava

    val sequenceAnalyzer = CustomAnalyzer.builder()
      .withTokenizer(classOf[NGramTokenizerFactory], nGramTokenizerParameter)
      .build()

    new PerFieldAnalyzerWrapper(
      new WhitespaceAnalyzer(),
      mutable.Map[String, Analyzer]("sequence" -> sequenceAnalyzer).asJava)
  }
}
