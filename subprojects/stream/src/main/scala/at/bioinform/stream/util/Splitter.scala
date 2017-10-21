package at.bioinform.stream.util

/**
 * A Splitter splits strings into chunks of a specified size with a given overlap.
 *
 * Note, that for performance reasons we do not split strings but StringBuilders.
 *
 */
trait Splitter extends (StringBuilder => (StringBuilder, String)) {

  def split = apply _
}

object Splitter {

  /** Applies no splitting at all, the entire builder result is returned. */
  val noop = new Splitter {
    override def apply(chain: StringBuilder) = (new StringBuilder(), chain.result())
  }

  /** Splits a string into chunks of given size. */
  def withSize(size: Int, overlap: Int = 0) = new Splitter {
    require(size > 0, "The size of a splitter should be positive.")
    require(overlap > -1, "The overlap of a splitter should not be negative.")
    require(size > overlap, "The size of a splitter must be larger than it's overlap.")

    override def apply(chain: StringBuilder) = {
      if (chain.size >= size - overlap) {
        (chain.drop(size - overlap), chain.substring(0, size))
      } else {
        (new StringBuilder(), chain.substring(0, chain.size))
      }
    }
  }
}
