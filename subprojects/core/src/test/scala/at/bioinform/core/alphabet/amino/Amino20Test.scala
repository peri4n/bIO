package at.bioinform.core.alphabet.amino

import at.bioinform.generators.Generate
import org.scalacheck.Gen
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class Amino20Test extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  describe("Amino20") {

    it("have the correct elements") {
      Amino20.elements shouldBe List(A, C, D, E, F, G, H, I, K, L, M, N, P, R, S, T, V, W, Y)
      Amino20.size shouldBe 20
    }

    it("can convert chars to integer") {
      Amino20.toInt('A') shouldBe 0
      Amino20.toInt('a') shouldBe 0
      Amino20.toInt('C') shouldBe 1
      Amino20.toInt('c') shouldBe 1
      Amino20.toInt('D') shouldBe 2
      Amino20.toInt('d') shouldBe 2
      Amino20.toInt('E') shouldBe 3
      Amino20.toInt('e') shouldBe 3

      val symbolChars = Amino20.elements.map(_.char)
      forAll { aa: Char =>
        whenever(!symbolChars.contains(aa)) {
          Amino20.toInt(aa) shouldBe 0
        }
      }
    }

    it("can convert symbols to integer") {
      Amino20.toInt(A) shouldBe 0
      Amino20.toInt(C) shouldBe 1
      Amino20.toInt(D) shouldBe 2
      Amino20.toInt(E) shouldBe 3
    }

    it("can convert from integers to symbols") {
      Amino20.fromInt(0) shouldBe A
      Amino20.fromInt(1) shouldBe C
      Amino20.fromInt(2) shouldBe D
      Amino20.fromInt(3) shouldBe E
      Amino20.fromInt(4) shouldBe F
    }

    it("toChar and fromChar should lead to identity (upper cased)") {
      forAll(Generate.amino20Char) { char: Char =>
        Amino20.fromChar(char).char shouldBe char.toUpper
      }
    }

    it("toInt and fromInt should lead to identity") {
      forAll(Gen.oneOf(0, 1, 2, 3)) { number: Int =>
        Amino20.toInt(Amino20.fromInt(number)) shouldBe number
      }
    }
  }
}
