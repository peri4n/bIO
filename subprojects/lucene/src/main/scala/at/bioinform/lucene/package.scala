package at.bioinform

import org.apache.lucene.document.Document

package object lucene {

  /** A transformer takes a thing and returns a document ready for Lucene indexing. */
  type Transformer[A] = A => Document

  /** A position in a biological seqence. */
  case class Pos(position: Int) extends AnyVal {
    def += (pos: Pos) = Pos(position + pos.position)
  }

  /** An identifier of a biological seqence. */
  case class Id(string: String) extends AnyVal

  /** A description of a biological seqence. */
  case class Desc(string: String) extends AnyVal

  /** A biological seqence. */
  case class Seq(string: String) extends AnyVal {
    def length() = Pos(string.length)
  }
}
