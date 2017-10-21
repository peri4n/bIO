package at.bioinform.lucene

import org.apache.lucene.document.{Document, Field, TextField}

/**
  * Collection of built-in transformers.
  */
object Transformer {

  val default: Transformer[Segment] = (entry: Segment) => {
    val document = new Document()
    document.add(new Field("id", entry.header.id, TextField.TYPE_STORED))
    document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
    document
  }
}
