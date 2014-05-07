package org.scalax.semweb.rdf.vocabulary

import org.scalax.semweb.rdf.IRI

object XSD {
  val rdfSyntax				= "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  val namespace					= "http://www.w3.org/2001/XMLSchema#"
  
  val xsd = IRI(namespace)


  //TODO to uppercase
  val StringDatatypeIRI 		=  xsd / "string"
  val LangStringDatatypeIRI  = xsd / "language"
  val BooleanDatatypeIRI 	= xsd / "boolean"
  val IntegerDatatypeIRI 	= xsd / "integer"
  val LongDatatypeIRI 	= xsd / "long"
  val DoubleDatatypeIRI 		= xsd / "double"
  val DecimalDatatypeIRI 	= xsd / "decimal"
}
