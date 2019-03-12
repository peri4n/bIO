package at.bioinform.core.alphabet.amino

import at.bioinform.core.alphabet.Alphabet

object Amino20 extends Alphabet {

  type elemType = AA20

  val elements = List(A, C, D, E, F, G, H, I, K, L, M, N, P, R, S, T, V, W, Y)

  override def size: Int = 20

  override def isCaseSensitive: Boolean = false
}
