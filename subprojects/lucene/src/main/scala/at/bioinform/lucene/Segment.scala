package at.bioinform.lucene

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
  */
trait Segment {

  /** Identifier of the seqment. */
  val id: String

  /** Containing sequence. */
  val sequence: String

  /** The offset in the embedding sequence. */
  val start: Int

  /** The end in the embedding sequence. */
  val end: Int

}
