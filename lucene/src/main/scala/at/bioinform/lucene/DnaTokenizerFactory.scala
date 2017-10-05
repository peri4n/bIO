package at.bioinform.lucene

import DnaTokenizerFactory._
import org.apache.lucene.analysis.util.TokenizerFactory
import org.apache.lucene.util.AttributeFactory

import scala.collection.JavaConverters._
import scala.collection.mutable

class DnaTokenizerFactory(args: Map[String, String]) extends TokenizerFactory(toJava(args)) {

  private val minGramSize = getInt(toJava(args), "minKmerSize", DnaTokenizer.DEFAULT_MIN_KMER_SIZE)

  private val maxGramSize = getInt(toJava(args), "maxKmerSize", DnaTokenizer.DEFAULT_MAX_KMER_SIZE)

  private val stranding = Strands.from(get(toJava(args), "stranding", DnaTokenizer.DEFAULT_STRANDING.toString))

  Predef.require(args.isEmpty, "Unknown parameters: " + args)

  override def create(factory: AttributeFactory) = new DnaTokenizer(factory, minGramSize, maxGramSize, stranding)

}

object DnaTokenizerFactory {

  private def toJava(args: Map[String, String]): java.util.Map[String, String] = (mutable.Map.empty[String, String] ++ args).asJava

}
