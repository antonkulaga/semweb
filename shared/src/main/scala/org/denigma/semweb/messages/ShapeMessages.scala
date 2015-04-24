package org.denigma.semweb.messages

import java.util.Date

import org.denigma.semweb.rdf.Res
import org.denigma.semweb.shex.{ShEx, Shape}

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
