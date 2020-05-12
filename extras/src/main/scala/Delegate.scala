package circeeg.extras

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.Context

class delegate extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro delegateMacro.impl
}

object delegateMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def reportInvalidAnnotationTarget() {
      c.error(c.enclosingPosition, "This annotation can only be used on vals")
    }

    def changeFirstTypeNameToTermName(t: Tree): Tree =
      t match {
        case tq"$base.$child" => q"$base.${child.toTermName}"
        case tq"$child" => q"${TermName(child.toString)}"
      }

    def typeCheckExpressionOfType(typeTree: Tree): Type = {
      c.typecheck(tree = typeTree, mode = c.TYPEmode).tpe
    }

    def computeType(tpt: Tree): Type = {
      if (tpt.tpe != null) {
        tpt.tpe
      } else {
        typeCheckExpressionOfType(tpt)
      }
    }

    def addDelegateMethods(valDef: ValDef, addToClass: ClassDef) = {
      val decls = computeType(valDef.tpt).decls
      val valTerms = decls.filter(_.asTerm.isVal)

      // e.g. case class X(v: Int), this creates both "v" (isVal) and "v " (!isVal)
      // Need to rid the latter method associated to the val
      val valSpaceTrimmedNames = valTerms.map(_.name.toString.trim).toList
      
      // Need to rid the methods associated to the enclosing object
      val allMethodsInDelegate = decls
        .filter(!_.asTerm.isVal)
        .filter(m => !valSpaceTrimmedNames.contains(m.asTerm.name.toString))
        .filter(_.asTerm.name.toString != "copy")
        .filter(_.asTerm.name.toString != "copy$default$1")
        .filter(_.asTerm.name.toString != "productPrefix")
        .filter(_.asTerm.name.toString != "productArity")
        .filter(_.asTerm.name.toString != "productElement")
        .filter(_.asTerm.name.toString != "productIterator")
        .filter(_.asTerm.name.toString != "canEqual")
        .filter(_.asTerm.name.toString != "hashCode")
        .filter(_.asTerm.name.toString != "toString")
        .filter(_.asTerm.name.toString != "equals")

      val ClassDef(mods, name, tparams, Template(parents, self, body)) = addToClass

      // TODO better filtering - allow overriding
      val existingMethods = body.flatMap(tree => tree match {
        case DefDef(_, n, _, _, _, _) => Some(n)
        case _ => None
      }).toSet

      val methodsToAdd = allMethodsInDelegate.filter(method => !existingMethods.contains(method.name.toTermName))

      val newMethods = for {
        methodToAdd <- methodsToAdd
      } yield {
        val methodSymbol = methodToAdd.asMethod

        val vparamss = methodSymbol.paramss.map(_.map {
          paramSymbol => ValDef(
            Modifiers(Flag.PARAM, tpnme.EMPTY, List()),
            paramSymbol.name.toTermName,
            TypeTree(paramSymbol.typeSignature),
            EmptyTree)
        })

        val invocationTree = methodSymbol.typeSignature match {
          case NullaryMethodType(_) => Select(Ident(valDef.name), methodSymbol.name)
          case _ =>
            Apply(
              Select(Ident(valDef.name), methodSymbol.name),
              methodSymbol.paramss.flatMap(_.map(param => Ident(param.name)))) // TODO - multi params list
        } 

        val methodDef = DefDef(Modifiers(),
          methodSymbol.name,
          List(), // TODO - type parameters
          vparamss,
          TypeTree(methodSymbol.returnType),
          invocationTree)

        methodDef
      }

      ClassDef(mods, name, tparams, Template(parents, self, body ++ newMethods))
    }

    val inputs = annottees.map(_.tree).toList
    val (_, expandees) = inputs match {
      case (param: ValDef) :: (enclosing: ClassDef) :: rest if param.isDef => {
        val newEnclosing = addDelegateMethods(param, enclosing)
        (param, newEnclosing :: rest)
      }
      case (param: TypeDef) :: (rest @ (_ :: _)) => reportInvalidAnnotationTarget(); (param, rest)
      case _ => reportInvalidAnnotationTarget(); (EmptyTree, inputs)
    }
    val outputs = expandees
    c.Expr[Any](Block(outputs, Literal(Constant(()))))
  }
}
