package org.denigma.semweb.messages

import org.denigma.semweb.commons.QueryLike

/*
read only messages
 */
object Read {
  
  trait ReadMessage

  case class Query(query:String,offset:Int = 0, limit:Int = Int.MaxValue,rewrite:Boolean = false) extends Paginated with ReadMessage

  case class Select(query:String,offset:Int = 0, limit:Int = Int.MaxValue,rewrite:Boolean = false) extends Paginated with ReadMessage

  case class Bind(query:String,binds:Map[String,String],offset:Int = 0, limit:Int = Int.MaxValue) extends Paginated with ReadMessage

  case class Search(query:String,searches:Map[String,String],binds:Map[String,String] = Map.empty,offset:Int = 0, limit:Int = Int.MaxValue) extends Paginated with ReadMessage

  case class Question(query:String) extends QueryLike with ReadMessage

  case class Construct(query:String) extends QueryLike with ReadMessage





}


trait Paginated extends QueryLike{
  def offset:Int
  def limit:Int
  def isPaginated = offset>0 || (limit != Int.MaxValue && limit>0)

}
