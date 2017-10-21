package at.bioinform.stream.fasta

import org.apache.lucene.document.{Document, Field, TextField}

/**
 * A transformer takes a thing and returns a document ready for Lucene indexing.
 */
object Transformer {

  type Transformer[A] = (A, Document)

  val default: Transformer[FastaEntry] = (entry: FastaEntry) => {
    val document = new Document()
    document.add(new Field("id", entry.header.id, TextField.TYPE_STORED))
    document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
    document
  }
}
