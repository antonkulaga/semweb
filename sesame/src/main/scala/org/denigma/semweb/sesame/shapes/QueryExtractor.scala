package org.denigma.semweb.sesame.shapes

import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{RDF, RDFS}
import org.denigma.semweb.shex._
import org.denigma.semweb.sparql
import org.denigma.semweb.sparql._

import scala.annotation.tailrec


trait QueryExtractor {

  /**
   * Makes a union of elements
   * @param elems
   * @return
   */
  def foldUnion(elems:Seq[RDFElement]):RDFElement = elems.size match {
    case 1 =>elems.head
    case 2 => Union(elems.head,elems.tail.head)
    case num =>
      val (a,b)= elems.splitAt(elems.size/2)
      foldUnion(Seq(foldUnion(a),foldUnion(b)))
  }

}
