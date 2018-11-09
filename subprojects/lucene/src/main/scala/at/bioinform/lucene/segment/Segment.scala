package at.bioinform.lucene.segment

import at.bioinform.io._

/**
  * A segment represents a part of a biological sequence.
  *
  * It is the result of the splitting of larger sequences into smaller chunks.
  * This is necessary because a matching document in a Lucene index is only
  * meaningful if the document is relatively small. It is what we ultimately store in the lucene index.
  *
  * Note that it cal also represent an entire sequence but this should only occur
  * if the sequence is small enough.
  *
  * @param id          Identifier of the segment
  * @param sequence    Contained sequence
  * @param description optional description
  * @param start       offset into the embedded sequence
  *
  */
case class Segment(id: Id, sequence: Seq, description: Option[Desc] = None, start: Option[Pos] = None)
