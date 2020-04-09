package circeeg.util

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax.EncoderOps
import scala.util.{Left, Right}

case class Sorl[T](val toList: List[T])

object Sorl {
  final def apply[T](vs: T*): Sorl[T] = Sorl[T](List(vs: _*))

  implicit def sorlEncoder[T: Encoder]: Encoder[Sorl[T]] = new Encoder[Sorl[T]] {
    final def apply(v: Sorl[T]): Json =
      if (v.toList.length == 1) {
          v.toList.head.asJson
      } else {
          v.toList.asJson
      }
  }

  implicit def sorlDecoder[T: Decoder]: Decoder[Sorl[T]] = new Decoder[Sorl[T]] {
    final def apply(v: HCursor): Decoder.Result[Sorl[T]] =
      v.as[T]
        .right.map(Sorl(_))  // Try single T first
        .left.flatMap(_ => v.as[List[T]].right.map(Sorl(_)))  // Try List[T] last
  }
}
