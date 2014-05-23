package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{RDFValue, IRI}
import org.scalax.semweb.shex.validation._

object PropertyModel {
  lazy val empty = PropertyModel(Map.empty,Valid)
}

case class PropertyModel(properties:Map[IRI,Set[RDFValue]],validation:ValidationResult = Valid ) {

  def isValid = validation == Valid
  lazy val violations: Set[Violation] = validation match {
    case Failed(vs)=>vs
    case _=>Set.empty[Violation]
  }

}
