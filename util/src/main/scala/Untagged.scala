package circeeg.util

import io.circe.{Decoder, Encoder, HCursor, Json}

/**
 * Provides a Circe untagged enumeration (arity-2).
 * 
 * The decoding is T1 biased, i.e. if you provide types with similar decoding implementations, the
 * first decoding success, starting from T1, will be take on the decoded value. Types with higher
 * decoding priority should therefore be placed from T1.
 */
class Untagged2[T1, T2] private {
  override def toString(): String = {
    this match {
      case Untagged2.V1(v) => Untagged2.V1(v).toString()
      case Untagged2.V2(v) => Untagged2.V2(v).toString()
    }
  }

  // v1.isDefined || v2.isDefined must be true after construction
  private var v1: Option[T1] = None
  private var v2: Option[T2] = None
}

object Untagged2 {
  import cats.syntax.either._
  import io.circe.syntax._

  sealed trait Variant[T1, T2]

  case class V1[T1, T2](v: T1) extends Variant[T1, T2] {
    override def toString(): String = s"V1(${v})"
  }

  object V1 {
    def unapply[T1, T2](u: Untagged2[T1, T2]): Option[T1] = u.v1
  }

  case class V2[T1, T2](v: T2) extends Variant[T1, T2] {
    override def toString(): String = s"V2(${v})"
  }

  object V2 {
    def unapply[T1, T2](u: Untagged2[T1, T2]): Option[T2] = u.v2
  }

  def from1[T1, T2](v: T1): Untagged2[T1, T2] = {
    val untagged = new Untagged2[T1, T2]
    untagged.v1 = Some(v)
    untagged
  }

  def from2[T1, T2](v: T2): Untagged2[T1, T2] = {
    val untagged = new Untagged2[T1, T2]
    untagged.v2 = Some(v)
    untagged
  }

  implicit def untagged2Encoder[T1: Encoder, T2: Encoder]: Encoder[Untagged2[T1, T2]] =
    new Encoder[Untagged2[T1, T2]] {
      final def apply(u: Untagged2[T1, T2]): Json =
        u match {
          case V1(v) => v.asJson
          case V2(v) => v.asJson
        }
  }

  implicit def untagged2Decoder[T1: Decoder, T2: Decoder]: Decoder[Untagged2[T1, T2]] = 
    new Decoder[Untagged2[T1, T2]] {
      final def apply(v: HCursor): Decoder.Result[Untagged2[T1, T2]] =
        v.as[T1] match {
          case Right(a) => Right(Untagged2.from1(a))
          case _ => v.as[T2].map(Untagged2.from2(_))
        }
  }
}
