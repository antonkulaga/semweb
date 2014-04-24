package org.scalax.semweb

import scala.annotation.ClassfileAnnotation

package object rdf {

  /**
   * TEMPORAL FIX
   * @param value
   */
  implicit class LabelHolder(value:RDFValue) {

    def label = value match {
      case l:Lit=>l.label
      case other=>value.stringValue
    }
  }

}


