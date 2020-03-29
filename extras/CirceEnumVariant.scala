package circeeg.extras

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class CirceEnumVariant(
  encodeOnly: Boolean = false,
  decodeOnly: Boolean = false
) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CirceEnumVariantMacro.impl
}

private class CirceEnumVariantMacro(val c: whitebox.Context) {
  import c.universe._

  private[this] def changeFirstTypeNameToTermName(t: Tree): Tree =
    t match {
      case tq"$base.$child" => q"$base.${child.toTermName}"
      case tq"$child" => q"${TermName(child.toString)}"
    }

  private[this] def typeCheckExpressionOfType(typeTree: Tree): Type =
    c.typeCheck(changeFirstTypeNameToTermName(typeTree)).tpe

  private[this] def computeType(tpt: Tree): Type = {
    val calculatedType = c.typeCheck(tpt.duplicate, silent = true, withMacrosDisabled = true).tpe
    val resultType = if (tpt.tpe == null) calculatedType else tpt.tpe

    if (resultType == NoType) {
      typeCheckExpressionOfType(tpt)
    } else {
      resultType
    }
  }

  def impl(annottees: Tree*): Tree = {
    annottees match {
      case (clsDef @ q"$mods class $className(..$fields) extends ..$parents { ..$members }") :: _ if fields.length == 1 => {
        val classTypeName = className
        val classTermName = classTypeName.toTermName
        val classTypeStr = classTypeName.decodedName.toString

        val encoderTermName = TermName("__encode" + classTypeStr)
        val decoderTermName = TermName("__decode" + classTypeStr)

        // tq"circeeg.util.X.type" gives the following showRaw
        // SingletonTypeTree(Select(Select(Ident(TermName("circeeg")), TermName("util")), TermName("X")))
        // val rawSingletonType = tq"circeeg.util.X.type" gives the following showRaw for rawSingletonType
        // Select(Select(Ident(TermName("circeeg")), TermName("util")), TermName("X"))
        // We will follow the above to get to what we need

        // fieldTypeSelect gives the following example
        // Select(Select(Ident(TermName("circeeg")), TermName("util")), TypeName("X"))
        // Note the very last element (actually the first because prefix notation) is TypeName
        // We need to convert to TermName to get the companion object
        val field = fields(0)
        val fieldName = field.name
        val fieldTypeSelect = field.tpt

        // Changing the TypeName to TermName gives us the companion object
        val fieldTermSelect = changeFirstTypeNameToTermName(fieldTypeSelect)

        q"""
        $clsDef

        object $classTermName {
          def apply = circeeg.extras.Func.mapResult($fieldTermSelect.apply _)(new $classTypeName(_))
        }

        implicit val $encoderTermName: io.circe.Encoder[$classTypeName] = new io.circe.Encoder[$classTypeName] {
          final def apply(v: $classTypeName) = v.$fieldName.asJson
        }

        implicit val $decoderTermName: io.circe.Decoder[$classTypeName] = new io.circe.Decoder[$classTypeName] {
          final def apply(c: io.circe.HCursor) = for { v <- c.as[$fieldTypeSelect] } yield { new $classTypeName(v) }
        }
        """
      }
      case _ => c.abort(
        c.enclosingPosition,
        "Invalid annotation target: class must extends a trait and contain only a single param value")
    }
  }
}