package at.bioinform

import org.apache.lucene.document.Document

package object lucene {

  /** A transformer takes a thing and returns a document ready for Lucene indexing. */
  type Transformer[A] = A => Document

}
