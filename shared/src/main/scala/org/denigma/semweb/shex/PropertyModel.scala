package org.denigma.semweb.shex

import org.denigma.semweb.rdf._
import org.denigma.semweb.shex.validation._
import org.denigma.semweb.rdf.IRI
//import org.denigma.semweb.rdf.LongLiteral
import org.denigma.semweb.rdf.StringLiteral
import org.denigma.semweb.rdf.DoubleLiteral
import org.denigma.semweb.rdf.StringLangLiteral
import com.softwaremill.quicklens._

object PropertyModel {

  def clean(res:Res) = PropertyModel(res,Map.empty[IRI,Set[RDFValue]],Valid)

  val empty = PropertyModel.clean(vocabulary.RDF.NIL)

  def apply(resource:Res,props:(IRI,RDFValue)*): PropertyModel = {
    PropertyModel(resource,props.map{case (key,value)=>key->Set(value)}.toMap)
  }


}

trait Model {

  def id:Res

}

/**
 * Case class that contains subject with properties and values as well some validation information
 * In future I am going to deprecate it in favour of PointedGraph
 * @param resource
 * @param properties
 * @param validation
 */
case class PropertyModel(resource:Res,properties:Map[IRI,Set[RDFValue]],validation:ValidationResult = Valid ) extends Model{

  type ModelUpdater =(IRI,RDFValue,PropertyModel)=>PropertyModel

  def isValid = validation == Valid
  lazy val violations: Set[Violation] = validation match {
    case Failed(vs)=>vs
    case _=>Set.empty[Violation]
  }

  /**
   * Checkf if property with some name contains value
   * @param property
   * @param value
   * @return
   */
  def contains(property:IRI,value:RDFValue) = properties.get(property) match {
    case Some(ps)=> ps.contains(value)
    case _ => false
  }

  def isEmpty = resource == null

  def isClean = properties.isEmpty


  //TODO: move to lenses ( https://github.com/adamw/quicklens )
  def replace(iri:IRI,value:RDFValue):  PropertyModel = this.copy(properties = properties.updated(iri,Set(value)))
  def replace(iri:IRI,text:String,lang:String):  PropertyModel  = this.replace(iri,StringLangLiteral(text,lang))
  def replace(iri:IRI,text:String):  PropertyModel  = this.replace(iri,StringLiteral(text))
  def replace(iri:IRI,value:Double):  PropertyModel = this.replace(iri,DoubleLiteral(value))
  def replace(iri:IRI,value:Int):  PropertyModel  = this.replace(iri,IntLiteral(value))

  //TODO: move to lenses ( https://github.com/adamw/quicklens )
  def update(iri:IRI,value:RDFValue)(implicit update:ModelUpdater): PropertyModel = update(iri,value,this)
  def update(iri:IRI,value:String)(implicit update:ModelUpdater): PropertyModel  = this.update(iri,StringLiteral(value))
  def update(iri:IRI,value:String,lang:String)(implicit update:ModelUpdater): PropertyModel = this.update(iri,StringLangLiteral(value,lang))
  def update(iri:IRI,value:Double)(implicit update:ModelUpdater): PropertyModel = this.update(iri,DoubleLiteral(value))
  def update(iri:IRI,value:Int)(implicit update:ModelUpdater): PropertyModel = this.update(iri,IntLiteral(value))

  override def id: Res = resource
}
