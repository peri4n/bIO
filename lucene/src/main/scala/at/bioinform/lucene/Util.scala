package at.bioinform.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory
import org.apache.lucene.analysis.standard.StandardAnalyzer

import scala.collection.JavaConverters._
import scala.collection.mutable

object Util {

  def analyzer = new PerFieldAnalyzerWrapper(
    new StandardAnalyzer(),
    mutable.Map[String, Analyzer]("sequence" -> CustomAnalyzer.builder()
      .withTokenizer(
        classOf[NGramTokenizerFactory],
        mutable.Map("minGramSize" -> "3", "maxGramSize" -> "4").asJava)
      .build()).asJava)
}
