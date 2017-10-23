package at.bioinform.lucene

import at.bioinform.lucene.segment.Segment
import org.apache.lucene.document.{Document, Field, TextField}

/**
 * Collection of built-in transformers.
 */
object Transformer {

  val default: Transformer[Segment] = (entry: Segment) => {
    val document = new Document()
    //    document.add(new Field("id", entry.id, TextField.TYPE_STORED))
    //    document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
    document
  }
}
