package at.bioinform.stream.fasta

/**
  * An entry stored in a [[https://en.wikipedia.org/wiki/FASTA_format FASTA]] formatted file.
  *
  * @param id An identifier
  * @param description A description
  * @param sequence A biological sequnece (e.g. DNA, RNA, ...)
  */
case class FastaEntry(id: String, description: Option[String], sequence: String) {

  def this(id: String, sequence: String) = this(id, None, sequence)

}

