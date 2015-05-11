package org.denigma.semweb.shex

import org.denigma.semweb.rdf.vocabulary.RDFS
import org.denigma.semweb.rdf.{RDFValue, IRI, RDFBuilder, Res}

import scala.collection.immutable._
import com.softwaremill.quicklens._

case class ShapeBuilder(shapeIRI:IRI,pointer:ArcRule = ArcRule.empty,rules:Set[Rule] = Set.empty){
  self=> //to avoid confusion when copying

  def basedOn(shape:Shape) = copy(rules = self.rules ++ shape.arcRules())


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
  def ofShape(shapeRes:Res) = modify(this)(t=>t.pointer.value).setTo(ValueReference(shapeRes))
  def isCalled(title:String) = modify(this)(t=>t.pointer.title).setTo(Some(title))
  def isInvisible = modify(this)(t=>t.pointer.priority).setTo(Some(-1))

  /**
   * If it is Empty rule then None, Some(pointer) otherwise
   * @return
   */
  def pointerOption = if(pointer==ArcRule.empty) None else Some(pointer) //temporal will be deleted in future

  def hasRule(rule:Rule) = this.copy(rules = self.rules + rule)
}