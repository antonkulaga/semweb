package org.scalax.semweb.rdf.vocabulary

import org.scalax.semweb.rdf.IRI

object RDF {
  /**
   * Recommended prefix for the RDF namespace: "rdf"
   */
  val prefix: String = "rdf"
  /**
   * An immutable {@link Namespace} constant that represents the RDF namespace.
   */
  val namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"


  val ns = IRI(namespace)
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#type */
  val TYPE: IRI =  ns / "type"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Property */
  val PROPERTY: IRI = ns / "Property"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral */
  val XMLLITERAL: IRI = ns / "XMLLiteral"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#subject */
  val SUBJECT: IRI = ns /  "subject"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate */
  val PREDICATE: IRI = ns /  "predicate"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#object */
  val OBJECT: IRI = ns /  "object"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement */
  val STATEMENT: IRI = ns /  "Statement"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag */
  val BAG: IRI = ns /  "Bag"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt */
  val ALT: IRI = ns /  "Alt"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq */
  val SEQ: IRI = ns /  "Seq"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#value */
  val VALUE: IRI = ns /  "value"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#li */
  val LI: IRI = ns /  "li"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#List */
  val LIST: IRI = ns /  "List"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#first */
  val FIRST: IRI = ns /  "rest"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#rest */
  val REST: IRI = ns / "rest"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#nil */
  val NIL: IRI = ns /  "nil"
  /** http://www.w3.org/1999/02/22-rdf-syntax-ns#langString */
  val LANGSTRING: IRI = ns /  "langString"

}
