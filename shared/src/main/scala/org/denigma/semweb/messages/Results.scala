package org.denigma.semweb.messages

import org.denigma.semweb.rdf.RDFValue
import org.denigma.semweb.shex.PropertyModel

object Results {
  
  trait ResultsMessage

  case class SelectResults(headers:List[String],rows:List[Map[String,RDFValue]], channel:String = Channeled.default) extends Channeled with ResultsMessage

  case class PropertyResults(models:List[PropertyModel], channel:String = Channeled.default) extends Channeled with ResultsMessage



}