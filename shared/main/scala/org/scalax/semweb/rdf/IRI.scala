package org.scalax.semweb.rdf
import org.scalax.semweb.rdf.vocabulary._


/*
implementation of openrdf URI class
 */
case class IRI(uri:String) extends IRILike
{


  def /(child:String): IRI = IRI( stringValue / child )
  def /(child:IRI): IRI = this / child.stringValue

}

trait IRILike extends IRIPatEl with Res{

  def uri:String

  require(uri.contains(":"), "uri string must by URL")

  lazy val lastIndex = Math.max(uri.lastIndexOf("#"),uri.lastIndexOf("/"))

  lazy val getLocalName: String = uri.substring(lastIndex)

  lazy val getNamespace: String = uri.substring(0,this.lastIndex)

  override lazy val stringValue: String = this.uri

  override def toString: String = "<"+uri+">"

  override def hashCode: Int = uri.hashCode



 // override def isIRI = true
}


trait IRIPatEl extends ResourcePatEl