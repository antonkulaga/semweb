package org.scalax.semweb

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
