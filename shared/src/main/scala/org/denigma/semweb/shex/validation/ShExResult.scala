package org.denigma.semweb.shex.validation

import org.denigma.semweb.rdf.{IRI, Res}

trait ShExResult

case class Pass(assignment: Map[Res,IRI]) extends ShExResult {

  def assign(node: Res, iri: IRI): ShExResult = {
       Pass(assignment = assignment + (node -> iri))
  }

}

case class NoPass() extends ShExResult

