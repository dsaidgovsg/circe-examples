package circeeg.util

import cats.syntax.either._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax._

import circeeg.util.Conf._

import circeeg.extras.CirceEnumComponent

@ConfiguredJsonCodec
sealed trait Base

object Base {
  // To get all the function result mapping with different arities
  import circeeg.extras.Func._

  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.

  @CirceEnumComponent
  final case class X(v: circeeg.util.X) extends Base

  @CirceEnumComponent
  final case class Y(v: circeeg.util.Y) extends Base

  @CirceEnumComponent
  final case class Z(v: circeeg.util.Z) extends Base

  // A uses arity-3
  @CirceEnumComponent
  final case class A(v: circeeg.util.A) extends Base
}