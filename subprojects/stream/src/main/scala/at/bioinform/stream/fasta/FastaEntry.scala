package at.bioinform.stream.fasta

import at.bioinform.lucene.Segment

case class FastaEntry(header: FastaHeader, sequence: String) extends Segment {

  val id = header.id

}

object FastaEntry {

  def apply(header: FastaHeader, sequence: String): FastaEntry = new FastaEntry(header, sequence)

  def apply(id: String, description: Option[String], sequence: String): FastaEntry = new FastaEntry(FastaHeader(id, description), sequence)

  def apply(id: String, sequence: String): FastaEntry = this(id, None, sequence)

}
