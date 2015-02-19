package org.scalax.semweb.composites

import org.scalax.semweb.messages.{Read, StorageMessage, StringQueryMessages}
import org.scalax.semweb.shex.{ValueReference, OrRule, AndRule}
import org.scalax.semweb.{shex, rdf}
import org.scalax.semweb.rdf._

import scala.reflect.classTag


import prickle.{CompositePickler, PicklerPair}
import prickle._

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

//!Don't do this. Not Necessary
//import Pickler._
//import Unpickler._
/**
  implicit conversions for different types
  */

trait  CommonComposites{

  //implicit lazy val nonePickler = CompositePickler[None.type ]


  //for missing common classes
}
import org.scalax.semweb.shex.{BNodeLabel, IRILabel,Label,ValueClass,
ValueType,ValueAny,ValueSet,ValueStem,Shape,ShEx,
NameClass,NameStem,NameAny,ExactlyOne,Plus,Star,Opt,
NameTerm,Cardinality,Action,ArcRule,Rule,SubjectRule,ContextRule} //because of crazy behaviour of prickle macro and implicit search

trait ShapePicklers extends RDFComposites{
  self:RDFComposites=>
  

  
  implicit lazy val labelPickler = CompositePickler[Label].concreteType[IRILabel].concreteType[BNodeLabel]

  implicit lazy val valueTypePickler = Pickler.materializePickler[ValueType]
  implicit lazy val valueTypeUnpickler = Unpickler.materializeUnpickler[ValueType]



  implicit lazy val valueClassPickler = CompositePickler[ValueClass].concreteType[ValueType].concreteType[ValueSet].concreteType[ValueStem].concreteType[ValueAny].concreteType[ValueReference]
  
  implicit lazy val nameClassPickler = CompositePickler[NameClass].concreteType[NameTerm].concreteType[NameStem].concreteType[NameAny]

  implicit lazy val cardinalityPickler = CompositePickler[Cardinality]
   .concreteType[Plus.type].concreteType[Star.type].concreteType[Opt.type].concreteType[ExactlyOne.type].concreteType[shex.Range]
  
  implicit lazy val actionPickler = CompositePickler[Action]
  

  implicit lazy val arcPickler = Pickler.materializePickler[ArcRule]
  implicit lazy val arcUnpickler = Unpickler.materializeUnpickler[ArcRule]

  /* and example how Arc rules look like when it is pickled
  {"name": {"#cls": "org.scalax.semweb.shex.NameStem", 
  "#val": {"#id": "4", "s": {"#id": "3", "uri": "http:\/\/ncbi.nlm.nih.gov\/gene\/"}}}, 
  "priority": {"#id": "7", "#elems": [0.0]}, "#id": "11", 
  "id": {"#cls": "org.scalax.semweb.shex.IRILabel", 
  "#val": {"#id": "2", "iri": {"#id": "1", "uri": "http:\/\/gero.longevityalliance.org\/"}}}, 
  "occurs": {"#cls": "org.scalax.semweb.shex.ExactlyOne$", 
  "#val": {"#scalaObj": "org.scalax.semweb.shex.ExactlyOne"}}, 
  "default": {"#id": "10", "#elems": [{"#cls": "org.scalax.semweb.rdf.IRI", 
  "#val": {"#id": "9", "uri": ":hello"}}]}, "actions": 
  {"#id": "6", "#elems": []}, 
  "title": {"#id": "8", "#elems": ["Hello world"]}, 
  "value": {"#cls": "org.scalax.semweb.shex.ValueStem", 
  "#val": {"#id": "5", "s": {"#ref": "1"}}}}
  */


  implicit lazy val subPickler = Pickler.materializePickler[SubjectRule]
  implicit lazy val subUnpickler = Unpickler.materializeUnpickler[SubjectRule]

  implicit lazy val conPickler = Pickler.materializePickler[ContextRule]
  implicit lazy val conUnpickler = Unpickler.materializeUnpickler[ContextRule]
  
  
  implicit lazy val rulePickler: PicklerPair[Rule] = CompositePickler[Rule]
    .concreteType[ArcRule].concreteType[SubjectRule].concreteType[ContextRule]
    .concreteType[AndRule](andPickler,andUnpickler, classTag[AndRule]  )
    .concreteType[OrRule](orPickler,orUnpickler, classTag[OrRule]  )

  
  implicit lazy val andPickler: Pickler[AndRule] = Pickler.materializePickler[AndRule]
  implicit lazy val andUnpickler: Unpickler[AndRule] = Unpickler.materializeUnpickler[AndRule]

  implicit lazy val orPickler: Pickler[OrRule] = Pickler.materializePickler[OrRule]
  implicit lazy val orUnpickler: Unpickler[OrRule] = Unpickler.materializeUnpickler[OrRule]


  implicit lazy val shapePickler = Pickler.materializePickler[Shape]
  implicit lazy val shapeUnpickler = Unpickler.materializeUnpickler[Shape]

  implicit lazy val shexPickler = Pickler.materializePickler[ShEx]
  implicit lazy val shexUnpickler = Unpickler.materializeUnpickler[ShEx]


}


trait RDFComposites extends CommonComposites{
 
  
  implicit lazy val iriPickler = Pickler.materializePickler[IRI]
  implicit lazy val iriUnpickler = Unpickler.materializeUnpickler[IRI]

  implicit lazy val rdfValue: PicklerPair[RDFValue] = withLiterals(withResource(CompositePickler[RDFValue]))

  implicit lazy val resourcePair: PicklerPair[Res] = withResource(CompositePickler[Res])
  
  def withResource[T >: Res <:RDFValue](pair:PicklerPair[T]):PicklerPair[T] = pair.concreteType[BlankNode].concreteType[IRI]

  def withLiterals[T>: Lit <:RDFValue](pair:PicklerPair[T]):PicklerPair[T] =pair
    .concreteType[rdf.TypedLiteral].concreteType[rdf.StringLiteral]
    .concreteType[rdf.DateTimeLiteral].concreteType[rdf.DateLiteral]
    .concreteType[rdf.DoubleLiteral].concreteType[rdf.IntLiteral]
}

trait MessagesComposites{
  

  implicit lazy val stringQueryMessage = CompositePickler[StringQueryMessages.StringMessage]
    .concreteType[StringQueryMessages.Ask].concreteType[StringQueryMessages.Construct]
    .concreteType[StringQueryMessages.Select].concreteType[StringQueryMessages.Update]


  implicit lazy val readQueryMessage = CompositePickler[Read.ReadMessage]
    .concreteType[Read.Bind].concreteType[Read.Construct].concreteType[Read.Query].
    concreteType[Read.Question].concreteType[Read.Search].concreteType[Read.Select]

}

object SemanticComposites extends ShapePicklers with MessagesComposites