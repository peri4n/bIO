package at.bioinform.stream.util

/**
 * A Splitter splits strings into chunks of a specified size with a given overlap.
 *
 * Note, that for performance reasons we do not split strings but StringBuilders.
 *
 */
case class Splitter(maxSize: Int, overlap: Int) {

  require(maxSize > 0, "The maximal size of a splitter should be positive.")
  require(overlap > -1, "The overlap of a splitter should not be negative.")
  require(maxSize > overlap, "The maximal size of a splitter must be larger than it's overlap.")

  def split(chain: StringBuilder): (StringBuilder, String) = {
    if (chain.size >= maxSize - overlap) {
      (chain.drop(maxSize - overlap), chain.substring(0, maxSize))
    } else {
      (new StringBuilder(), chain.substring(0, chain.size))
    }
  }

}

