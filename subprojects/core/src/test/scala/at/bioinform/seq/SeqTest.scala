package at.bioinform.seq

import org.scalatest.{FunSpec, Matchers}
import at.bioinform.seq.Seq._

class SeqTest extends FunSpec with Matchers {

  describe("A Seq") {
    it("has the correct q grams.") {
      "ACGTC".qGrams(0) should contain theSameElementsAs List("").map(Seq.apply)
      "ACGTC".qGrams(1) should contain theSameElementsAs List("A", "C", "G", "T", "C").map(Seq.apply)
      "ACGTC".qGrams(2) should contain theSameElementsAs List("AC", "CG", "GT", "TC").map(Seq.apply)
      "ACGTC".qGrams(3) should contain theSameElementsAs List("ACG", "CGT", "GTC").map(Seq.apply)
    }

    it("has the correct q grams with step size 2.") {
      "ACGTC".qGrams(0, 2) should contain theSameElementsAs List("").map(Seq.apply)
      "ACGTC".qGrams(1, 2) should contain theSameElementsAs List("A", "G", "C").map(Seq.apply)
      "ACGTC".qGrams(2, 2) should contain theSameElementsAs List("AC", "GT").map(Seq.apply)
      "ACGTC".qGrams(3, 2) should contain theSameElementsAs List("ACG", "GTC").map(Seq.apply)
    }
  }

}
