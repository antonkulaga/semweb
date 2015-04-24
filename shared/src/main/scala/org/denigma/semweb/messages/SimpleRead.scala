package org.denigma.semweb.messages

import java.util.Date
import org.denigma.semweb.commons.QueryLike
import org.denigma.semweb.rdf.RDFValue
import org.denigma.semweb.shex.PropertyModel

/*
TODO: REWRITE IN A BETTER WAY
*/
object SimpleRead {

  trait SimpleQuery extends QueryLike


  case class Select(query:String,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with SimpleQuery

  case class Bind(query:String,binds:Map[String,String],offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated  with SimpleQuery

  case class Search(query:String,searches:Map[String,String],binds:Map[String,String] = Map.empty,offset:Long = 0, limit:Long = Long.MaxValue,params:Map[String,String]) extends Paginated with SimpleQuery

  case class Question(query:String) extends QueryLike with SimpleQuery

  case class Construct(query:String) extends QueryLike with SimpleQuery


}



/**
 * Contains messages with Query
 */
object StringQueryMessages{
  
  trait StringMessage

  case class Select(query:String, id:String,  channel:String = Channeled.default,time:Date = new Date()) extends StorageMessage with StringMessage

  case class Ask(query:String,  id:String,  channel:String = Channeled.default,time:Date = new Date()) extends StorageMessage with StringMessage

  case class Construct(query:String,  id:String,  channel:String = Channeled.default,time:Date = new Date()) extends StorageMessage with StringMessage

  case class Update(query:String,  id:String,  channel:String = Channeled.default,time:Date = new Date()) extends StorageMessage with StringMessage


}