package circeeg.util

import cats.syntax.either._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax._

import circeeg.util.Conf._

@ConfiguredJsonCodec
sealed trait Base

object Base {
  // To get all the function result mapping with different arities
  import circeeg.util.Func._

  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.

  //
  // X section
  //

  final case class X(v: circeeg.util.X) extends Base
  object X {
    // circeeg.util.X.apply _ means it literally takes on the method circeeg.util.X.apply
    // and then we map over the result type O1 to O2
    // This replaces the need to know: def apply(v: Int) = new X(circeeg.util.X(v))
    def apply = (circeeg.util.X.apply _).mapResult(new X(_))
  }

  implicit val encodeX: Encoder[X] = new Encoder[X] {
    final def apply(v: X) = v.v.asJson
  }

  implicit val decodeX: Decoder[X] = new Decoder[X] {
    final def apply(c: HCursor) = for { v <- c.as[circeeg.util.X] } yield { new X(v) }
  }

  //
  // End of X section (the above is all you need to make a macro)
  //

  final case class Y(v: circeeg.util.Y) extends Base
  object Y {
    def apply = (circeeg.util.Y.apply _).mapResult(new Y(_))
  }

  implicit val encodeY: Encoder[Y] = new Encoder[Y] {
    final def apply(v: Y) = v.v.asJson
  }

  implicit val decodeY: Decoder[Y] = new Decoder[Y] {
    final def apply(c: HCursor) = for { v <- c.as[circeeg.util.Y] } yield { new Y(v) }
  }

  final case class Z(v: circeeg.util.Z) extends Base
  object Z {
    def apply = (circeeg.util.Z.apply _).mapResult(new Z(_))
  }

  implicit val encodeZ: Encoder[Z] = new Encoder[Z] {
    final def apply(v: Z) = v.v.asJson
  }

  implicit val decodeZ: Decoder[Z] = new Decoder[Z] {
    final def apply(c: HCursor) = for { v <- c.as[circeeg.util.Z] } yield { new Z(v) }
  }

  // A uses arity-3

  final case class A(v: circeeg.util.A) extends Base
  object A { def apply = (circeeg.util.A.apply _).mapResult(new A(_)) }

  implicit val encodeA: Encoder[A] = new Encoder[A] {
    final def apply(v: A) = v.v.asJson
  }

  implicit val decodeB: Decoder[A] = new Decoder[A] {
    final def apply(c: HCursor) = for { v <- c.as[circeeg.util.A] } yield { new A(v) }
  }
}
