package org.denigma.semweb

import scala.annotation.ClassfileAnnotation

package object rdf {

  implicit def string2Literal(str:String):StringLiteral = StringLiteral(str)
  implicit def literal2String(lit:StringLiteral): String = lit.text


  /**
   * TEMPORAL FIX TODO: change
   * @param value
   */
  implicit class LabelHolder(value:RDFValue) {

    def label = value match {
      case l:Lit=>l.label
      case other=>value.stringValue
    }
  }

}


