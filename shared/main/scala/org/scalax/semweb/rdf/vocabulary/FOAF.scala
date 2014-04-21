package org.scalax.semweb.rdf.vocabulary

import org.scalax.semweb.rdf.IRI

/**
 * Foaf helper
 */
object FOAF {
  val namespace: String = "http://xmlns.com/foaf/0.1/"
  val foaf = IRI(namespace)
  val PERSON: IRI = foaf / "Person"
  final val NAME: IRI = foaf / "name"
  final val KNOWS: IRI = foaf / "knows"
  final val MBOX: IRI = foaf / "mbox"

}
