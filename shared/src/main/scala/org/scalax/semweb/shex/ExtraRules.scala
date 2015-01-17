package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.shex._



object SubjectRule{

  val property = rs / "subject_of"

}

case class SubjectRule(id:Label,value:ValueType) extends ValueRule(id,value){
  override def toTriplets(subject: Res): Set[Trip] = {
    Set(Trip(subject, SubjectRule.property, me)) ++ value.toTriplets(subject)

  }

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject, SubjectRule.property, me)) ++ value.toQuads(subject)
  }
}

object ContextRule{
  val property = rs / "has_context"
}


case class ContextRule(id:Label,name:NameClass,value:ValueClass,
                       priority:Option[Int] = None, //the smaller the more important
                       title:Option[String] = None
                        ) extends ValueRule(id,value)
{

  override def toTriplets(subject: Res): Set[Trip] = {
    val tlt: Set[Trip] =  this.title.fold(Set.empty[Trip])(t=>Set(Trip(me, vocabulary.DCElements.title,t)))
    val prior: Set[Trip] = this.priority.fold(Set.empty[Trip])(p=>Set(Trip(me, ArcRule.priority,IntLiteral(p))))
    Set(Trip(subject, ContextRule.property, me)) ++ name.toTriplets(subject) ++ tlt ++ prior ++ value.toTriplets(subject)
  }

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    val tlt: Set[Quad] =  this.title.fold(Set.empty[Quad])(t=>Set(Quad(me, vocabulary.DCElements.title,t,context)))
    val prior: Set[Quad] = this.priority.fold(Set.empty[Quad])(p=>Set(Quad(me, ArcRule.priority,IntLiteral(p),context)))
    Set(Quad(subject, ContextRule.property, me)) ++ name.toQuads(subject)(context) ++ tlt ++ prior++ value.toQuads(subject)
  }
}



abstract class ValueRule(id:Label,valueClass:ValueClass) extends Rule{
  lazy val me = id.asResource




}