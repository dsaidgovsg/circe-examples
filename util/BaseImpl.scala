package circeeg.util

import java.net.Socket

import io.circe.generic.JsonCodec
import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.util.Conf.custom

// The traits here are just to show that for some reason if you need to the
// various enum components extend some traits, as long as the trait are just for
// methods, it has no impact on the serialization hierarchy and neither do they
// need to be circe annotated

trait Useless {
  def foo(): String
  def foo(x: Int): Int
  // TODO - multi params list (https://www.notion.so/542152da398846078c06a8e0301756fb?v=e1a9ffb6299249a2918d753c4760ed6c&p=1c9d4a2a4740499dbed0fb7e3a5ff066)
  // def foo(x: Int)(y: Int): Int
  def id: Int
}

trait UselessToo extends Useless {
  // Purposely take in and return unserializable type
  def bar(x: Array[Socket]): Socket = x(0)
}

// The following case classes totally do not need the above traits and do not
// need to derive from any base traits
// This flips the usual sealed trait which requires the base sealed trait
// (enum base), and the enum components to be located in the same file
// The enum base is actually in Base.scala instead, similar to how Rust enum
// works

@ConfiguredJsonCodec
case class AnyNameIsFine(x: String, y: Int)

@ConfiguredJsonCodec
case class X(v: Int) extends UselessToo {
  override def foo(): String = "X-foo"
  override def foo(x: Int): Int = id + x
  // override def foo(x: Int)(y: Int): Int = foo(x) + y
  override def id: Int = 0
}

@ConfiguredJsonCodec
case class Y(v: Int) extends Useless {
  override def foo(): String = "Y-foo"
  override def foo(x: Int): Int = id + x
  // override def foo(x: Int)(y: Int): Int = foo(x) + y
  override def id: Int = 1
}

@ConfiguredJsonCodec
case class Z(v: String) extends UselessToo {
  override def foo(): String = "Z-foo"
  override def foo(x: Int): Int = id + x
  // override def foo(x: Int)(y: Int): Int = foo(x) + y
  override def id: Int = 2
}

@ConfiguredJsonCodec
case class A(w: Int = 111, y: List[Int] = List(1, 2, 3), z: String = "Hello World!") extends UselessToo {
  override def foo(): String = z
  override def foo(x: Int): Int = x + id + w + y.sum
  // override def foo(x: Int)(y: Int): Int = foo(x) + y
  override def id: Int = w
}

@JsonCodec
case class Empty() extends UselessToo {
  override def foo(): String = "EMPTY!"
  override def foo(x: Int): Int = id + 1
  // override def foo(x: Int)(y: Int): Int = foo(x) + y
  override def id: Int = 123
}
