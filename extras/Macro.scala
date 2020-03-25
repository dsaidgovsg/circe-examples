package circeeg.extras

import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

// @compileTimeOnly("enable macro paradise to expand macro annotations")
// class identity extends StaticAnnotation {
//   def macroTransform(annottees: Any*): Any = macro ???
// }

// object identityMacro {
//   def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
//     import c.universe._
//     val inputs = annottees.map(_.tree).toList
//     val (annottee, expandees) = inputs match {
//       case (param: ValDef) :: (rest @ (_ :: _)) => (param, rest)
//       case (param: TypeDef) :: (rest @ (_ :: _)) => (param, rest)
//       case _ => (EmptyTree, inputs)
//     }
//     println((annottee, expandees))
//     val outputs = expandees
//     c.Expr[Any](Block(outputs, Literal(Constant(()))))
//   }
// }

class CirceEnumComponent(
  encodeOnly: Boolean = false,
  decodeOnly: Boolean = false
) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CirceEnumComponentMacros.impl
}

// private[generic] class CirceEnumComponentMacros(val c: whitebox.Context) extends JsonCodecMacros {
private class CirceEnumComponentMacros(val c: whitebox.Context) {
  import c.universe._
  import io.circe.{Decoder, Encoder, HCursor, Json}
  import Func._

  private[this] def isFinalCaseClass(clsDef: ClassDef) =
    clsDef.mods.hasFlag(Flag.CASE) && clsDef.mods.hasFlag(Flag.FINAL)

  def changeFirstTypeNameToTermName(t: Tree) =
    t match {
      case tq"${base}.${child}" => q"${base}.${TermName(child.toString)}"
      case tq"${child}" => q"${TermName(child.toString)}"
    }

  def impl(annottees: Tree*): Tree = annottees match {
    case (clsDef: ClassDef) :: Nil =>
      clsDef match {
        case q"final case class $className(..$fields) extends ..$parents" if fields.length == 1 => {
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
            def apply = (${fieldTermSelect}.apply _).mapResult(new ${classTypeName}(_))
          }

          implicit val ${encoderTermName}: Encoder[${classTypeName}] = new Encoder[${classTypeName}] {
            final def apply(v: ${classTypeName}) = v.v.asJson
          }

          implicit val ${decoderTermName}: Decoder[${classTypeName}] = new Decoder[${classTypeName}] {
            final def apply(c: HCursor) = for { v <- c.as[${fieldTypeSelect}] } yield { new ${classTypeName}(v) }
          }
          """
        }

        case _ => c.abort(
          c.enclosingPosition,
          "Invalid annotation target: final case class must contain only a single param value")
      }

    case _ => c.abort(
      c.enclosingPosition,
      "Invalid annotation target: must be a final case class wrapped within a companion object of a serializable sealed trait")
  }
}
