package circeeg.extras

object Func {
  // Arity-0

  implicit class Arity0[I1, O1, O2](f: I1 => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[O1, O2](f: () => O1)(g: O1 => O2): () => O2 =
    () => g(f())

  // Arity-1

  implicit class Arity1[I1, O1, O2](f: I1 => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, O1, O2](f: I1 => O1)(g: O1 => O2): I1 => O2 =
    (i1: I1) => g(f(i1))

  // Arity-2

  implicit class Arity2[I1, I2, O1, O2](f: (I1, I2) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, O1, O2](f: (I1, I2) => O1)(g: O1 => O2): (I1, I2) => O2 =
    (i1: I1, i2: I2) => g(f(i1, i2))

  // Arity-3
  
  implicit class Arity3[I1, I2, I3, O1, O2](f: (I1, I2, I3) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, O1, O2](f: (I1, I2, I3) => O1)(g: O1 => O2): (I1, I2, I3) => O2 =
    (i1: I1, i2: I2, i3: I3) => g(f(i1, i2, i3))
}
