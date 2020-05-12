package circeeg.util



trait Expr2Impl {
  def eval: Int
}

abstract class LitImpl(v: Int) extends Expr2Impl {
  override def eval: Int = v
}

abstract class AddImpl(v: Seq[Expr2Impl]) extends Expr2Impl {
  override def eval: Int = v.map(_.eval).sum
}

abstract class Add2Impl(v1: Expr2Impl, v2: Expr2Impl) extends Expr2Impl {
  override def eval: Int = v1.eval + v2.eval
}
