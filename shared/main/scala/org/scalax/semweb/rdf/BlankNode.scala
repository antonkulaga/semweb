package org.scalax.semweb.rdf

case class BlankNode(id:String) extends Res with BNodePatEl{

  override def stringValue: String = id

  override def equals(that: Any): Boolean = that match  {

    case value:RDFValue=>value.stringValue==stringValue
    case _=>false

  }

  override def toString = s"_:$id"

}

trait BNodePatEl extends CanBeSubject