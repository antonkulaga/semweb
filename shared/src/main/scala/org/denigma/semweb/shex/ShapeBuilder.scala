package org.denigma.semweb.shex

import org.denigma.semweb.rdf.vocabulary.RDFS
import org.denigma.semweb.rdf.{RDFValue, IRI, RDFBuilder, Res}

import scala.collection.immutable._
import com.softwaremill.quicklens._


/*object Property{

  def apply(id:Res,property:IRI) = {
    new WithShapeProperty(ArcRule(property,id))
  }
}
case class WithShapeProperty(arc:ArcRule = ArcRule.empty)
{

  def occurs(c:Cardinality) = modify(this)(t=>t.pointer.occurs).setTo(c)
  def hasPriority(prior: Int) = modify(this)(t=>t.pointer.priority).setTo(Some(prior))
  def hasNoPriority = modify(this)(t=>t.pointer.priority).setTo(None)
  def default(value:RDFValue) = modify(this)(t=>t.pointer.default).setTo(Some(value))
  def from(vals:IRI*) = modify(this)(t=>t.pointer.value).setTo(ValueSet(vals.toSet))
  def startsWith(start:IRI) = modify(this)(t=>t.pointer.value).setTo(ValueStem(start))
  def of(tp:IRI) = modify(this)(t=>t.pointer.value).setTo(ValueType(tp))
  def oneOf(params: IRI*) = from(params:_*) //produces valueset
  def ofShape(shape:Shape) = modify(this)(t=>t.pointer.value).setTo(ValueReference(shape.id))
  def isCalled(title:String) = modify(this)(t=>t.pointer.title).setTo(Some(title))

  def result = if(arc==ArcRule.empty) None else Some(arc) //temporal will be deleted in future
}*/

/*
trait WithValueProperty {
  protected var vc: ValueClass = ValueType(RDFS.RESOURCE)

  type This = this.type

  def from(vals:IRI*):This = {
    vc = ValueSet(vals.toSet)
    this
  }

  def startsWith(start:IRI):This = {
    vc = ValueStem(start)
    this
  }


  def of(tp: IRI):This = {
    vc = res2ValueType(tp)
    this
  }

  def oneOf(params: IRI*):This = {
    vc = ValueSet(params.toSet)
    this
  }

  def ofShape(sh:Shape):This = {
    vc = ValueReference(sh.id.asResource)
    this
  }


}
trait WithTitledProperty {
  protected var title: Option[String] = None

  type This = this.type

  def isCalled(tlt:String):This = {
    title = Some(tlt)
    this
  }

}
/**
 * For nice shape building
 * @param propertyIRI
 */
class WithShapeProperty(id:Res,propertyIRI:IRI) extends WithValueProperty with WithTitledProperty{
  protected var occ: Cardinality = Plus
  protected var priority: Option[Int] = None
  protected var defaultValue:Option[RDFValue] = None
  lazy val result: Option[ArcRule] = Some(ArcRule(Label.apply(id), propertyIRI, vc, occ, Seq.empty, this.priority, this.title,default = this.defaultValue))

  override type This = this.type

  def occurs(c: Cardinality) = {
    occ = c
    this
  }

  def occurs(from:Int,to:Int) = {
    occ = Cardinality(from,to)
    this
  }

  def hasPriority(prior: Int) = {
    this.priority = Some(prior)
    this
  }

  def hasNoPriority = {
    this.priority = None
    this
  }


  def default(v:RDFValue) = {
    this.defaultValue = Some(v)
    this
  }
}

*/


case class ShapeBuilder(shapeIRI:IRI,pointer:ArcRule = ArcRule.empty,rules:Set[Rule] = Set.empty){

  self=> //to avoid confusion when copying
  protected def genId(property:IRI) = shapeIRI / property.localName

  
  lazy val andRule =  new AndRule(rules+self.pointer,Label.apply(shapeIRI))
  lazy val shape:Shape = Shape(shapeIRI,andRule)
  
  def -- : ShapeBuilder = this.withNewPointer

  protected def withNewPointer = if(pointer==ArcRule.empty) self else self.copy(pointer = ArcRule.empty,rules = rules+self.pointer)

  def has(property:IRI): ShapeBuilder = this.copy(pointer = ArcRule(property,genId(property)))

  def and(prop:IRI) = this.withNewPointer.has(prop) 


  def occurs(c:Cardinality) = modify(this)(t=>t.pointer.occurs).setTo(c)
  def hasPriority(prior: Int) = modify(this)(t=>t.pointer.priority).setTo(Some(prior))
  def hasNoPriority = modify(this)(t=>t.pointer.priority).setTo(None)
  def default(value:RDFValue) = modify(this)(t=>t.pointer.default).setTo(Some(value))
  def from(vals:IRI*) = modify(this)(t=>t.pointer.value).setTo(ValueSet(vals.toSet))
  def startsWith(start:IRI) = modify(this)(t=>t.pointer.value).setTo(ValueStem(start))
  def of(tp:IRI) = modify(this)(t=>t.pointer.value).setTo(ValueType(tp))
  def oneOf(params: IRI*) = from(params:_*) //produces ValueSet
  def ofShape(shape:Shape) = modify(this)(t=>t.pointer.value).setTo(ValueReference(shape.id))
  def isCalled(title:String) = modify(this)(t=>t.pointer.title).setTo(Some(title))

  /**
   * If it is Empty rule then None, Some(pointer) otherwise
   * @return
   */
  def pointerOption = if(pointer==ArcRule.empty) None else Some(pointer) //temporal will be deleted in future

  def hasRule(rule:Rule) = this.copy(rules = self.rules + rule)
}

/*
class ShapeBuilder(shapeIRI:IRI) extends RDFBuilder[WithShapeProperty]{

  protected def genId(property:IRI) = shapeIRI / property.localName
  
  def has(iri:IRI) = this -- hasProperty(genId(iri), iri)
  //def has(property: WithShapeProperty) = this -- hasProperty(genId(iri), iri)


  /**
   *
   * @param res id of arcrule
   * @param iri property name
   * @return
   */
  def hasProperty(res:Res,iri:IRI) = this -- Property(res,iri)

  def hasRule(rule:Rule):this.type  = {
    this.rules = this.rules + rule
    this
  }
  


  //TODO: rewrite
  protected var rules:Set[Rule] = Set.empty




  /**
   * Creates shape
   * @return
   */
  def result:Shape = {
    val allRules: Set[Rule] = this.values.collect{case v if v.arc!=ArcRule.empty=>v.arc} ++ this.rules
    val and: AndRule = new AndRule(allRules,Label.apply(shapeIRI))
    Shape(shapeIRI,and)
  }

}*/
