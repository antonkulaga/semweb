package org.scalax.semweb.shex.parser

import scala.collection.immutable.Map
import org.scalax.semweb.rdf.IRI


case class PrefixMap(map: Map[String,IRI]) {

  def getIRI(prefix : String) : Option[IRI] = {
    map.get(prefix)
  }
  
  def contains(prefix: String) : Boolean = map.contains(prefix)

  def addPrefix(prefix : String, iri: IRI) : PrefixMap = {
    PrefixMap(map + (prefix -> iri))
  }

  override def toString : String = {
    "Prefix map: " + map
  }
}

object PrefixMap {
  def empty = PrefixMap(Map[String,IRI]())
  
  def addPrefix(prefix: String, iri: IRI)(pm: PrefixMap) : PrefixMap =
    pm.addPrefix(prefix,iri)

}

