package at.bioinform.lucene

import java.util.concurrent.TimeUnit

import org.apache.lucene.document.{Document, Field, FieldType, TextField}
import org.apache.lucene.index._
import org.apache.lucene.store.RAMDirectory
import org.openjdk.jmh.annotations._

import scala.util.Random

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
class IndexBenchmark {

  val FieldType = {
    val ft = new FieldType()
    ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS)
    ft.setStored(true)
    ft.setStoreTermVectors(true)
    ft.setTokenized(true)
    ft.setStoreTermVectorOffsets(true)
    ft
  }
  @Param(Array("100", "1000", "10000", "100000", "1000000"))
  var SequenceSize: Int = _
  var sequence: String = _
  var writer: IndexWriter = _

  @Setup
  def setUp(): Unit = {
    writer = new IndexWriter(new RAMDirectory(), new IndexWriterConfig(Analyzers.ngram(8, 8)))
    sequence = DnaGenerator.randomSequence(new Random(42), SequenceSize)
  }

  def tearDown(): Unit = {
    writer.commit()
    writer.close()
  }

  @Benchmark
  def indexingRandomDNA() {
    val document = new Document()
    document.add(new Field("id", "test", TextField.TYPE_STORED))
    document.add(new Field("sequence", sequence, FieldType))
    writer.addDocument(document)
  }
}
