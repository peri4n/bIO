package at.bioinform.codec

case class FastaEntry(header: FastaHeader, sequence: String)

object FastaEntry {

  def apply(header: FastaHeader, sequence: String): FastaEntry = new FastaEntry(header, sequence)

  def apply(id: String, description: Option[String], sequence: String): FastaEntry = new FastaEntry(FastaHeader(id, description), sequence)

  def apply(id: String, sequence: String): FastaEntry = this(id, None, sequence)

}
