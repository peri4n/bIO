package at.bioinform.stream.util

import at.bioinform.lucene._
import org.scalatest.{FunSpec, Matchers}

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

    it("should not split if it is the noop splitter.") {
      val (rest, split) = Splitter.noop.split(new StringBuilder("ariesntiaersnt"))
      rest should be(new StringBuilder())
      split should be(Seq("ariesntiaersnt"))
    }
  }
}
