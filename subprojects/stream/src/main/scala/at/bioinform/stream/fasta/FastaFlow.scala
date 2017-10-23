package at.bioinform.stream.fasta

import java.net.URI
import java.nio.file.{Path, Paths}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Source}
import akka.util.ByteString
import at.bioinform.lucene.segment.Segment
import at.bioinform.stream.util.Splitter

import scala.concurrent.Future

object FastaFlow {

  /** Maximal allowed line size in a FASTA file. */
  val MaxLineSize = 200

  /** Character the begins a FASTA line comment. */
  val FastaCommentStart = ByteString("#")

  /**
    * Utility method to easily create a processor from a given URI.
    *
    * @param uri URI to a FASTA formatted file.
    * @return a flow providing [[Segment]]s
    */
  def from(uri: URI, splitter: Splitter): Source[Segment, Future[IOResult]] = from(Paths.get(uri), splitter)

  /**
    * Utility method to easily create a processor from a given path.
    *
    * @param path path to a FASTA formatted file.
    * @return a flow providing [[Segment]]s
    */
  def from(path: Path, splitter: Splitter): Source[Segment, Future[IOResult]] = {
    FileIO.fromPath(path).via(FastaFlow(splitter))
  }

  /** A flow that parses [[akka.util.ByteString]] into [[at.bioinform.stream.fasta.FastaEntry]] */
  def apply(splitter: Splitter) = Framing.delimiter(ByteString(System.lineSeparator()), MaxLineSize)
    .via(Flow[ByteString].filter(ignoreLine))
    .via(FastaParser(splitter))
    .named("FastaFlow")

  /**
    * Filter for lines that are ignored in the FASTA format.
    *
    * @param line the line to be checked
    * @return true if the line is empty or starts with a '#'
    */
  private def ignoreLine(line: ByteString): Boolean = {
    line.isEmpty || line.startsWith(FastaCommentStart)
  }
}
