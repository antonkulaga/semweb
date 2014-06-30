package org.scalax.semweb.messages

import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{Res, IRI}
import org.scalax.semweb.shex.Shape

/**
 * Protocol for Shape related messaged
 */
object ShapeMessages {

  trait ShapeMessage extends Channeled

  case class GetShapes(shapeIds:List[Res],channel:String = Channeled.default) extends ShapeMessage

  case class AllResourcesForShape[T](shapeId:Res,channel:String = Channeled.default) extends ShapeMessage

  case class AllShapesForResource[T](res:Res,channel:String = Channeled.default) extends ShapeMessage

  case class UpdateShape(shape:Shape,rewrite:Boolean = true, channel:String = Channeled.default)


}
