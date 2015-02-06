package org.scalax.semweb.messages

import org.scalax.semweb.rdf.RDFValue
import org.scalax.semweb.shex.PropertyModel

object Results {
  
  trait ResultsMessage

  case class SelectResults(headers:List[String],rows:List[Map[String,RDFValue]], channel:String = Channeled.default) extends Channeled with ResultsMessage

  case class PropertyResults(models:List[PropertyModel], channel:String = Channeled.default) extends Channeled with ResultsMessage



}