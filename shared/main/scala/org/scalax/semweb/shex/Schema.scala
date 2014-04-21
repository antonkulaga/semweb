package org.scalax.semweb.shex

import org.scalax.semweb.shex.parser.PrefixMap
import org.scalax.semweb.shex.ShapeSyntax._

/**
 * The following definitions follow: http://www.w3.org/2013/ShEx/Definition
 * 
 */

case class Schema(pm: PrefixMap, rules: Seq[Shape]) {

//  override def toString: String = {
//    val sd = ShapeDoc(pm)
//    sd.schema2String(this)
//  }


}

object Schema {
  
 // def matchShape(schema: Schema, graph: RDFGraph) : ShExResult = ???
}
