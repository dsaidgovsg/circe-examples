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

  // Arity-4
  
  implicit class Arity4[I1, I2, I3, I4, O1, O2](f: (I1, I2, I3, I4) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, O1, O2](f: (I1, I2, I3, I4) => O1)(g: O1 => O2): (I1, I2, I3, I4) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4) => g(f(i1, i2, i3, i4))

  // Arity-5
  
  implicit class Arity5[I1, I2, I3, I4, I5, O1, O2](f: (I1, I2, I3, I4, I5) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, I5, O1, O2](f: (I1, I2, I3, I4, I5) => O1)(g: O1 => O2): (I1, I2, I3, I4, I5) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4, i5: I5) => g(f(i1, i2, i3, i4, i5))

  // Arity-6
  
  implicit class Arity6[I1, I2, I3, I4, I5, I6, O1, O2](f: (I1, I2, I3, I4, I5, I6) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, I5, I6, O1, O2](f: (I1, I2, I3, I4, I5, I6) => O1)(g: O1 => O2): (I1, I2, I3, I4, I5, I6) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4, i5: I5, i6: I6) => g(f(i1, i2, i3, i4, i5, i6))

  // Arity-7
  
  implicit class Arity7[I1, I2, I3, I4, I5, I6, I7, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, I5, I6, I7, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7) => O1)(g: O1 => O2): (I1, I2, I3, I4, I5, I6, I7) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4, i5: I5, i6: I6, i7: I7) => g(f(i1, i2, i3, i4, i5, i6, i7))

  // Arity-8
  
  implicit class Arity8[I1, I2, I3, I4, I5, I6, I7, I8, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7, I8) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, I5, I6, I7, I8, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7, I8) => O1)(g: O1 => O2): (I1, I2, I3, I4, I5, I6, I7, I8) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4, i5: I5, i6: I6, i7: I7, i8: I8) => g(f(i1, i2, i3, i4, i5, i6, i7, i8))

  // Arity-9
  
  implicit class Arity9[I1, I2, I3, I4, I5, I6, I7, I8, I9, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7, I8, I9) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, I5, I6, I7, I8, I9, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7, I8, I9) => O1)(g: O1 => O2): (I1, I2, I3, I4, I5, I6, I7, I8, I9) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4, i5: I5, i6: I6, i7: I7, i8: I8, i9: I9) => g(f(i1, i2, i3, i4, i5, i6, i7, i8, i9))

  // Arity-10
  
  implicit class Arity10[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7, I8, I9, I10) => O1) {
    def mapResult(g: O1 => O2) = Func.mapResult(f)(g)
  }

  def mapResult[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O1, O2](f: (I1, I2, I3, I4, I5, I6, I7, I8, I9, I10) => O1)(g: O1 => O2): (I1, I2, I3, I4, I5, I6, I7, I8, I9, I10) => O2 =
    (i1: I1, i2: I2, i3: I3, i4: I4, i5: I5, i6: I6, i7: I7, i8: I8, i9: I9, i10: I10) => g(f(i1, i2, i3, i4, i5, i6, i7, i8, i9, i10))
}
