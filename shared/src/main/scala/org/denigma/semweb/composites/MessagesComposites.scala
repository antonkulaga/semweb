package org.denigma.semweb.composites

import org.denigma.semweb.messages.{Read, ShapeMessages, StringQueryMessages}
import prickle.{CompositePickler, Pickler, Unpickler}

/**
 * Created by antonkulaga on 4/17/15.
 */
trait MessagesComposites {
  self:ShapePicklers=>


  implicit lazy val datePickler = Pickler.DatePickler
  implicit lazy val dateUnpickler = Unpickler.DateUnpickler

  implicit lazy val stringQueryMessage = CompositePickler[StringQueryMessages.StringMessage]
    .concreteType[StringQueryMessages.Ask].concreteType[StringQueryMessages.Construct]
    .concreteType[StringQueryMessages.Select].concreteType[StringQueryMessages.Update]


  implicit lazy val readQueryMessage = CompositePickler[Read.ReadMessage]
    .concreteType[Read.Bind].concreteType[Read.Construct].concreteType[Read.Query].
    concreteType[Read.Question].concreteType[Read.Search].concreteType[Read.Select]

  implicit lazy val allResPickler = Pickler.materializePickler[ShapeMessages.AllResourcesForShape]
  implicit lazy val allResUnpickler = Unpickler.materializeUnpickler[ShapeMessages.AllResourcesForShape]
  implicit lazy val getShapesPickler = Pickler.materializePickler[ShapeMessages.GetShapes]
  implicit lazy val getShapesUnpickler = Unpickler.materializeUnpickler[ShapeMessages.GetShapes]

  implicit lazy val shapeMessage = CompositePickler[ShapeMessages.ShapeMessage]
    .concreteType[ShapeMessages.AllResourcesForShape]
    .concreteType[ShapeMessages.AllShapesForResource]
    .concreteType[ShapeMessages.GetShapes]
    .concreteType[ShapeMessages.GetShEx]
    .concreteType[ShapeMessages.UpdateShape]
    .concreteType[ShapeMessages.UpdateShEx]



}
