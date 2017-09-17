package at.bioinform.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable

object Util {

  def analyzer = new PerFieldAnalyzerWrapper(
    new WhitespaceAnalyzer(),
    mutable.Map[String, Analyzer]("sequence" -> CustomAnalyzer.builder()
      .withTokenizer(
        classOf[NGramTokenizerFactory],
        mutable.Map("minGramSize" -> "6", "maxGramSize" -> "6").asJava)
      .build()).asJava)
}
