package org.scalax.semweb.composites

import org.scalax.semweb.messages.{Read, StorageMessage, StringQueryMessages}
import org.scalax.semweb.{shex, rdf}
import org.scalax.semweb.rdf._


import prickle.{CompositePickler, PicklerPair}
import prickle._

//!Don't do this. Not Necessary
import Pickler._
/**
  implicit conversions for different types
  */

trait  CommonComposites{


//for missing common classes
}

trait ShapePicklers extends RDFComposites{
  self:RDFComposites=>
  
  import org.scalax.semweb.shex.{BNodeLabel, IRILabel,Label,ValueClass,
  ValueType,ValueAny,ValueSet,ValueStem,
  NameClass,NameStem,NameAny,ExactlyOne,Plus,Star,Opt,NameTerm,Cardinality,ArcRule} //because of crazy behaviour of prickle macro
  
  implicit lazy val labelPickler = CompositePickler[Label].concreteType[IRILabel].concreteType[BNodeLabel]

  //implicit lazy val valueTypePickler = materializePickler[ValueType]
 
  implicit lazy val valueClassPickler = CompositePickler[ValueClass].concreteType[ValueType].concreteType[ValueSet].concreteType[ValueStem].concreteType[ValueAny]
  
 implicit lazy val nameClassPickler = CompositePickler[NameClass].concreteType[NameTerm].concreteType[NameStem].concreteType[NameAny]

 implicit lazy val cardinalityPickler = CompositePickler[Cardinality]
   .concreteType[Plus.type].concreteType[Star.type].concreteType[ExactlyOne.type].concreteType[shex.Range]

 implicit lazy val ArcRulePickler =  CompositePickler[ArcRule]


}

trait RDFComposites{
  implicit lazy val rdfValue: PicklerPair[RDFValue] = withLiterals(withResource(CompositePickler[RDFValue]))

  implicit lazy val resourcePair: PicklerPair[Res] = withResource(CompositePickler[Res])
  
  def withResource[T >: Res <:RDFValue](pair:PicklerPair[T]):PicklerPair[T] = pair.concreteType[BlankNode].concreteType[IRI]

  def withLiterals[T>: Lit <:RDFValue](pair:PicklerPair[T]):PicklerPair[T] =pair
    .concreteType[rdf.TypedLiteral].concreteType[rdf.StringLiteral]
    .concreteType[rdf.DateTimeLiteral].concreteType[rdf.DateLiteral]
    .concreteType[rdf.DoubleLiteral].concreteType[rdf.IntLiteral]
}

trait MessagesComposites{
  
  implicit val stringQueryMessage = CompositePickler[StringQueryMessages.StringMessage]
    .concreteType[StringQueryMessages.Ask].concreteType[StringQueryMessages.Construct]
    .concreteType[StringQueryMessages.Select].concreteType[StringQueryMessages.Update]

  implicit val reasQueryMessage = CompositePickler[Read.ReadMessage]
    .concreteType[Read.Bind].concreteType[Read.Construct].concreteType[Read.Query].
    concreteType[Read.Question].concreteType[Read.Search].concreteType[Read.Select]

}

object SemanticComposites extends ShapePicklers with MessagesComposites