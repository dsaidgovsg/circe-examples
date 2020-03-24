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
import circeeg.util.{AgeBand, Demo, Gender}
import circeeg.util.Conf.custom

object Main extends App {
  val np = Printer.spaces2
  val dnp = Printer.spaces2.copy(dropNullValues = true)

  // Pretty-print in special format
  def pp[T](title: String, v: T) = {
    val dashes = "-" * title.length
    println(s"\n$title\n$dashes\n$v\n")
  }

  //
  // Filter
  //

  // DwellTimeFilter

  val dwellTimeFilter: Filter = DwellTimeFilter(
    cells = Vector(123),
    startTime = ZonedDateTime.parse("2020-01-01T00:00:00+08:00"),
    endTime = ZonedDateTime.parse("2020-01-01T23:59:59+08:00"),
    minDwell = Duration.ofHours(1),
    maxDwell = None,
    recurrence = None
  )

  // dwellTimeFilter.asJson.spaces2 also works
  pp("encodedDwellTimeFilterWithNull", np.pretty(dwellTimeFilter.asJson))
  pp("encodedDwellTimeFilterWithoutNull", dnp.pretty(dwellTimeFilter.asJson))
  pp("decodedDwellTimeFilterWithNull", decode[Filter](np.pretty(dwellTimeFilter.asJson)).right.get)
  pp("decodedDwellTimeFilterWithoutNull", decode[Filter](dnp.pretty(dwellTimeFilter.asJson)).right.get)

  //
  // Demo
  //

  // AgeBand

  val ageBand1 = AgeBand.withName("<20")
  pp("encodedAgeBand1", ageBand1.asJson.spaces2)
  pp("decodedAgeBand1", decode[AgeBand](ageBand1.asJson.spaces2).right.get)

  val ageBand2 = AgeBand.withName("40-44")
  pp("encodedAgeBand2", ageBand2.asJson.spaces2)
  pp("decodedAgeBand2", decode[AgeBand](ageBand2.asJson.spaces2).right.get)

  val ageBand3 = AgeBand.withName(">70")
  pp("encodedAgeBand3", ageBand3.asJson.spaces2)
  pp("decodedAgeBand3", decode[AgeBand](ageBand3.asJson.spaces2).right.get)

  val ageBand4 = AgeBand.withName("unknown")
  pp("encodedAgeBand4", ageBand4.asJson.spaces2)
  pp("decodedAgeBand4", decode[AgeBand](ageBand4.asJson.spaces2).right.get)

  // Assert all cases
  AgeBand.values.foreach { ageBand =>
    assert(ageBand.asJson == Json.fromString(ageBand.entryName))
  }

  // Gender

  val gender1 = Gender.withName("male")
  pp("encodedGender1", gender1.asJson.spaces2)
  pp("decodedGender1", decode[Gender](gender1.asJson.spaces2).right.get)

  val gender2 = Gender.withName("female")
  pp("encodedGender2", gender2.asJson.spaces2)
  pp("decodedGender2", decode[Gender](gender2.asJson.spaces2).right.get)

  val gender3 = Gender.withName("unknown")
  pp("encodedGender3", gender3.asJson.spaces2)
  pp("decodedGender3", decode[Gender](gender3.asJson.spaces2).right.get)

  // Assert all cases
  Gender.values.foreach { gender =>
    assert(gender.asJson == Json.fromString(gender.entryName))
  }

  // Demo

  val demo1 = Demo(ages = None, genders = None)
  pp("encodedDemo1WithNull", np.pretty(demo1.asJson))
  pp("encodedDemo1WithoutNull", dnp.pretty(demo1.asJson))
  pp("decodedDemo1WithNull", decode[Demo](np.pretty(demo1.asJson)).right.get)
  pp("decodedDemo1WithoutNull", decode[Demo](dnp.pretty(demo1.asJson)).right.get)

  val demo2 = Demo(
    ages = Some(Set(AgeBand.withName("40-44"), AgeBand.withName("45-49"))),
    genders = Some(Set(Gender.withName("male"), Gender.withName("female")))
  )
  pp("encodedDemo2WithNull", np.pretty(demo2.asJson))
  pp("encodedDemo2WithoutNull", dnp.pretty(demo2.asJson))
  pp("decodedDemo2WithNull", decode[Demo](np.pretty(demo2.asJson)).right.get)
  pp("decodedDemo2WithoutNull", decode[Demo](dnp.pretty(demo2.asJson)).right.get)

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
