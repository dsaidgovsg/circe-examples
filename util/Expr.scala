package circeeg.util

import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.extras.{CirceForward, CirceEnumVariant, delegate}
import circeeg.util.Conf.custom

trait Expr {
  def eval: Int
}

object Expr {
  @CirceForward
  case class Lit(v: Int) extends Expr {
    def eval: Int = v
  }

  @CirceForward
  case class Add(v: Seq[ExprEnum]) extends Expr {
    def eval: Int = v.map(_.eval).sum
  }
}

@ConfiguredJsonCodec
sealed trait ExprEnum extends Expr {
  val v: Expr
  def eval: Int = v.eval
}

object ExprEnum {
  @CirceEnumVariant
  final case class Lit(v: Expr.Lit) extends ExprEnum

  @CirceEnumVariant
  final case class Add(v: Expr.Add) extends ExprEnum
}
