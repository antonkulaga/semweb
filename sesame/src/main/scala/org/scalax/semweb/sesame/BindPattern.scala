package org.scalax.semweb.sesame

import org.openrdf.model._
import org.scalax.semweb.rdf.PatternElement
import org.scalax.semweb.sparql._

object BindPattern {

  def canBind(p:QuadPattern,st:Statement) = (!p.hasContext || canBindValue(p.cont,st.getContext)) && canBindTriplet(p,st)

  def canBindTriplet(p:TripletPattern,st:Statement): Boolean = canBindValue(p.sub,st.getSubject) &&  canBindValue(p.pred,st.getPredicate) &&  canBindValue(p.obj,st.getObject)


  def canBindValue(p:PatternElement,value:Value) = p match {
    case v:Variable => true
    case null=> true
    case _ if value==null=>false
    case v:Value=> v.stringValue()==value.stringValue()
  }


}
