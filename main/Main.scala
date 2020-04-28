package circeeg.main

import cats.data.NonEmptyList
import io.circe.Json
import io.circe.parser.decode
// This is for .asJson, usually imported as io.circe.syntax._
import io.circe.syntax.EncoderOps
import io.circe.Printer
import java.time.{Duration, ZonedDateTime}

import circeeg.util.{Base, B0, B1, B2}
import circeeg.util.{FooVal, BarVal, Other}
import circeeg.util.{Filter, DwellTimeFilter}
import circeeg.util.{AgeBand, Demo, Gender}
import circeeg.util.Conf.custom
import circeeg.util.EmptyMapAsNone
import circeeg.util.Expr2
import circeeg.util.ExprEnum
import circeeg.util.NoneDefault._
import circeeg.util.Sorl

object Main extends App {
  val np = Printer.spaces2
  val dnp = Printer.spaces2.copy(dropNullValues = true)

  // Pretty-print in special format
  def pp[T](title: String, v: T) = {
    val dashes = "-" * title.length
    println(s"\n$title\n$dashes\n$v\n")
  }

  //
  // EmptyDictAsNone
  //

  {
    val eman1: EmptyMapAsNone[String] = EmptyMapAsNone.empty
    val emanEncoded1 = eman1.asJson.noSpaces
    pp("Eman encoded 1", emanEncoded1)
    val emanDecoded1 = decode[EmptyMapAsNone[String]](emanEncoded1).right.get
    pp("Eman decoded 1", emanDecoded1)
  }

  {
    val emanEncoded2 = "null"
    pp("Eman encoded 2", emanEncoded2)
    val emanDecoded2 = decode[EmptyMapAsNone[String]](emanEncoded2).right.get
    pp("Eman decoded 2", emanDecoded2)
  }

  {
    val eman3 = EmptyMapAsNone("hello")
    val emanEncoded3 = eman3.asJson.noSpaces
    pp("Eman encoded 3", emanEncoded3)
    val emanDecoded3 = decode[EmptyMapAsNone[String]](emanEncoded3).right.get
    pp("Eman decoded 3", emanDecoded3)
  }

  {
    val eman4 = EmptyMapAsNone(Map("a" -> 1, "b" -> 2))
    val emanEncoded4 = eman4.asJson.noSpaces
    pp("Eman encoded 4", emanEncoded4)
    val emanDecoded4 = decode[EmptyMapAsNone[Map[String, Int]]](emanEncoded4).right.get
    pp("Eman decoded 4", emanDecoded4)
  }

  //
  // Expr2
  //

  {
    import Expr2._

    val expr1: Expr2 = Lit(111)
    val exprEncoded1 = expr1.asJson.noSpaces
    pp("Expr encoded 1", exprEncoded1)
    val exprDecoded1 = decode[Expr2](exprEncoded1).right.get
    pp("Expr decoded 1", exprDecoded1)
    pp("Expr eval 1", exprDecoded1.eval)

    val expr2: Expr2 = Add(Seq(Lit(111), Add(Seq(Lit(111), Lit(222)))))
    val exprEncoded2 = expr2.asJson.noSpaces
    pp("Expr encoded 2", exprEncoded2)
    val exprDecoded2 = decode[Expr2](exprEncoded2).right.get
    pp("Expr decoded 2", exprDecoded2)
    pp("Expr eval 2", exprDecoded2.eval)

    val expr3: Expr2 = Add2(Lit(123), Lit(456))
    val exprEncoded3 = expr3.asJson.noSpaces
    pp("Expr encoded 3", exprEncoded3)
    val exprDecoded3 = decode[Expr2](exprEncoded3).right.get
    pp("Expr decoded 3", exprDecoded3)
    pp("Expr eval 3", exprDecoded3.eval)
  }

  //
  // Expr
  //

  {
    import ExprEnum._

    val expr1: ExprEnum = Lit(111)
    val exprEncoded1 = expr1.asJson.noSpaces
    pp("Expr encoded 1", exprEncoded1)
    val exprDecoded1 = decode[ExprEnum](exprEncoded1).right.get
    pp("Expr decoded 1", exprDecoded1)
    pp("Expr eval 1", exprDecoded1.eval)

    val expr2: ExprEnum = Add(Seq(Lit(111), Add(Seq(Lit(111), Lit(222)))))
    val exprEncoded2 = expr2.asJson.noSpaces
    pp("Expr encoded 2", exprEncoded2)
    val exprDecoded2 = decode[ExprEnum](exprEncoded2).right.get
    pp("Expr decoded 2", exprDecoded2)
    pp("Expr eval 2", exprDecoded2.eval)
  }

  //
  // Custom single element or list of element
  //

  val raw1 = "\"Hello\""
  val sorl1 = decode[Sorl[String]](raw1).right.get
  pp("Single string", s"${raw1} => ${sorl1} => ${sorl1.asJson.noSpaces}")

  val raw2 = "[123]"
  val sorl2 = decode[Sorl[Int]](raw2).right.get
  pp("Array of single Int", s"${raw2} => ${sorl2} => ${sorl2.asJson.noSpaces}")

  val raw3 = "{\"abc\": 1, \"def\": 2}"
  val sorl3 = decode[Sorl[Map[String, Int]]](raw3).right.get
  pp("Single Map[String, Int]", s"${raw3} => ${sorl3} => ${sorl3.asJson.noSpaces}")

  val raw4 = "[123, 456]"
  val sorl4 = decode[Sorl[List[Int]]](raw4).right.get
  pp("Single List[Int]", s"${raw4} => ${sorl4} => ${sorl4.asJson.noSpaces}")

  val raw5 = "[[\"Hello\", \"How are you\"], [], [\"World\"]]"
  val sorl5 = decode[Sorl[List[String]]](raw5).right.get
  pp("Array of multiple Strings", s"${raw5} => ${sorl5} => ${sorl5.asJson.noSpaces}")

  //
  // Sorl with Option interaction
  //

  val oraw1 = "null"
  val osorl1 = decode[Option[Sorl[String]]](oraw1).right.get.noneToDefault
  pp("Null none-to-default", s"${oraw1} => ${osorl1} => ${osorl1.asJson.noSpaces}")

  val oraw2 = "\"hello\""
  val osorl2 = decode[Option[Sorl[String]]](oraw2).right.get.noneToDefault
  pp("Single value none-to-default", s"${oraw2} => ${osorl2} => ${osorl2.asJson.noSpaces}")

  val oraw3 = "[\"hello\", \"world\"]"
  val osorl3 = decode[Option[Sorl[String]]](oraw3).right.get.noneToDefault
  pp("Multiple values none-to-default", s"${oraw3} => ${osorl3} => ${osorl3.asJson.noSpaces}")

  //
  // Base + Delegate
  //

  // You could go with either Base, B1, B2

  val random: B0 = B0.Random("Impl case class can be any name", 777)
  val x: Base = B1.X(1)
  val y: B1 = B1.Y(1)
  val z: B2 = B2.Z("abc")
  val a: Base = B2.A()  // Default values work
  val e: B2 = B2.E()  // Variant with empty ctor works too
  val b: Base = Base.B("Hello!")
  val c: Base = Base.C(NonEmptyList.one(123))
  val d: Base = Base.D()  // Variant with no fields works too

  val encodedRandom = np.pretty(random.asJson)
  val decodedRandom = decode[B0](encodedRandom).right.get
  pp("random-encode", encodedRandom)
  pp("random-decode", decodedRandom)

  val encodedX = np.pretty(x.asJson)
  val decodedX = decode[Base](encodedX).right.get
  pp("x-encode", encodedX)
  pp("x-decode", decodedX)
  pp("x-foo", decodedX.foo)
  pp("x-foo(0)", decodedX.foo(0))
  pp("x-id", decodedX.id)

  val encodedY = np.pretty(y.asJson)
  val decodedY = decode[Base](encodedY).right.get
  pp("y-encode", encodedY)
  pp("y-decode", decodedY)
  pp("y-foo", decodedY.foo)
  pp("y-foo(0)", decodedY.foo(0))
  pp("y-id", decodedY.id)

  val encodedZ = np.pretty(z.asJson)
  val decodedZ = decode[Base](encodedZ).right.get
  pp("z-encode", encodedZ)
  pp("z-decode", decodedZ)
  pp("z-foo", decodedZ.foo)
  pp("z-foo(0)", decodedZ.foo(0))
  pp("z-id", decodedZ.id)

  val encodedA = np.pretty(a.asJson)
  val decodedA = decode[Base](encodedA).right.get
  pp("a-encode", encodedA)
  pp("a-decode", decodedA)
  pp("a-foo", decodedA.foo)
  pp("a-foo(0)", decodedA.foo(0))
  pp("a-id", decodedA.id)

  val encodedE = np.pretty(e.asJson)
  val decodedE = decode[Base](encodedE).right.get
  pp("empty-encode", encodedE)
  pp("empty-decode", decodedE)
  pp("empty-foo", decodedE.foo)
  pp("empty-foo(0)", decodedE.foo(0))
  pp("empty-id", decodedE.id)

  val encodedB = np.pretty(b.asJson)
  val decodedB = decode[Base](encodedB).right.get
  pp("b-encode", encodedB)
  pp("b-decode", decodedB)
  pp("b-foo", decodedB.foo)
  pp("b-foo(0)", decodedB.foo(0))
  pp("b-id", decodedB.id)

  val encodedC = np.pretty(c.asJson)
  val decodedC = decode[Base](encodedC).right.get
  pp("c-encode", encodedC)
  pp("c-decode", decodedC)
  pp("c-foo", decodedC.foo)
  pp("c-foo(0)", decodedC.foo(0))
  pp("c-id", decodedC.id)

  val encodedD = np.pretty(d.asJson)
  val decodedD = decode[Base](encodedD).right.get
  pp("d-encode", encodedD)
  pp("d-decode", decodedD)
  pp("d-foo", decodedD.foo)
  pp("d-foo(0)", decodedD.foo(0))
  pp("d-id", decodedD.id)

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
