package org.scalax.semweb.messages

import org.scalax.semweb.commons.QueryLike

/*
read only messages
 */
object Read {

  case class Query(query:String,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated

  case class Select(query:String,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated

  case class Bind(query:String,binds:Map[String,String],offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Search(query:String,searches:Map[String,String],binds:Map[String,String] = Map.empty,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Question(query:String) extends QueryLike

  case class Construct(query:String) extends QueryLike





}


trait Paginated extends QueryLike{
  def offset:Long
  def limit:Long
  def isPaginated = offset>0 || (limit != Long.MaxValue && limit>0)

}
