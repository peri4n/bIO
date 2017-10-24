package at.bioinform

import org.apache.lucene.document.Document

package object lucene {

  /** A transformer takes a thing and returns a document ready for Lucene indexing. */
  type Transformer[A] = A => Document

  /** A position in a biological seqence. */
  case class Pos(value: Int) extends AnyVal {
    def +=(pos: Pos) = Pos(value + pos.value)
  }

  object Pos {
    implicit def pos2Int(pos: Pos): Int = pos.value
  }

  /** An identifier of a biological seqence. */
  case class Id(string: String) extends AnyVal

  object Id {
    implicit def id2String(id: Id): String = id.string
  }

  /** A description of a biological seqence. */
  case class Desc(value: String) extends AnyVal

  object Desc {
    implicit def desc2String(desc: Desc): String = desc.value
  }

  /** A biological seqence. */
  case class Seq(value: String) extends AnyVal {
    def length() = Pos(value.length)
  }

  object Seq {
    implicit def seq2String(seq: Seq): String = seq.value
  }
}
