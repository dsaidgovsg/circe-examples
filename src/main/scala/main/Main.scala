package circeeg.main

import io.circe._
import io.circe.generic.auto._
import io.circe.generic.extras._
import io.circe.parser._
import io.circe.syntax._

import circeeg.util.{BarVal, BazVal, Foo}
import circeeg.util.conf._

object Main extends App {
  //
  // BarVal
  //

  val origBar: Foo = BarVal(13, Some(14.0))

  val encodedBar = origBar.asJson.noSpaces
  println(encodedBar)

  val decodedBar = decode[Foo](encodedBar)
  println(decodedBar)

  val decodedBar2 = decode[Foo]("""{"bar_val":{"val_int":13,"val_dbl":null}}""")
  println(decodedBar2)

  val decodedBar3 = decode[Foo]("""{"bar_val":{"val_int":13}}""")
  println(decodedBar3)

  //
  // BazVal
  //

  val origBaz: Foo = BazVal(Vector("123", "456"))

  val encodedBaz = origBaz.asJson.noSpaces
  println(encodedBaz)

  val decodedBaz = decode[Foo](encodedBaz)
  println(decodedBaz)

  val decodedBaz2 = decode[Foo]("""{"baz_val":{"val_vec":[]}}""")
  println(decodedBaz2)
}
