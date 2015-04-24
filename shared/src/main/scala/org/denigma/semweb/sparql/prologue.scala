package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.rdf.vocabulary
import org.denigma.semweb.rdf.vocabulary._
import scala.text.DocCons


case class Prologue(base:Base,prefixes:List[Prefix] = List.empty[Prefix]) extends PrologueDecl
{

}

case object DefaultPrologue extends PrologueDecl{
  override def base: Base = Base(IRI(WI.RESOURCE))

  override val prefixes: List[Prefix] = List(
  Prefix("de",IRI("http://denigma.org/resource/")),
  Prefix("dc",vocabulary.DCElements.elems),
  Prefix("foaf",FOAF.foaf),
  Prefix("owl",vocabulary.OWL.owl),
  Prefix("xsd",vocabulary.XSD.xsd),
  Prefix("rdf",vocabulary.RDF.ns),
  Prefix("rdfs",vocabulary.RDFS.rdfs)

  )
}

trait PrologueDecl {
  def base:Base
  val prefixes:List[Prefix]

}


/**
 * Prefix class
 */
case class Prefix(prefix:String,iri:IRI) extends NameSpace(prefix,iri)
abstract class NameSpace(prefix:String,iri:IRI) 
{
//  def compareTo(o: Namespace): Int =
//    if (getPrefix == o.getPrefix)  if (getName == o.getName) 0 else getName.compareTo(o.getName)
//
//    else getPrefix.compareTo(o.getPrefix)


  def getPrefix: String = prefix

   val getName: String = iri.stringValue


  override def toString = s"\nPREFIX $prefix: <$getName>"
}


case class Base(iri:IRI) extends NameSpace("",iri){
  //override def toString = s"\nBASE <$getName>"

  override def toString:String = ":"+iri.toString

}