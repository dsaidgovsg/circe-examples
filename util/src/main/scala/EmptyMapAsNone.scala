package circeeg.util

import scala.util.Right

import io.circe.{Decoder, Encoder, HCursor, Json}

case class EmptyMapAsNone[A](val toOption: Option[A])

object EmptyMapAsNone {
  import io.circe.syntax._

  final def apply[A](x: A): EmptyMapAsNone[A] = EmptyMapAsNone[A](Option(x))
  final def empty[A]: EmptyMapAsNone[A] = EmptyMapAsNone[A](None)

  implicit def emanEncoder[A: Encoder]: Encoder[EmptyMapAsNone[A]] = new Encoder[EmptyMapAsNone[A]] {
    final def apply(x: EmptyMapAsNone[A]): Json =
      if (x.toOption.isEmpty) {
          // It can be any key type since the value is meant to be empty
          Map[String, A]().asJson
      } else {
          x.toOption.asJson
      }
  }

  implicit def emanDecoder[A: Decoder]: Decoder[EmptyMapAsNone[A]] = new Decoder[EmptyMapAsNone[A]] {
    final def apply(x: HCursor): Decoder.Result[EmptyMapAsNone[A]] =
      // It can be any key type since the value is meant to be empty
      x.as[Map[String, A]] match {
        case Right(v) if v.isEmpty => Right(EmptyMapAsNone.empty)  // Try empty dict first
        case _ => x.as[Option[A]].right.map(EmptyMapAsNone(_)) // Try Option[A] last (we accept null as None too)
      }
  }
}
