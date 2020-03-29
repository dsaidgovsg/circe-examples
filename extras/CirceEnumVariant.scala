package circeeg.extras

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.reflect.ClassTag

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

  private[this] def typeCheckExpressionOfType(typeTree: Tree): Type = {
    // println(showRaw(typeTree))
    val tpe = c.typeCheck(changeFirstTypeNameToTermName(typeTree)).tpe
    // println(showRaw(tpe))
    tpe
    // val someValueOfTypeStringX = reify {
    //   def x[T](): T = throw new Exception
    //   x[String]()
    // }

    // println(showRaw(someValueOfTypeStringX))

    // val someValueOfTypeString = q"""
    // {
    //   def x[T](): T = throw new Exception
    //   x[String]()
    // }
    // """

    // println(showRaw(someValueOfTypeString))

    // val Expr(Block(stats, Apply(TypeApply(someValueFun, _), someTypeArgs))) = someValueOfTypeString
    // val someValueOfGivenType = Block(stats, Apply(TypeApply(someValueFun, List(typeTree)), someTypeArgs))
    // val someValueOfGivenType = someValueOfTypeString
    // typeTree.tpe

    // val someValueOfGivenTypeChecked = c.typeCheck(someValueOfGivenType)
    // someValueOfGivenTypeChecked.tpe
  }

  private[this] def computeType(tpt: Tree): Type = {
    // println(showRaw(tpt))
    // val x = c.Type(tpt)
    // println(x)
    // x.tpe
    // println(showRaw(tpt))
    // val x = c.universe.TypeName(tpt.toString)
    // Type(x)
    // Type(tpt)
    // println(tpt.isType)
    // println(tpt.tpe)
    // tpt.tpe

    val calculatedType = c.typeCheck(tpt.duplicate, silent = true, withMacrosDisabled = true).tpe
    val resultType = if (tpt.tpe == null) calculatedType else tpt.tpe

    if (resultType == NoType) {
      typeCheckExpressionOfType(tpt)
    } else {
      resultType
    }

    // if (tpt.tpe != null) {
    //   tpt.tpe
    // } else {
    //   val calculatedType = c.typeCheck(tpt.duplicate, silent = true, withMacrosDisabled = true).tpe
    //   val result = if (tpt.tpe == null) calculatedType else tpt.tpe

    //   println(result)
    //   result

    //   // if (result == NoType) {
    //   //   typeCheckExpressionOfType(tpt)
    //   // } else {
    //   //   result
    //   // }
    // }
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

        // val decls = computeType(field.tpt).decls
        // println(showRaw(decls))

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

// class CirceEnumDerive[T](base: Class[T]) extends StaticAnnotation {
//   def macroTransform(annottees: Any*): Any = macro CirceEnumDeriveMacro.impl[T]
// }

// private class CirceEnumDeriveMacro(val c: whitebox.Context) {
//   import c.universe._

//   private[this] def changeFirstTermNameToTypeName(t: Tree): Tree =
//     t match {
//       case q"$base.$child" => tq"$base.${child.toTypeName}"
//       case q"$child" => tq"${TypeName(child.toString)}"
//     }

//   private[this] val macroName: Tree = {
//     c.prefix.tree match {
//       case Apply(Select(New(name), _), _) => name
//       case _ => c.abort(c.enclosingPosition, "Unexpected macro application")
//     }
//   }

//   private[this] val baseTermName: Tree = {
//     c.prefix.tree match {
//       case q"new $macroName($base)" => base
//       case _ => c.abort(c.enclosingPosition, s"Unsupported arguments supplied to @$macroName")
//     }
//   }

//   private[this] val baseTypeName: Tree = changeFirstTermNameToTypeName(baseTermName)

//   def impl[T](annottees: Tree*): Tree = {
//     annottees match {
//       case (traitDef @ q"sealed trait $tpname") :: (companionDef: ModuleDef) :: Nil => {
//         // println(companionDef)
//         // println(showRaw(companionDef))
//         q"""
//         sealed trait $tpname extends $baseTypeName
//         $companionDef
//         """
//       }

//       case _ => c.abort(
//         c.enclosingPosition,
//         "Invalid annotation target: must be a sealed trait not extending other traits, and with companion object")
//     }
//   }
// }
