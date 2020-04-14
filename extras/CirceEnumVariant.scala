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

  // private[this] def extractCtor(param: ValDef, classTermName: TermName) = {
  private[this] def extractCtor(param: ValDef, innerTerm: Tree, classTypeName: TypeName) = {
    // We assume only one constructor (i.e. default apply method for companion object)
    val ctor = computeType(param.tpt).decls.filter(_.isConstructor).head
    val methodSymbol = ctor.asMethod
    val paramss = methodSymbol.paramLists

    val vparamss = paramss.map(_.map {
      paramSymbol => ValDef(
        Modifiers(Flag.PARAM, tpnme.EMPTY, List()),
        paramSymbol.name.toTermName,
        TypeTree(paramSymbol.typeSignature),
        EmptyTree)
    })

    // We assume the number of sets of brackets is just 1
    // i.e. (v: Int), and not (v: Int)(x: Int)
    val vparams = vparamss.head

    val invocationTree = methodSymbol.typeSignature match {
      case NullaryMethodType(_) =>
        Select(Ident(param.name), methodSymbol.name)
      case _ =>
        val pams = methodSymbol.paramss.flatMap(_.map(param => Ident(param.name)))
        q"new $classTypeName($innerTerm(..$pams))"
    }

    q"def apply(..$vparams): $classTypeName = $invocationTree"
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
