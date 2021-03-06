package org.denigma.schemas.common

import org.denigma.semweb.rdf.vocabulary.{RDF, RDFS, WI, XSD}
import org.denigma.semweb.rdf.{IRI, vocabulary}


trait BasicSchema {
  val de = IRI("http://denigma.org/resource/")
  val gero = IRI("http://gero.longevityalliance.org/")
  val entrez = IRI("http://ncbi.nlm.nih.gov/gene/")

  val prefixes: Seq[(String, String)] = Seq(
    "rdf"->RDF.namespace,
    "rdfs"->RDFS.namespace,
    "xsd"->XSD.namespace,
    "owl"->vocabulary.OWL.namespace,
    "dc"->vocabulary.DCElements.namespace,
    "pl"->(WI.PLATFORM.namespace+"/"),
    "shex"-> org.denigma.semweb.shex.rs.stringValue,
    "def"->org.denigma.semweb.shex.se.stringValue,
    "pmid"->"http://ncbi.nlm.nih.gov/pubmed/",
    "de"->de.stringValue,
    "gero"->gero.stringValue,
    "entrez"->entrez.stringValue
  )


}
