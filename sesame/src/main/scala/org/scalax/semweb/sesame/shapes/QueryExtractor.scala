package org.scalax.semweb.sesame.shapes
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDF, RDFS}
import org.scalax.semweb.shex._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._

import scala.annotation.tailrec


trait QueryExtractor {

  def foldUnion(elems:Seq[RDFElement]):RDFElement = elems.size match {
    case 1 =>elems.head
    case 2 => Union(elems.head,elems.tail.head)
    case num =>
      val (a,b)= elems.splitAt(elems.size/2)
      foldUnion(Seq(foldUnion(a),foldUnion(b)))
  }

}
