package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{RDFElement, IRI}


object ASK
{

  def apply(params:RDFElement*): AskRDF = {
    val q = new AskRDF()
    if(params.length>0) q.WHERE(params:_*)
    q
  }
}

class AskRDF extends WithWhere
{
  def stringValue = s"ASK  \n${WHERE.stringValue}\n"



}

//TODO: make full support of from
case class From(iri:IRI) extends RDFElement {


  override def stringValue: String = s"\nFROM <${iri.stringValue}> \n"
}
