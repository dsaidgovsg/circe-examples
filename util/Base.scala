package circeeg.util

import cats.data.NonEmptyList
import cats.syntax.either._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps  // .asJson needs this

import circeeg.extras.{CirceEnumVariant, delegate}
import circeeg.util.Conf.custom

// Scala has a fairly cheat way to "union" the ADT variants if the variants
// happen to have additional tiering.
// Just use additional sealed traits inheriting base sealed traits for this

@ConfiguredJsonCodec
sealed trait Base extends Useless

@ConfiguredJsonCodec
sealed trait B1 extends Base

@ConfiguredJsonCodec
sealed trait B2 extends Base


object B1 {
  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.
  // JsonKey does not work for ADT case class like this unfortunately
  // but at least we have a second-say on how the enum component here is named

  @CirceEnumVariant
  final case class X(@delegate v: circeeg.util.X) extends B1

  @CirceEnumVariant
  final case class Y(@delegate v: circeeg.util.Y) extends B1
}

object B2 {
  @CirceEnumVariant
  final case class Z(@delegate v: circeeg.util.Z) extends B2

  // A uses arity-3
  @CirceEnumVariant
  final case class A(@delegate v: circeeg.util.A) extends B2

  @CirceEnumVariant(case_class_fwd = false)
  final case class B(v: String) extends B2 {
    def foo(): String = "abc"
    def foo(x: Int): Int = id + 1
    def id: Int = 777
  }

  @CirceEnumVariant(case_class_fwd = false)
  final case class C(v: NonEmptyList[Int]) extends B2 {
    def foo(): String = "NonEmptyList"
    def foo(x: Int): Int = id + 1
    def id: Int = 88
  }
}

object Base
