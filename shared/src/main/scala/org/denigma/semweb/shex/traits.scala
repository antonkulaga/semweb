package org.denigma.semweb.shex

import org.denigma.semweb.rdf.{Quad, Trip, Res}
import org.denigma.semweb.sparql.{Variable, Pat}


trait ToTriplets{
  def toTriplets(subject:Res):Set[Trip]
}

trait ToQuads
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad]
}

trait WithPatterns {
  def empty:PatternResult = (Set.empty[Pat],Map.empty[String,Variable])

  type PatternResult = (Set[Pat],Map[String,Variable])

}

trait ToPatterns extends WithPatterns
{
  /**
   * Tries to make patterns with provided variables
   * @param res
   * @return
   */
  //def toPatterns(res:Res,vars:Map[String,Variable] = Map.empty):Try[Set[Pat]]

  def toPatterns(res:Res):PatternResult


}

