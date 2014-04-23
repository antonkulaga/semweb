package org.scalax.semweb

import org.scalax.semweb.rdf.IRI
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



  lazy val rs =  "http://open-services.net/ns/core#"


  def range(m: Long, n: Long): Cardinality = {
    require(n > m)
    Cardinality(min = m, max = n)
  }

  /** Utility to generate rules from arcs */
  def envolve(s: ArcRule): Rule = OrRule(List(AndRule(List(s))))



}
