package org.denigma.schemas.common

import org.denigma.semweb.rdf.{IRI, Res}
import org.denigma.semweb.shex.{PropertyModel, Shape}

/**
 * Items trait for testing
 */
trait Items {

   var properties = List.empty[IRI]


   var items = Map.empty[Res,List[PropertyModel]]

   var shapes:Map[Res,Shape] = Map.empty

   def addShape(shape:Shape) = {
     val res: Res = shape.id.asResource
     items = items + (res -> List.empty)
     shapes  = shapes + (res->shape)
   }


 }
