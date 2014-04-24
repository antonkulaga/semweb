package org.scalax.semweb.rdf



/*
implementation of openrdf URI class
 */
case class IRI(uri:String) extends IRILike
{

  def /(child:String): IRI = if(stringValue.endsWith("/") || stringValue.endsWith("#")) IRI(stringValue+child) else IRI(stringValue+ "/" +child) //  IRI( stringValue / child )
  def /(child:IRI): IRI = this / child.stringValue

}

trait IRILike extends IRIPatEl with Res{

  def uri:String

  require(uri.contains(":"), "uri string must by URL")


  lazy val lastIndex = Math.max(uri.lastIndexOf("#"),uri.lastIndexOf("/"))

  lazy val localName: String = uri.substring(lastIndex)

  lazy val namespace: String = uri.substring(0,this.lastIndex)

  override lazy val stringValue: String = this.uri

  override def toString: String = "<"+uri+">"

  override def hashCode: Int = uri.hashCode



 // override def isIRI = true
}


trait IRIPatEl extends ResourcePatEl