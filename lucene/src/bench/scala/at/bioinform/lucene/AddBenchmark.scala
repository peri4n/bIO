package at.bioinform.lucene

import java.nio.file.Files

import org.apache.lucene.document.{Document, Field, FieldType, TextField}
import org.apache.lucene.index._
import org.apache.lucene.store.MMapDirectory
import org.scalameter.{Bench, Key, Warmer}
import org.scalameter.api._

import scala.util.Random

class AddBenchmark extends Bench.LocalTime {

  //  val standardConfig = config(
  //    Key.exec.minWarmupRuns -> 20,
  //    Key.exec.maxWarmupRuns -> 40,
  //    Key.exec.benchRuns -> 100,
  //    Key.verbose -> true) withWarmer new Warmer.Default
  //
  //  val index = new MMapDirectory(Files.createTempDirectory("test"))
  //  val writer = new IndexWriter(index, new IndexWriterConfig(Util.analyzer(8, 8)))
  //
  //  val fieldType = new FieldType()
  //  fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS)
  //  fieldType.setStored(true)
  //  fieldType.setStoreTermVectors(true)
  //  fieldType.setTokenized(true)
  //  fieldType.setStoreTermVectorOffsets(true)
  //
  //  standardConfig.measure {
  //    for (i <- 0 until 1000) {
  //      val document = new Document()
  //      document.add(new Field("id", i.toString, TextField.TYPE_STORED))
  //
  //      document.add(new Field("sequence", randomSequence(1000), fieldType))
  //      writer.addDocument(document)
  //    }
  //  }
  //  writer.commit()

  def randomSequence(length: Int): String = {
    val random = new Random(System.currentTimeMillis())

    val builder = new StringBuilder(length)
    for (_ <- 0 until length) {
      builder += randomNuc(random)
    }

    builder.result()
  }

  def randomNuc(random: Random): Char = {
    random.nextInt(4) match {
      case 0 => 'a'
      case 1 => 'c'
      case 2 => 'g'
      case 3 => 't'
    }
  }
}
