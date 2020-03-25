package circeeg.util

import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.util.Conf.custom

@ConfiguredJsonCodec
sealed trait Other

@ConfiguredJsonCodec
case class FooVal(valInt: Int, valDbl: Option[Double]) extends Other

@ConfiguredJsonCodec
case class BarVal(valVec: Vector[String]) extends Other

// Only for Scala 2.11 + circe 0.12.0-M3
// https://github.com/circe/circe/issues/251
object Other
