package at.bioinform.seq

import at.bioinform.core.alphabet.dna.DNA4._
import at.bioinform.generators.Generate
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class ChainBuilderTest extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  describe("ChainBuilder") {

    it("can add sequences.") {
      forAll ((Generate.dna4, "sequence")) { seq: String =>
        ChainBuilder(0) += seq
      }
    }
  }

}
