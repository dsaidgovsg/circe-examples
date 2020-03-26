package circeeg.extras

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class CirceEnumComponent(
  encodeOnly: Boolean = false,
  decodeOnly: Boolean = false
) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CirceEnumComponentMacros.impl
}

// private[generic] class CirceEnumComponentMacros(val c: whitebox.Context) extends JsonCodecMacros {
private class CirceEnumComponentMacros(val c: whitebox.Context) {
  import c.universe._

  private[this] def isFinalCaseClass(clsDef: ClassDef) =
    clsDef.mods.hasFlag(Flag.CASE) && clsDef.mods.hasFlag(Flag.FINAL)

  def changeFirstTypeNameToTermName(t: Tree) =
    t match {
      case tq"${base}.${child}" => q"${base}.${TermName(child.toString)}"
      case tq"${child}" => q"${TermName(child.toString)}"
    }

  def impl(annottees: Tree*): Tree = {
      annottees match {
        case (clsDef: ClassDef) :: Nil =>
          clsDef match {
            case q"$mods class $className(..$fields) extends ..$parents" if fields.length == 1 => {
              val classTypeName = clsDef.name
              val classTermName = classTypeName.toTermName
              val classTypeStr = classTypeName.decodedName.toString

              val encoderTermName = TermName("encode" + classTypeStr)
              val decoderTermName = TermName("decode" + classTypeStr)

              // tq"circeeg.util.X.type" gives the following showRaw
              // SingletonTypeTree(Select(Select(Ident(TermName("circeeg")), TermName("util")), TermName("X")))
              // val rawSingletonType = tq"circeeg.util.X.type" gives the following showRaw for rawSingletonType
              // Select(Select(Ident(TermName("circeeg")), TermName("util")), TermName("X"))
              // We will follow the above to get to what we need

              // fieldTypeSelect gives the following example
              // Select(Select(Ident(TermName("circeeg")), TermName("util")), TypeName("X"))
              // Note the very last element (actually the first because prefix notation) is TypeName
              // We need to convert to TermName to get the companion object
              val fieldTypeSelect = fields(0).tpt

              // Changing the TypeName to TermName gives us the companion object
              val fieldTermSelect = changeFirstTypeNameToTermName(fieldTypeSelect)

              q"""
              $clsDef
              object ${classTermName} {
                def apply = circeeg.extras.Func.mapResult(${fieldTermSelect}.apply _)(new ${classTypeName}(_))
              }

              implicit val ${encoderTermName}: io.circe.Encoder[${classTypeName}] = new io.circe.Encoder[${classTypeName}] {
                final def apply(v: ${classTypeName}) = v.v.asJson
              }

              implicit val ${decoderTermName}: io.circe.Decoder[${classTypeName}] = new io.circe.Decoder[${classTypeName}] {
                final def apply(c: io.circe.HCursor) = for { v <- c.as[${fieldTypeSelect}] } yield { new ${classTypeName}(v) }
              }
              """
            }

            case _ => c.abort(
              c.enclosingPosition,
              "Invalid annotation target: class must extends a trait and contain only a single param value")
          }

        case _ => c.abort(
          c.enclosingPosition,
          "Invalid annotation target: must be a class")
      }
  }
}
