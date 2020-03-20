package circeeg.main

import io.circe._
import io.circe.generic.auto._
import io.circe.generic.extras._
import io.circe.parser._
import io.circe.syntax._
import io.circe.Printer
import java.time.{Duration, ZonedDateTime}

import circeeg.util.{FooVal, BarVal, Other}
import circeeg.util.{Filter, DwellTimeFilter}
import circeeg.util.conf._

object Main extends App {
  val withNullPrinter = Printer.spaces2
  val withoutNullPrinter = Printer.spaces2.copy(dropNullValues = true)

  def specialPrint[T](title: String, v: T) = {
    val dashes = "-" * title.length
    println(s"\n$title\n$dashes\n$v\n")
  }

  //
  // Filter
  //

  // DwellTimeFilter

  val origDwellTimeFilter: Filter = DwellTimeFilter(
    cells = Vector(123),
    startTime = ZonedDateTime.parse("2020-01-01T00:00:00+08:00"),
    endTime = ZonedDateTime.parse("2020-01-01T23:59:59+08:00"),
    minDwell = Duration.ofHours(1),
    maxDwell = None,
    recurrence = None,
  )

  // origDwellTimeFilter.asJson.spaces2 also works
  val encodedDwellTimeFilterWithNull = withNullPrinter.print(origDwellTimeFilter.asJson)
  specialPrint("encodedDwellTimeFilterWithNull", encodedDwellTimeFilterWithNull)

  val encodedDwellTimeFilterWithoutNull = withoutNullPrinter.print(origDwellTimeFilter.asJson)
  specialPrint("encodedDwellTimeFilterWithoutNull", encodedDwellTimeFilterWithoutNull)

  val decodedDwellTimeFilterWithNull = decode[Filter](encodedDwellTimeFilterWithNull)
  specialPrint("decodedDwellTimeFilterWithNull", decodedDwellTimeFilterWithNull)

  val decodedDwellTimeFilterWithoutNull = decode[Filter](encodedDwellTimeFilterWithoutNull)
  specialPrint("decodedDwellTimeFilterWithoutNull", decodedDwellTimeFilterWithoutNull)

  //
  // Others
  //

  // FooVal

  // val origFoo: Other = FooVal(13, Some(14.0))

  // val encodedFoo = origFoo.asJson.spaces2
  // specialPrint("encodedFoo", encodedFoo)

  // val decodedFoo = decode[Other](encodedFoo)
  // specialPrint("decodedFoo", decodedFoo)

  // val decodedFoo2 = decode[Other]("""{"foo_val":{"val_int":13,"val_dbl":null}}""")
  // specialPrint("decodedFoo2", decodedFoo2)

  // val decodedFoo3 = decode[Other]("""{"foo_val":{"val_int":13}}""")
  // specialPrint("decodedFoo3", decodedFoo3)

  // BarVal

  // val origBar: Other = BarVal(Vector("123", "456"))

  // val encodedBar = origBar.asJson.spaces2
  // specialPrint("encodedBar", encodedBar)

  // val decodedBar = decode[Other](encodedBar)
  // specialPrint("decodedBar", decodedBar)

  // val decodedBar2 = decode[Other]("""{"bar_val":{"val_vec":[]}}""")
  // specialPrint("decodedBar2", decodedBar2)
}
