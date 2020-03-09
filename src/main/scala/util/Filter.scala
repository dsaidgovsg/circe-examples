package circeeg.util

import io.circe.generic.extras._
import java.time.{Duration, ZonedDateTime}

import circeeg.util.conf._

@ConfiguredJsonCodec
sealed trait Filter

@ConfiguredJsonCodec
case class DwellTimeInWindow(
  cells: Vector[Int],
  startTime: ZonedDateTime,
  endTime: ZonedDateTime,
  minDwell: Duration,
  maxDwell: Option[Duration],
  recurrence: Option[Int],
) extends Filter
