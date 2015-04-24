package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI

object DCElements {
  val namespace: String = "http://purl.org/dc/elements/1.1/"
  val elems = IRI(namespace)


  //TODO : to uppercase
  val title: IRI = elems / "title"
  val creator: IRI = elems / "creator"
  val subject: IRI = elems / "subject"
  val description: IRI = elems / "description"
  val publisher: IRI = elems / "publisher"
  val contributor: IRI = elems / "contributor"
  val date: IRI = elems / "date"
  val `type`: IRI = elems / "type"
  val format: IRI = elems / "format"
  val identifier: IRI = elems / "identifier"
  val source: IRI = elems / "source"
  val language: IRI = elems / "language"
  val relation: IRI = elems / "relation"
  val coverage: IRI = elems / "coverage"
  val rights: IRI = elems / "rights"
}
