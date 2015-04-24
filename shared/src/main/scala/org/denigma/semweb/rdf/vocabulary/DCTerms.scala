package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI

object DCTerms {
  val namespace: String = "http://purl.org/dc/terms/"
  val terms = IRI(namespace)
  val description: IRI = terms /  "description"
  val hasVersion: IRI = terms /  "hasVersion"
  val issued: IRI = terms /  "issued"
  val modified: IRI = terms /  "modified"
  val publisher: IRI = terms /  "publisher"
  val reviewer: IRI = terms /  "reviewer"
  val Review: IRI = terms /  "Review"
  val text: IRI = terms /  "text"
  val title: IRI = terms /  "title"
}
