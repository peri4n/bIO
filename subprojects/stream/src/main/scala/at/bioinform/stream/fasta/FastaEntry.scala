package at.bioinform.stream.fasta

import at.bioinform.lucene._

case class FastaEntry(id: Id, desc: Option[Desc], sequence: Seq)

object FastaEntry {

  def apply(id: Id, sequence: Seq): FastaEntry = new FastaEntry(id, None, sequence)

  def apply(id: Id, description: Desc, sequence: Seq): FastaEntry = new FastaEntry(id, Some(description), sequence)

  def apply(id: Id, description: Option[Desc], sequence: Seq): FastaEntry = new FastaEntry(id, description, sequence)
}

