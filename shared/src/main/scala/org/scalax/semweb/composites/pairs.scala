package org.scalax.semweb.composites

import org.scalax.semweb.messages.{Read, StorageMessage, StringQueryMessages}
import org.scalax.semweb.shex.{OrRule, AndRule}
import org.scalax.semweb.{shex, rdf}
import org.scalax.semweb.rdf._


import prickle.{CompositePickler, PicklerPair}
import prickle._

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
ValueType,ValueAny,ValueSet,ValueStem,
NameClass,NameStem,NameAny,ExactlyOne,Plus,Star,Opt,
NameTerm,Cardinality,Action,ArcRule,Rule,SubjectRule,ContextRule} //because of crazy behaviour of prickle macro and implicit search

trait ShapePicklers extends RDFComposites{
  self:RDFComposites=>
  

  
  implicit lazy val labelPickler = CompositePickler[Label].concreteType[IRILabel].concreteType[BNodeLabel]

  implicit lazy val valueTypePickler = CompositePickler[ValueType]
  
 
  implicit lazy val valueClassPickler = CompositePickler[ValueClass].concreteType[ValueType].concreteType[ValueSet].concreteType[ValueStem].concreteType[ValueAny]
  
  implicit lazy val nameClassPickler = CompositePickler[NameClass].concreteType[NameTerm].concreteType[NameStem].concreteType[NameAny]

  implicit lazy val cardinalityPickler = CompositePickler[Cardinality]
   .concreteType[Plus.type].concreteType[Star.type].concreteType[ExactlyOne.type].concreteType[shex.Range]
  
  implicit lazy val actionPickler = CompositePickler[Action]

  implicit lazy val arcPickler = Pickler.materializePickler[ArcRule]
  implicit lazy val arcUnpickler = Unpickler.materializeUnpickler[ArcRule]

 /* implicit lazy val andPickler = Pickler.materializePickler[AndRule]
  implicit lazy val andUnpickler = Unpickler.materializeUnpickler[AndRule]

  implicit lazy val orPickler = Pickler.materializePickler[OrRule]
  implicit lazy val orUnpickler = Unpickler.materializeUnpickler[OrRule]*/

  implicit lazy val subPickler = Pickler.materializePickler[SubjectRule]
  implicit lazy val subUnpickler = Unpickler.materializeUnpickler[SubjectRule]

  implicit lazy val conPickler = Pickler.materializePickler[ContextRule]
  implicit lazy val conUnpickler = Unpickler.materializeUnpickler[ContextRule]

/*
  implicit lazy val rulePickler = CompositePickler[Rule].concreteType[AndRule].concreteType[OrRule]
    .concreteType[ArcRule].concreteType[SubjectRule].concreteType[ContextRule]
*/


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