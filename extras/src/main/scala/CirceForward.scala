package circeeg.extras

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class CirceForward extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CirceForwardMacro.impl
}

private class CirceForwardMacro(val c: whitebox.Context) {
  import c.universe._

  def impl(annottees: Tree*): Tree = {
    annottees match {
      case (clsDef @ q"case class $className(..$fields) extends { ..$_ } with ..$_ { $_ => ..$_ }") :: _ if fields.length == 1 => {
        val classTypeName = className
        val classTermName = classTypeName.toTermName
        val classTypeStr = classTypeName.decodedName.toString

        val encoderTermName = TermName("__encode" + classTypeStr)
        val decoderTermName = TermName("__decode" + classTypeStr)

        val field = fields(0)
        val fieldName = field.name
        val fieldTypeSelect = field.tpt

        val q"$objTerm" = q"""
          object $classTermName {
            implicit val $encoderTermName: io.circe.Encoder[$classTypeName] = new io.circe.Encoder[$classTypeName] {
              final def apply(v: $classTypeName) = io.circe.syntax.EncoderOps(v.$fieldName).asJson
            }

            implicit val $decoderTermName: io.circe.Decoder[$classTypeName] = new io.circe.Decoder[$classTypeName] {
              final def apply(c: io.circe.HCursor) = for { v <- c.as[$fieldTypeSelect] } yield { new $classTypeName(v) }
            }
          }
          """

        q"$clsDef; $objTerm"
      }
      case _ => c.abort(
        c.enclosingPosition,
        "Invalid annotation target: must be a case class that contain only a single param value for forwarding")
    }
  }
}
