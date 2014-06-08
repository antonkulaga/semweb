package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.validation._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.LongLiteral
import org.scalax.semweb.rdf.StringLiteral
import org.scalax.semweb.rdf.DoubleLiteral
import org.scalax.semweb.rdf.StringLangLiteral

object PropertyModel {
  def clean(res:Res) = PropertyModel(res,Map.empty,Valid)

  val empty = PropertyModel.clean(null)
}

trait Model {

  def id:Res

}

case class PropertyModel(resource:Res,properties:Map[IRI,Set[RDFValue]],validation:ValidationResult = Valid ) extends Model{

  type ModelUpdater =(IRI,RDFValue,PropertyModel)=>PropertyModel

  def isValid = validation == Valid
  lazy val violations: Set[Violation] = validation match {
    case Failed(vs)=>vs
    case _=>Set.empty[Violation]
  }

  def isEmpty = resource == null

  def isClean = properties.isEmpty


  def replace(iri:IRI,value:RDFValue):  PropertyModel = this.copy(properties = properties.updated(iri,Set(value)))

  def replace(iri:IRI,text:String,lang:String):  PropertyModel  = this.replace(iri,StringLangLiteral(text,lang))
  def replace(iri:IRI,text:String):  PropertyModel  = this.replace(iri,StringLiteral(text))
  def replace(iri:IRI,value:Double):  PropertyModel = this.replace(iri,DoubleLiteral(value))
  def replace(iri:IRI,value:Long):  PropertyModel  = this.replace(iri,LongLiteral(value))

  def update(iri:IRI,value:RDFValue)(implicit update:ModelUpdater): PropertyModel = update(iri,value,this)
  def update(iri:IRI,value:String)(implicit update:ModelUpdater): PropertyModel  = this.update(iri,StringLiteral(value))
  def update(iri:IRI,value:String,lang:String)(implicit update:ModelUpdater): PropertyModel = this.update(iri,StringLangLiteral(value,lang))
  def update(iri:IRI,value:Double)(implicit update:ModelUpdater): PropertyModel = this.update(iri,DoubleLiteral(value))
  def update(iri:IRI,value:Long)(implicit update:ModelUpdater): PropertyModel = this.update(iri,LongLiteral(value))

  override def id: Res = resource
}
