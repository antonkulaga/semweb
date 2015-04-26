package org.denigma.semweb.shex

import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.rdf.{Trip, IntLiteral, Quad, Res}


object Bound {
  def apply(limit:Int) = new Bound(limit)
  case object Zero extends Bound(0)
  case object Once extends Bound(1)
  case object Unbound extends Bound(Int.MaxValue)
  case class Bounded(override val limit:Int) extends Bound(limit)


}
class Bound(val limit:Int) {
  def isUnbound = limit==Int.MaxValue
  def isZero = limit==0
}


abstract class Cardinality(min: Bound,max: Bound) extends ToQuads with ToTriplets
{
  require(min.limit<=max.limit) //check that minimum is lower that maximum
}

object Range {
  val minProperty = rs / "minoccurs"
  val maxProperty = rs / "maxoccurs"
}

case class Range(min:Int,max:Int) extends Cardinality(Bound(min),Bound(max))
{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = Set(
    Quad(subject,Range.minProperty,IntLiteral(min),context),
    Quad(subject,Range.maxProperty,IntLiteral(max),context)
  )

  override def toTriplets(subject: Res): Set[Trip] = Set(
    Trip(subject,Range.minProperty,IntLiteral(min)),
    Trip(subject,Range.maxProperty,IntLiteral(max))
  )

}


object Cardinality
{
  //def apply(min: Bound,max: Bound) = new Cardinality(min,max)
  def apply(min: Int,max:Int) = Range(min,max)

}



// Utility definitions


import Bound._

case object ExactlyOne extends Cardinality(Once,Once)
{
  val property = rs / "occurs"
  val obj = rs / "Exactly-one"

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = Set(
    Quad(subject,property,obj,context)
  )

  override def toTriplets(subject: Res): Set[Trip] = Set(
    Trip(subject,property,obj)
  )
}

case object Plus extends Cardinality(Once,Unbound)
{
  val property = rs / "occurs"
  val obj = rs / "One-or-many"

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = Set(
    Quad(subject,this.property,obj,context)
  )

  override def toTriplets(subject: Res): Set[Trip] = Set(
   Trip(subject,this.property,obj)
  )

}

case object Star  extends Cardinality(Zero,Unbound) {
  val property = rs / "occurs"
  val obj = rs / "Zero-or-many"

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = Set(
    Quad(subject,property, obj,context)
  )

  override def toTriplets(subject: Res): Set[Trip] = Set(
    Trip(subject,property, obj)
  )
}

case object Opt extends Cardinality(Zero,Once) {

  val property = rs / "occurs"
  val obj = rs / "Zero-or-one"

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = Set(
    Quad(subject,property, obj,context)
  )

  override def toTriplets(subject: Res): Set[Trip] = Set(
    Trip(subject,property, obj)
  )
}

// lazy val NoId : Label =Label(iri =(""))