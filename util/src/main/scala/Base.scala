package circeeg.util

import cats.data.NonEmptyList
import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.extras.CirceEnumVariant
import circeeg.util.Conf.custom

// Scala has a fairly cheat way to "union" the ADT variants if the variants
// happen to have additional tiering.
// Just use additional sealed traits inheriting base sealed traits for this

@ConfiguredJsonCodec
sealed trait Base extends Useless

object Base {
  @CirceEnumVariant
  final case class B(v: String) extends Base {
    def foo(): String = "abc"
    def foo(x: Int): Int = id + 1
    def id: Int = 777
  }

  @CirceEnumVariant
  final case class C(v: NonEmptyList[Int]) extends Base {
    def foo(): String = "NonEmptyList"
    def foo(x: Int): Int = id + 1
    def id: Int = 88
  }

  final case class D() extends Base {
    def foo(): String = "Empty variant"
    def foo(x: Int): Int = id + 1
    def id: Int = 99
  }
}

@ConfiguredJsonCodec
sealed trait B0

object B0 {
  @CirceEnumVariant
  final case class Random(v: circeeg.util.AnyNameIsFine) extends B0
}

@ConfiguredJsonCodec
sealed trait B1 extends Base {
  val v: Useless
  def foo(): String = v.foo()
  def foo(x: Int): Int = v.foo(x)
  def id: Int = v.id
}

object B1 {
  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.
  // JsonKey does not work for ADT case class like this unfortunately
  // but at least we have a second-say on how the enum component here is named

  @CirceEnumVariant
  final case class X(v: circeeg.util.X) extends B1

  @CirceEnumVariant
  final case class Y(v: circeeg.util.Y) extends B1
}

@ConfiguredJsonCodec
sealed trait B2 extends Base {
  val v: Useless
  def foo(): String = v.foo()
  def foo(x: Int): Int = v.foo(x)
  def id: Int = v.id
}

object B2 {
  @CirceEnumVariant
  final case class Z(v: circeeg.util.Z) extends B2

  // A uses arity-3
  @CirceEnumVariant
  final case class A(v: circeeg.util.A) extends B2

  // Empty has no param in ctor
  @CirceEnumVariant
  final case class E(v: circeeg.util.Empty) extends B2
}
