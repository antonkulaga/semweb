package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI

object XSD {
 // val rdfSyntax				= "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  val namespace					= "http://www.w3.org/2001/XMLSchema#"
  
  val xsd = IRI(namespace)


  val StringDatatypeIRI 		=  xsd / "string"
  val LangStringDatatypeIRI  = xsd / "language"
  val BooleanDatatypeIRI 	= xsd / "boolean"
  val IntegerDatatypeIRI 	= xsd / "integer"
  val IntDatatypeIRI 	= xsd / "int"

  //val LongDatatypeIRI 	= xsd / "long"
  val DoubleDatatypeIRI 		= xsd / "double"
  val DecimalDatatypeIRI 	= xsd / "decimal"

  val Date = xsd / "date"
  val DateTime = xsd / "dateTime"
}
