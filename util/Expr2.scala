package circeeg.util

import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.extras.{CirceForward, CirceEnumVariant, delegate}
import circeeg.util.Conf.custom

@ConfiguredJsonCodec
sealed trait Expr2 extends Expr2Impl

object Expr2 {
  @CirceEnumVariant
  case class Lit(v: Int) extends LitImpl(v) with Expr2

  @CirceEnumVariant
  case class Add(v: Seq[Expr2]) extends AddImpl(v) with Expr2

  // Cannot forward since two params
  case class Add2(v1: Expr2, v2: Expr2) extends Add2Impl(v1, v2) with Expr2
}
