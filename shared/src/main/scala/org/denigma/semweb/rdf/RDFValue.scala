package org.denigma.semweb.rdf

/**
RDF value implementation
  */
trait RDFValue extends CanBeObject{

  /**
   * Just fot the sake of nice input
   * @return
   */
  def label:String

  override def equals(that: Any): Boolean = that match  {

    case value:RDFValue=>value.stringValue==stringValue
    case _=>false
  }

  //override def stringValue: String = this.toString
}

trait CanBeObject extends RDFElement


trait RDFElement {

  def stringValue:String

}