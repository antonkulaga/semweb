package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{Res, IRI}

trait ShExResult

case class Pass(assignment: Map[Res,IRI]) extends ShExResult {

  def assign(node: Res, iri: IRI): ShExResult = {
       Pass(assignment = assignment + (node -> iri))
  }

}

case class NoPass() extends ShExResult

