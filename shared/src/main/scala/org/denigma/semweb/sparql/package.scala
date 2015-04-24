package org.denigma.semweb


/**
 * Sparql package object
 */
package object sparql {
  def ?(name:String): Variable = Variable(name)


  implicit class AggInSelect(a:Aggregate) extends SelectElement {
    override def isAgg = true

    override def stringValue: String = a.stringValue
  }

}
