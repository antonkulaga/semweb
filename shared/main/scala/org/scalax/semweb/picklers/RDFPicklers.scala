package org.scalax.semweb.picklers

import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.rdf._
import org.scalax.semweb.sparql.Pat
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sparql.Pat
import org.scalax.semweb.rdf.BlankNode
import org.scalax.semweb.rdf.StringLiteral
import org.scalax.semweb.shex.validation.{JustFailure, Valid}
import org.scalax.semweb.shex.PropertyModel
import org.scalajs.spickling.PicklerRegistry

trait RDFPicklers extends CommonPicklers{

  self:PicklerRegistry=>


  def registerRdf() = {


    //Semantic
    register[IRI]
    register[BlankNode]
    register[StringLiteral]
    register[BooleanLiteral]
    register[DoubleLiteral]
    register[DecimalLiteral]
    register[StringLangLiteral]
    register[LongLiteral]
    register[IntegerLiteral]
    register[AnyLit]


    register[Pat]
    register[Trip]
    register[Quad]

    register(Valid)
    register[JustFailure]

    //register[Map[IRI,RDFValue]]
    register[PropertyModel]






  }

}
