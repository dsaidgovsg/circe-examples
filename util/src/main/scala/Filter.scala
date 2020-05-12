package circeeg.util

import java.time.{Duration, ZonedDateTime}

import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.util.Conf.custom

@ConfiguredJsonCodec
sealed trait Filter

@ConfiguredJsonCodec
case class DwellTimeFilter(
  cells: Vector[Int],
  startTime: ZonedDateTime,
  endTime: ZonedDateTime,
  minDwell: Duration,
  maxDwell: Option[Duration],
  recurrence: Option[Int]
) extends Filter

// Only for Scala 2.11 + circe 0.12.0-M3
// https://github.com/circe/circe/issues/251
object Filter
