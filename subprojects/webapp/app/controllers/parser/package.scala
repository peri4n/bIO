package controllers

import java.nio.file.Paths

import akka.stream.IOResult
import akka.stream.scaladsl.{Flow, Keep}
import at.bioinform.lucene.segment.Segment
import at.bioinform.stream.util.Splitter
import org.apache.lucene.document.Document
import org.apache.lucene.store.SimpleFSDirectory

package object parser {

  val fasta: BodyParser[IOResult] = BodyParser { req =>
    val sink = FastaFlow(Splitter.withSize(10, 2))
      .via(Flow[Segment].map(_ => new Document()))
      .toMat(LuceneSink(new SimpleFSDirectory(Paths.get("target/database"))))(Keep.right)
    Accumulator(sink).map(Right.apply)
  }

}
