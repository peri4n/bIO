package at.bioinform.stream.util

import org.scalatest.{FunSpec, Matchers}
import at.bioinform.lucene._

class SplitterTest extends FunSpec with Matchers {

  describe("A Splitter") {
    it("should not split te input is smaller than the max size.") {
      val (rest, split) = Splitter.withSize(10, 5).split(new StringBuilder("test"))
      rest should be(StringBuilder.newBuilder)
      split should be(Seq("test"))
    }

    it("should split a string larger than the max size.") {
      val (rest, split) = Splitter.withSize(5, 3).split(new StringBuilder("arnstient"))
      rest should be(new StringBuilder("nstient"))
      split should be(Seq("arnst"))
    }

    it("should split a string just smaller than the max size plus overlap.") {
      val (rest, split) = Splitter.withSize(7, 3).split(new StringBuilder("arnstient"))
      rest should be(new StringBuilder("tient"))
      split should be(Seq("arnstie"))
    }
  }
}
