package at.bioinform.core

trait Symbol {

  val char: Char

}

sealed trait Nuc extends Symbol {

  val complement: Nuc

  def isPyrimidine: Boolean

  @inline def isPurine: Boolean = !isPyrimidine

}

/** Adenin */
case object A extends Nuc {
  val char = 'A'
  val complement = T
  val isPyrimidine = false
}

/** Cytosine */
case object C extends Nuc {
  val char = 'C'
  val complement = G
  val isPyrimidine = true
}

/** Guanine */
case object G extends Nuc {
  val char = 'G'
  val complement = C
  val isPyrimidine = false
}

/** Thymine */
case object T extends Nuc {
  val char = 'T'
  val complement = A
  val isPyrimidine = true
}
