package at.bioinform.core.alphabet

package object dna {

  sealed trait Nuc4 extends Nuc5 {

    val complement: Nuc4

    def isPyrimidine: Boolean

  }

  sealed trait Nuc5 extends Nuc12 {

    val complement: Nuc5

    def isPyrimidine: Boolean

  }

  sealed trait Nuc12 extends Nuc16 {

    val complement: Nuc12

    def isPyrimidine: Boolean

  }

  /**
    * Set of all IUPAC DNA symbols
    */
  sealed trait Nuc16 extends Symbol {

    val complement: Nuc16

    def isPyrimidine: Boolean

    @inline def isPurine: Boolean = !isPyrimidine

  }

  /**
    * All nucleotides
    */

  /** Adenin */
  case object A extends Nuc4 {
    val char = 'A'
    val complement = T
    val isPyrimidine = false
  }

  /** Cytosine */
  case object C extends Nuc4 {
    val char = 'C'
    val complement = G
    val isPyrimidine = true
  }

  /** Guanine */
  case object G extends Nuc4 {
    val char = 'G'
    val complement = C
    val isPyrimidine = false
  }

  /** Thymine */
  case object T extends Nuc4 {
    val char = 'T'
    val complement = A
    val isPyrimidine = true
  }

  /** Unknown */
  case object N extends Nuc5 {
    val char = 'N'
    val complement = N
    val isPyrimidine = false
  }

  /** A or G */
  case object R extends Nuc12 {
    val char = 'R'
    val complement = Y
    val isPyrimidine = false // TODO
  }

  /** C or T */
  case object Y extends Nuc12 {
    val char = 'Y'
    val complement = R
    val isPyrimidine = false // TODO
  }

  /** G or C */
  case object S extends Nuc12 {
    val char = 'S'
    val complement = W
    val isPyrimidine = false // TODO
  }

  /** A or T */
  case object W extends Nuc12 {
    val char = 'W'
    val complement = S
    val isPyrimidine = false // TODO
  }

  /** G or T */
  case object K extends Nuc12 {
    val char = 'K'
    val complement = M
    val isPyrimidine = false // TODO
  }

  /** A or C */
  case object M extends Nuc12 {
    val char = 'M'
    val complement = K
    val isPyrimidine = false // TODO
  }

  /** C or G or T */
  case object B extends Nuc12 {
    val char = 'B'
    val complement = K
    val isPyrimidine = false // TODO
  }

  /** A or G or T */
  case object D extends Nuc12 {
    val char = 'D'
    val complement = K
    val isPyrimidine = false // TODO
  }

  /** A or C or T */
  case object H extends Nuc12 {
    val char = 'H'
    val complement = K
    val isPyrimidine = false // TODO
  }

  /** A or C or G */
  case object V extends Nuc12 {
    val char = 'V'
    val complement = K
    val isPyrimidine = false // TODO
  }

}
