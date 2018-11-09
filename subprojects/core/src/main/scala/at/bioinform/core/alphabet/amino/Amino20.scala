package at.bioinform.core.alphabet.amino

import at.bioinform.core.alphabet.Alphabet

class Amino20 extends Alphabet {

  override type elemType = AA20

  /** Elements of the alphabet */
  val elements  = List(A, C, D, E, F, G, H, I, K, L, M, N, P, R, S, T, V, W, Y)

}
