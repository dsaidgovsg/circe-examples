package circeeg.extras

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class CirceEnumVariant(case_class_fwd: Boolean = true) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CirceEnumVariantMacro.impl
}

private class CirceEnumVariantMacro(val c: whitebox.Context) {
  import c.universe._

  private[this] val macroName: Tree = {
    c.prefix.tree match {
      case Apply(Select(New(name), _), _) => name
      case _ => c.abort(c.enclosingPosition, "Unexpected macro application")
    }
  }

  private[this] val caseClassForwarding: Boolean = {
    c.prefix.tree match {
      case q"new ${`macroName`}()" => true
      case q"new ${`macroName`}(case_class_fwd = true)" => true
      case q"new ${`macroName`}(case_class_fwd = false)" => false
      case _ => c.abort(c.enclosingPosition, s"Unsupported arguments supplied to @$macroName")
    }
  }

  private[this] def changeFirstTypeNameToTermName(t: Tree): Tree =
    t match {
      case tq"$base.$child" => q"$base.${child.toTermName}"
      case tq"$child" => q"${TermName(child.toString)}"
    }

  private[this] def typeCheckExpressionOfType(typeTree: Tree): Type = {
    c.typecheck(tree = typeTree, mode = c.TYPEmode).tpe
  }

  private[this] def computeType(tpt: Tree): Type = {
    if (tpt.tpe != null) {
      tpt.tpe
    } else {
      typeCheckExpressionOfType(tpt)
    }
  }

  private[this] def extractCtor(target: ValDef, innerCaseClass: Tree, classTypeName: TypeName) = {
    // We assume only one constructor (i.e. default apply method for companion object)
    val targetTpe = computeType(target.tpt)
    val targetCompanion = targetTpe.typeSymbol.companion
    val targetCtor = targetTpe.decls.filter(_.isConstructor).head
    val targetCtorSym = targetCtor.asMethod
    val paramss = targetCtorSym.paramLists

    val vparamss = paramss.map(_.zipWithIndex.map {
      case (paramSymbol, i) =>
        // This is the way to get default value from the companion object apply method
        val methodWithDefault = TermName(targetCtorSym.name + "$default$" + (i + 1)).encodedName.toTermName

        targetTpe.companion.member(methodWithDefault) match {
          case NoSymbol => q"val ${paramSymbol.name.toTermName}: ${paramSymbol.typeSignature}"  // No default value
          case _ => q"val ${paramSymbol.name.toTermName}: ${paramSymbol.typeSignature} = ${targetCompanion}.$methodWithDefault"
        }
    })

    val invocationTree = targetCtorSym.typeSignature match {
      case NullaryMethodType(_) => q"$target.$targetCtorSym"  // Non-parentheses method
      case _ =>
        val flattenedParams = targetCtorSym.paramss.flatMap(_.map(target => Ident(target.name)))
        q"new $classTypeName($innerCaseClass(..$flattenedParams))"
    }

    q"def apply(...$vparamss): $classTypeName = $invocationTree"
  }

  def impl(annottees: Tree*): Tree = {
    annottees match {
      case (clsDef @ q"$mods class $tpname(..$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }") :: _ if paramss.length == 1 => {
        val classTypeName = tpname
        val classTermName = classTypeName.toTermName
        val classTypeStr = classTypeName.decodedName.toString

        val encoderTermName = TermName("__encode" + classTypeStr)
        val decoderTermName = TermName("__decode" + classTypeStr)

        // tq"circeeg.util.X.type" gives the following showRaw
        // SingletonTypeTree(Select(Select(Ident(TermName("circeeg")), TermName("util")), TermName("X")))
        // val rawSingletonType = tq"circeeg.util.X.type" gives the following showRaw for rawSingletonType
        // Select(Select(Ident(TermName("circeeg")), TermName("util")), TermName("X"))
        // We will follow the above to get to what we need

        // paramTypeSelect gives the following example
        // Select(Select(Ident(TermName("circeeg")), TermName("util")), TypeName("X"))
        // Note the very last element (actually the first because prefix notation) is TypeName
        // We need to convert to TermName to get the companion object
        val param = paramss.head
        val paramName = param.name
        val paramTypeSelect = param.tpt

        // Changing the TypeName to TermName gives us the companion object
        val paramTermSelect = changeFirstTypeNameToTermName(paramTypeSelect)

        val q"..$imports" = q"""
          import cats.syntax.either._
          import io.circe.syntax._
          """

        val q"..$codecVals" = q"""
          implicit val $encoderTermName: io.circe.Encoder[$classTypeName] = new io.circe.Encoder[$classTypeName] {
            final def apply(v: $classTypeName) = v.$paramName.asJson
          }

          implicit val $decoderTermName: io.circe.Decoder[$classTypeName] = new io.circe.Decoder[$classTypeName] {
            final def apply(c: io.circe.HCursor) = for { v <- c.as[$paramTypeSelect] } yield { new $classTypeName(v) }
          }
          """

        caseClassForwarding match {
          case true =>
            val ctor = extractCtor(param, paramTermSelect, classTypeName)

            q"""
            ..$imports; $clsDef; ..$codecVals

            object $classTermName {
              $ctor
            }
            """
          case false => q"..$imports; $clsDef; ..$codecVals"
        }
      }
      case _ => c.abort(
        c.enclosingPosition,
        "Invalid annotation target: class must extends a trait and contain only a single param value")
    }
  }
}
