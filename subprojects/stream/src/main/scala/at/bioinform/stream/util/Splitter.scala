package at.bioinform.stream.util

import at.bioinform.lucene._

/**
  * A Splitter splits strings into chunks of a specified size with a given overlap.
  *
  * Note, that for performance reasons we do not split strings but StringBuilders.
  */
trait Splitter extends (StringBuilder => (StringBuilder, Seq)) {

  /** Maximal size of a stringbuilder before he gets split. */
  def maxSize: Option[Int]

  /**
    * Predicate if a splitter will split the string when applied to the split function.
    *
    * @param stringBuilder builder to test for splitting
    * @return if true `split` will actually do something
    */
  def willSplit(stringBuilder: StringBuilder): Boolean = maxSize.exists(_ < stringBuilder.size)

  /** Convenience alias */
  def split = apply _
}

object Splitter {

  /** Applies no splitting at all, the entire builder result is returned. */
  val noop = new Splitter {
    val maxSize = None

    override def apply(chain: StringBuilder) = (new StringBuilder(), Seq(chain.result()))
  }

  /** Splits a string into chunks of given size. */
  def withSize(size: Int, overlap: Int = 0) = new Splitter {
    require(size > 0, "The size of a splitter should be positive.")
    require(overlap > -1, "The overlap of a splitter should not be negative.")
    require(size > overlap, "The size of a splitter must be larger than it's overlap.")

    val maxSize = Some(size - overlap)

    override def apply(chain: StringBuilder) = {
      if (willSplit(chain)) {
        (chain.drop(size - overlap), Seq(chain.substring(0, size)))
      } else {
        (new StringBuilder(), Seq(chain.substring(0, chain.size)))
      }
    }
  }
}
