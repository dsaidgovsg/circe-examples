package circeeg.extras

object Func {
  implicit class Func1Extra[I1, O1, O2](f: I1 => O1) {
    def mapResult(g: O1 => O2): I1 => O2 = (i1: I1) => g(f(i1))
  }

  implicit class Func2Extra[I1, I2, O1, O2](f: (I1, I2) => O1) {
    def mapResult(g: O1 => O2): (I1, I2) => O2 = (i1: I1, i2: I2) => g(f(i1, i2))
  }

  implicit class Func3Extra[I1, I2, I3, O1, O2](f: (I1, I2, I3) => O1) {
    def mapResult(g: O1 => O2): (I1, I2, I3) => O2 = (i1: I1, i2: I2, i3: I3) => g(f(i1, i2, i3))
  }
}
