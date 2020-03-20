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
import circeeg.util.AgeDemo
import circeeg.util.conf._

object Main extends App {
  val withNullPrinter = Printer.spaces2
  val withoutNullPrinter = Printer.spaces2.copy(dropNullValues = true)

  // Pretty-print in special format
  def pp[T](title: String, v: T) = {
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
  pp("encodedDwellTimeFilterWithNull", withNullPrinter.print(origDwellTimeFilter.asJson))
  pp("encodedDwellTimeFilterWithoutNull", withoutNullPrinter.print(origDwellTimeFilter.asJson))
  pp("decodedDwellTimeFilterWithNull", decode[Filter](withNullPrinter.print(origDwellTimeFilter.asJson)).right.get)
  pp("decodedDwellTimeFilterWithoutNull", decode[Filter](withoutNullPrinter.print(origDwellTimeFilter.asJson)).right.get)

  //
  // Demo
  //

  // AgeDemo

  val origAgeDemo1 = AgeDemo.withName("<20")
  pp("encodedAgeDemo1", origAgeDemo1.asJson.spaces2)
  pp("decodedAgeDemo1", decode[AgeDemo](origAgeDemo1.asJson.spaces2).right.get)

  val origAgeDemo2 = AgeDemo.withName("40-44")
  pp("encodedAgeDemo2", origAgeDemo2.asJson.spaces2)
  pp("decodedAgeDemo2", decode[AgeDemo](origAgeDemo2.asJson.spaces2).right.get)

  val origAgeDemo3 = AgeDemo.withName(">70")
  pp("encodedAgeDemo3", origAgeDemo3.asJson.spaces2)
  pp("decodedAgeDemo3", decode[AgeDemo](origAgeDemo3.asJson.spaces2).right.get)

  val origAgeDemo4 = AgeDemo.withName("unknown")
  pp("encodedAgeDemo4", origAgeDemo4.asJson.spaces2)
  pp("decodedAgeDemo4", decode[AgeDemo](origAgeDemo4.asJson.spaces2).right.get)

  //
  // Others
  //

  // FooVal

  // val origFoo: Other = FooVal(13, Some(14.0))

  // val encodedFoo = origFoo.asJson.spaces2
  // pp("encodedFoo", encodedFoo)

  // val decodedFoo = decode[Other](encodedFoo)
  // pp("decodedFoo", decodedFoo)

  // val decodedFoo2 = decode[Other]("""{"foo_val":{"val_int":13,"val_dbl":null}}""")
  // pp("decodedFoo2", decodedFoo2)

  // val decodedFoo3 = decode[Other]("""{"foo_val":{"val_int":13}}""")
  // pp("decodedFoo3", decodedFoo3)

  // BarVal

  // val origBar: Other = BarVal(Vector("123", "456"))

  // val encodedBar = origBar.asJson.spaces2
  // pp("encodedBar", encodedBar)

  // val decodedBar = decode[Other](encodedBar)
  // pp("decodedBar", decodedBar)

  // val decodedBar2 = decode[Other]("""{"bar_val":{"val_vec":[]}}""")
  // pp("decodedBar2", decodedBar2)
}
