package at.bioinform.lucene

import java.io.StringReader

import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, OffsetAttribute}

import scala.collection.JavaConverters._
import scala.collection.mutable

class DnaTokenizerTest extends org.scalatest.FunSpecLike {

  describe("A custom tokenizer") {

    it("should correctly tokenize a string.") {
      val reader = new StringReader("ACTATTAGCACGGG")

      val analyzer = CustomAnalyzer.builder()
        .withTokenizer("ngram", mutable.Map("minGramSize" -> "3", "maxGramSize" -> "4").asJava)
        .build()
      val stream = analyzer.tokenStream("test", reader)

      val offsetAtt = stream.addAttribute(classOf[OffsetAttribute])
      val termAtt = stream.addAttribute(classOf[CharTermAttribute])
      stream.reset()

      while (stream.incrementToken) {
        println("[" + termAtt.toString + "]")
        println("Token starting offset: " + offsetAtt.startOffset)
        println(" Token ending offset: " + offsetAtt.endOffset)
        println("")
      }

      stream.end()
      stream.close()
      analyzer.close()

    }
  }

}
