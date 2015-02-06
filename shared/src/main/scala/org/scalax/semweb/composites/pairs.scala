package org.scalax.semweb.composites

import org.scalax.semweb.messages.{Read, StorageMessage, StringQueryMessages}
import org.scalax.semweb.rdf
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

trait RDFComposites{
  implicit val rdfValue: PicklerPair[RDFValue] = withLiterals(withResource(CompositePickler[RDFValue]))

  implicit val resourcePair = withResource(CompositePickler[Res])

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

object SemanticComposites extends CommonComposites with RDFComposites with MessagesComposites