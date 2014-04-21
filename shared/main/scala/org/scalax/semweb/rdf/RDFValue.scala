package org.scalax.semweb.rdf

/**
RDF value implementation
 */
trait RDFValue extends ValuePatEl{

  def stringValue:String

  //override def stringValue: String = this.toString
}

trait ValuePatEl extends PatternElement

/*
 TODO: add binding for vars
 */
trait PatternElement extends QueryElement

trait QueryElement {

  def stringValue:String

}