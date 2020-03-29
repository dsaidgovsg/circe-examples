package circeeg.util

import cats.syntax.either._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps  // .asJson needs this

import circeeg.extras.{CirceEnumVariant, delegate}
import circeeg.util.Conf.custom

// trait FooBase {
//   def method1(): String
//   def method2(p1: String): Long
//   def method3(p1: String): Int
//   def method4(p1: String, p2: Long): String
// }

// @ConfiguredJsonCodec
// case class FooImpl(x: Int) extends FooBase {
//   override def method1() = "m1"
//   override def method2(p1: String) = 42L
//   override def method3(p1: String) = p1.length()
//   override def method4(p1: String, p2: Long) = s"ok $p1 $p2"
// }

// @ConfiguredJsonCodec
// sealed trait FooEnum extends FooBase

// // Putting the @delegate while wrapping the class in object
// // will cause a macro fatal error
// // Likely due to Scala macro bugs
// object FooEnum {
//   @ConfiguredJsonCodec
//   final case class Impl(@delegate v: FooImpl) extends FooEnum
// }

// object FooEnum

@ConfiguredJsonCodec
sealed trait Base extends Useless

object Base {
  // The case class (enum component) names below do not actually have to
  // be exactly the same as the ones in Xyz.scala.
  // They are named the same to easily correlate the two
  // The case class name here however DO determine the automatic inferred
  // JSON key name to use when serde-ing.
  // JsonKey does not work for ADT case class like this unfortunately
  // but at least we have a second-say on how the enum component here is named

  @CirceEnumVariant
  final case class X(@delegate v: circeeg.util.X) extends Base

  @CirceEnumVariant
  final case class Y(@delegate v: circeeg.util.Y) extends Base

  @CirceEnumVariant
  final case class Z(@delegate v: circeeg.util.Z) extends Base

  // A uses arity-3
  @CirceEnumVariant
  final case class A(@delegate v: circeeg.util.A) extends Base
}

// object Base
