package org.scalax.semweb.rdf



/*
implementation of openrdf URI class
 */
case class IRI(uri:String) extends CanBePredicate with Res
{

  def /(child:String): IRI = if(stringValue.endsWith("/") || stringValue.endsWith("#")) IRI(stringValue+child) else IRI(stringValue+ "/" +child) //  IRI( stringValue / child )
  def /(child:IRI): IRI = this / child.stringValue


  require(uri.contains(":"), "uri string must by URL")


  /**
   * Either "#" or "/"
   */
  lazy val lastSegment = Math.max(uri.lastIndexOf("#"),uri.lastIndexOf("/"))

  lazy val localName: String = uri.substring(lastSegment)

  lazy val namespace: String = uri.substring(0,this.lastSegment)

  override lazy val stringValue: String = this.uri

  override def toString: String = "<"+uri+">"

  override def hashCode: Int = uri.hashCode

}


trait CanBePredicate extends CanBeSubject