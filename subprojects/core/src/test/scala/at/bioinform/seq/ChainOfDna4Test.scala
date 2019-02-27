package at.bioinform.seq

import at.bioinform.core.alphabet.dna._
import at.bioinform.generators.Generate
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class ChainOfDna4Test extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  import at.bioinform.core.alphabet.dna.DNA4._

  describe("A chain") {

    it("can be constructed with an implicit alphabet (e.g DNA4).") {
      val value = Chain("ACGT")
      value.alphabet shouldBe DNA4
    }

    it("should have the correct bit pattern.") {
      Chain.fromString("A").bitPattern shouldBe "00"
      Chain.fromString("C").bitPattern shouldBe "01"
      Chain.fromString("G").bitPattern shouldBe "10"
      Chain.fromString("T").bitPattern shouldBe "11"
      Chain.fromString("TA").bitPattern shouldBe "0011"
      Chain.fromString("TC").bitPattern shouldBe "0111"
      Chain.fromString("TG").bitPattern shouldBe "1011"
      Chain.fromString("TT").bitPattern shouldBe "1111"
      Chain.fromString("CTA").bitPattern shouldBe "001101"
      Chain.fromString("CTC").bitPattern shouldBe "011101"
      Chain.fromString("CTG").bitPattern shouldBe "101101"
      Chain.fromString("CTT").bitPattern shouldBe "111101"
    }

    it("can be randomly accessed.") {
      val value = Chain("ACGT")
      value(0) shouldBe A
      value(1) shouldBe C
      value(2) shouldBe G
      value(3) shouldBe T
    }

    it("can compute substrings.") {
      forAll ((Generate.dna4WithLength, "sequenceWithPosition")) { case (seq: String, pos: Int) =>
        Chain(seq).substring(pos) shouldBe Chain(seq.substring(pos))
      }
    }

    it("can compare to itself.") {
      forAll ((Generate.dna4, "sequence1"), (Generate.dna4, "sequence2")) { (seq1: String, seq2: String) =>
        whenever(seq1 != seq2) {
          Chain.fromDna4(seq1) shouldNot be(Chain.fromDna4(seq2))
        }
      }
    }

    it("has the correct q grams.") {
      dna4"ACGTC".qGrams(0) should contain theSameElementsAs List("").map(Chain.fromDna4)
      dna4"ACGTC".qGrams(1) should contain theSameElementsAs List("A", "C", "G", "T", "C").map(Chain.fromDna4)
      dna4"ACGTC".qGrams(2) should contain theSameElementsAs List("AC", "CG", "GT", "TC").map(Chain.fromDna4)
      dna4"ACGTC".qGrams(3) should contain theSameElementsAs List("ACG", "CGT", "GTC").map(Chain.fromDna4)
    }

    it("has the correct q grams with step size 2.") {
      dna4"ACGTC".qGrams(0, 2) should contain theSameElementsAs List("").map(Chain.fromDna4)
      dna4"ACGTC".qGrams(1, 2) should contain theSameElementsAs List("A", "G", "C").map(Chain.fromDna4)
      dna4"ACGTC".qGrams(2, 2) should contain theSameElementsAs List("AC", "GT").map(Chain.fromDna4)
      dna4"ACGTC".qGrams(3, 2) should contain theSameElementsAs List("ACG", "GTC").map(Chain.fromDna4)
    }

    it("can be converted to string.") {
      forAll ((Generate.dna4, "sequence")) { seq: String =>
        Chain.fromString(seq).toString shouldBe seq
      }
    }

    it("can be converted to string with unknown characters converted to A") {
      dna4"ACxTC".toString shouldBe "ACATC"
    }
  }

}
