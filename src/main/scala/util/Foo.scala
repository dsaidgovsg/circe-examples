package circeeg.util

import io.circe.generic.extras._

import circeeg.util.conf._

@ConfiguredJsonCodec
sealed trait Foo

@ConfiguredJsonCodec
case class BarVal(valInt: Int, valDbl: Option[Double]) extends Foo

@ConfiguredJsonCodec
case class BazVal(valVec: Vector[String]) extends Foo
