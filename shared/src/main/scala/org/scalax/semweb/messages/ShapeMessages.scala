package org.scalax.semweb.messages

import java.util.Date

import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{Res, IRI}
import org.scalax.semweb.shex.{ShEx, Shape}

/**
 * Protocol for Shape related messaged
 */
object ShapeMessages {

  trait ShapeMessage extends Channeled

  case class GetShapes(shapeIds:Seq[Res],channel:String = Channeled.default) extends ShapeMessage

  case class GetShEx(shex:Res,channel:String = Channeled.default) extends ShapeMessage

  case class AllResourcesForShape(shapeId:Res,channel:String = Channeled.default) extends ShapeMessage

  case class AllShapesForResource(res:Res,channel:String = Channeled.default) extends ShapeMessage

  case class UpdateShape(shape:Shape,rewrite:Boolean = true, channel:String = Channeled.default) extends ShapeMessage

  case class UpdateShEx(shape:ShEx,rewrite:Boolean = true, channel:String = Channeled.default) extends ShapeMessage

  case class SuggestProperty(typed:String, id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ShapeMessage



}
