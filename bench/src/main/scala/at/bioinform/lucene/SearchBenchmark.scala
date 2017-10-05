package at.bioinform.lucene

import java.nio.file.{Files, Path, Paths}

import org.apache.commons.io.FileUtils
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig, Term}
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.{Directory, MMapDirectory}
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

/**
  * - Search sequences of different lengths
  */
@Fork(1)
@State(Scope.Thread)
class SearchBenchmark {

  var directory: Directory = _

  var writer: IndexWriter = _

  var searcher: IndexSearcher = _

  var tempDir: Path = _

  @Param(Array("1000", "10000", "100000"))
  var sequencesInIndex: Int = _

  @Param(Array("1000", "10000", "100000"))
  var lengthOfSequences: Int = _

  def fillIndex(writer: IndexWriter, sequencesInIndex: Int, lengthOfSequences: Int) = {
    val random = new Random()
    for(i <- 0 until sequencesInIndex) {
      val doc = new Document()
      doc.add(new Field("id", i.toString, TextField.TYPE_STORED))
      doc.add(new Field("sequence", DnaGenerator.randomSequence(random, lengthOfSequences), TextField.TYPE_STORED))
     writer.addDocument(doc)
    }
    writer.commit()
  }

  @Setup
  def setUp(): Unit = {
    tempDir = createTempDir

    directory = new MMapDirectory(tempDir)

    writer = new IndexWriter(directory, new IndexWriterConfig(new PerFieldAnalyzerWrapper(
      new WhitespaceAnalyzer(),
      mutable.Map[String, Analyzer]("sequence" ->
        CustomAnalyzer.builder()
          .withTokenizer(
            classOf[NGramTokenizerFactory],
            mutable.Map(
              "minGramSize" -> "8",
              "maxGramSize" -> "8")
              .asJava)
          .build()).asJava)))

    fillIndex(writer, sequencesInIndex, lengthOfSequences)

    searcher = new IndexSearcher(DirectoryReader.open(new MMapDirectory(tempDir)))
  }

  private def createTempDir(): Path = {
    val outputDir = Paths.get("target/test")
    if (!Files.exists(outputDir)) {
      Files.createDirectory(outputDir)
    }
    Files.createTempDirectory(outputDir, "search-benchmark")
  }

  @TearDown
  def tearDown(): Unit = {
    writer.commit()
    writer.close()
    directory.close()
    FileUtils.forceDelete(tempDir.toFile)
  }

  @Benchmark
  def searchDna(blackhole: Blackhole): Unit = {
    val t1 = new Term("sequence", "AGCTTTAA")
    val seqQuery = new TermQuery(t1)
    val seqTopDocs = searcher.search(seqQuery, 10)
    blackhole.consume(seqTopDocs)
  }
}
