package org.denigma.semweb.composites

import org.denigma.semweb.rdf
import org.denigma.semweb.rdf.{BlankNode, IRI, RDFValue, Res, _}
import prickle._


trait RDFComposites extends CommonComposites{


  implicit lazy val iriPickler = Pickler.materializePickler[IRI]
  implicit lazy val iriUnpickler = Unpickler.materializeUnpickler[IRI]

  implicit lazy val rdfValue: PicklerPair[RDFValue] = withLiterals(withResource(CompositePickler[RDFValue]))

  implicit lazy val resourcePair: PicklerPair[Res] = withResource(CompositePickler[Res])

  def withResource[T >: Res <:RDFValue](pair:PicklerPair[T]):PicklerPair[T] = pair.concreteType[BlankNode].concreteType[IRI]

  /**
   * Fix to be moved to Semweb
   * @param pair
   * @tparam T
   * @return
   */
  def withLiterals[T>: Lit <:RDFValue](pair:PicklerPair[T]):PicklerPair[T] =pair
    .concreteType[rdf.TypedLiteral].concreteType[rdf.StringLiteral]
    .concreteType[rdf.DateTimeLiteral].concreteType[rdf.DateLiteral]
    .concreteType[rdf.DoubleLiteral].concreteType[rdf.IntLiteral].concreteType[rdf.BooleanLiteral]
}
