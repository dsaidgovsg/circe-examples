package circeeg.util

import io.circe.generic.extras._

import circeeg.util.conf._

@ConfiguredJsonCodec
sealed trait Other

@ConfiguredJsonCodec
case class FooVal(valInt: Int, valDbl: Option[Double]) extends Other

@ConfiguredJsonCodec
case class BarVal(valVec: Vector[String]) extends Other
