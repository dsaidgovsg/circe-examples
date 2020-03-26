package circeeg.util

import cats.syntax.either._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps  // .asJson needs this

import circeeg.extras.{CirceEnumDerive, CirceEnumVariant}
import circeeg.util.Conf.custom

@ConfiguredJsonCodec
@CirceEnumDerive[circeeg.util.Useless]
sealed trait Base

object Base {
  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.
  // JsonKey does not work for ADT case class like this unfortunately
  // but at least we have a second-say on how the enum component here is named

  @CirceEnumVariant
  final case class X(v: circeeg.util.X) extends Base

  @CirceEnumVariant
  final case class Y(v: circeeg.util.Y) extends Base

  @CirceEnumVariant
  final case class Z(v: circeeg.util.Z) extends Base

  // A uses arity-3
  @CirceEnumVariant
  final case class A(v: circeeg.util.A) extends Base
}
