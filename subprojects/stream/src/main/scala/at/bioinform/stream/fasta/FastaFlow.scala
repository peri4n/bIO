package at.bioinform.stream.fasta

import java.net.URI
import java.nio.file.{Path, Paths}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Framing, Source}
import akka.util.ByteString

import scala.concurrent.Future

/**
 * Flow that takes a FASTA formated input and returns actual FASTA entries.
 */
object FastaFlow {

  /** Maximal allowed line size in a FASTA file. */
  val MAX_LINE_SIZE = 200

  def apply() = Framing.delimiter(ByteString(System.lineSeparator()), MAX_LINE_SIZE).via(FastaParser)

  /**
   * Utility method to easily create a processor from a given path.
   *
   * @param path path to a FASTA formatted file.
   * @return a flow providing [[FastaEntry]]s
   */
  def from(path: Path): Source[FastaEntry, Future[IOResult]] = {
    FileIO.fromPath(path)
      .via(FastaFlow())
      .named("FastaFlow")
  }

  def from(uri: URI): Source[FastaEntry, Future[IOResult]] = from(Paths.get(uri))

}
