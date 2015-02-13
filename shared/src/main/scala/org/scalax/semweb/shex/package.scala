package org.scalax.semweb

import org.scalax.semweb.rdf.{Res, BlankNode, IRI}
import org.scalax.semweb.rdf.vocabulary._

/**
 * Package objects with the most important constants and implicits
 */
package object shex {
  lazy val foaf = "http://xmlns.com/foaf/0.1/"
  lazy val xsd  = "http://www.w3.org/2001/XMLSchema#"
  lazy val shex = "http://www.w3.org/2013/ShEx/ns#"
  lazy val typeShexLiteral  	= ValueType(v = IRI(shex + "Literal"))
  lazy val typeShexIRI  		= ValueType(v = IRI(shex + "IRI"))
  lazy val typeShexBNode  	= ValueType(v = IRI(shex + "BNode"))
  lazy val typeShexNonLiteral	= ValueType(v = IRI(shex + "NonLiteral"))
  lazy val typeXsdString		= ValueType(v = IRI(xsd  + "string"))

  lazy val NoActions : Seq[Action] = Seq.empty[Action]



  lazy val rs =  IRI("http://open-services.net/ns/core#")
  lazy val se =  IRI("http://www.w3.org/2013/ShEx/Definition#") //URL does not work but it is in


  def range(m: Int, n: Int): Cardinality = {
    require(n > m)
    Cardinality(min = m, max = n)
  }

  implicit def iri2Label(iri:IRI): IRILabel = IRILabel(iri)
  implicit def bNode2Label(b:BlankNode): BNodeLabel = BNodeLabel(b)

  implicit def iri2Name(iri:IRI): NameTerm = NameTerm(iri)

  /**
   * Converts resource to value type
   * @param res
   * @return
   */
  implicit def res2ValueType(res:Res): ValueType = ValueType(res)

  implicit def res2Label(res:Res): Label with Product with Serializable = res match
  {
    case iri:IRI=>this.iri2Label(iri)
    case bnode:BlankNode=>this.bNode2Label(bnode)
  }




}
