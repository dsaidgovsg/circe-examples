package circeeg.util

import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.util.Conf._

// The traits here are just to show that for some reason if you need to the
// various enum components extend some traits, as long as the trait are just for
// methods, it has no impact on the serialization hierarchy and neither do they
// need to be circe annotated

trait Useless
trait UselessToo extends Useless

// The following case classes totally do not need the above traits and do not
// need to derive from any base traits
// This flips the usual sealed trait which requires the base sealed trait
// (enum base), and the enum components to be located in the same file
// The enum base is actually in Base.scala instead, similar to how Rust enum
// works

@ConfiguredJsonCodec
case class X(v: Int) extends UselessToo

@ConfiguredJsonCodec
case class Y(v: Int) extends Useless

@ConfiguredJsonCodec
case class Z(v: String) extends UselessToo

@ConfiguredJsonCodec
case class A(x: Int, y: List[Int], z: String) extends UselessToo
