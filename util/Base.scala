package circeeg.util

import cats.data.NonEmptyList
import cats.syntax.either._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps  // .asJson needs this

import circeeg.extras.{CirceEnumVariant, delegate}
import circeeg.util.Conf.custom

@ConfiguredJsonCodec
sealed trait Base extends Useless

object Base {
  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.
  // JsonKey does not work for ADT case class like this unfortunately
  // but at least we have a second-say on how the enum component here is named

  @CirceEnumVariant
  final case class X(@delegate v: circeeg.util.X) extends Base

  @CirceEnumVariant
  final case class Y(@delegate v: circeeg.util.Y) extends Base

  @CirceEnumVariant
  final case class Z(@delegate v: circeeg.util.Z) extends Base

  // A uses arity-3
  @CirceEnumVariant
  final case class A(@delegate v: circeeg.util.A) extends Base

  @CirceEnumVariant(case_class_fwd = false)
  final case class B(v: String) extends Base {
    def foo(): String = v
    def foo(x: Int): Int = id + 1
    def id: Int = 777
  }

  @CirceEnumVariant(case_class_fwd = false)
  final case class C(v: NonEmptyList[Int]) extends Base {
    def foo(): String = "NonEmptyList"
    def foo(x: Int): Int = id + 1
    def id: Int = 88
  }
}
