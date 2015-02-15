package org.scalax.semweb.shex

import org.scalax.semweb.rdf.vocabulary.{RDFS, RDF, WI}
import org.scalax.semweb.rdf.{Quad, Res, Trip, _}

object SubjectRule {
  val clazz: IRI = WI.pl("Subject")
  val property =  WI.pl("has_subject_settings")
  val base = WI.pl("base")
}

case class SubjectRule(id:Label = Rule.genRuleLabel(),
                       value:ValueClass = ValueType.apply(RDFS.RESOURCE), title:Option[String] = None,
                       base:Option[String] = None) extends FieldRule(id,value,title)
{

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = 
    super.toQuads(subject,SubjectRule.property,SubjectRule.clazz)(context)++
      this.base.fold(Set.empty[Quad])(t=>Set(Quad(me, WI.PLATFORM.BASE,t,context)))

  override def toTriplets(subject: Res): Set[Trip] = 
    super.toTriplets(subject,SubjectRule.property,SubjectRule.clazz)++
      this.base.fold(Set.empty[Trip])(t=>Set(Trip(me, WI.PLATFORM.BASE,t)))
  
  def isCalled(tlt:String) = copy(title = Some(tlt))
  
  def headOption = if(id.asResource==WI.PLATFORM.EMPTY) None else Some(this)

  def hasBase(str:String) = this.copy(base = Some(str))

  def hasBase(b:IRI) = copy(base = Some(b.stringValue))
  
  def startsWith(start:IRI) = copy(value = ValueStem(start))
    
  def of(tp: IRI) = copy(value = res2ValueType(tp))
    
  
}

object ContextRule {
  val clazz: IRI = WI.pl("Context")
  val property =  WI.pl("has_context_settings")
}

case class ContextRule(id:Label = Rule.genRuleLabel(),value:ValueClass, title:Option[String] = None) extends FieldRule(id,value,title)
{

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = toQuads(subject,ContextRule.property,SubjectRule.clazz)(context)

  override def toTriplets(subject: Res): Set[Trip] = super.toTriplets(subject,ContextRule.property,ContextRule.clazz)

  def isCalled(tlt:String) = copy(title = Some(tlt))

  def headOption = if(id.asResource==WI.PLATFORM.EMPTY) None else Some(this)

  def startsWith(start:IRI) = copy(value = ValueStem(start))

  def of(tp: IRI) = copy(value = res2ValueType(tp))
}

abstract class FieldRule(id:Label,value:ValueClass, title:Option[String] = None) extends Rule
{

  lazy val me = id.asResource

  def toQuads(subj: Res,field:IRI,clazz:IRI)(implicit context: Res): Set[Quad] = {
    val aboutMe = Set(
      Quad(subj, field, me, context),
      Quad(me, RDF.TYPE, clazz, context)
    )
    val tlt: Set[Quad] =  this.title.fold(Set.empty[Quad])(t=>Set(Quad(me, vocabulary.DCElements.title,t,context)))
    aboutMe ++  tlt  ++ value.toQuads(me)(context)

  }

  def toTriplets(subj:Res,field:IRI,clazz:IRI):Set[Trip] = {
    val aboutMe = Set(
      Trip(subj, field, me),
      Trip(me, RDF.TYPE, clazz)
    )
    val tlt: Set[Trip] =  this.title.fold(Set.empty[Trip])(t=>Set(Trip(me, vocabulary.DCElements.title,t)))
    aboutMe++ tlt  ++ value.toTriplets(me)
  }
}
