package org.denigma.semweb.sesame

import org.denigma.semweb.commons.Logged

import scala.util.Try
import org.openrdf.repository.RepositoryConnection
import org.denigma.semweb.rdf.vocabulary.WI
import org.openrdf.query._
import org.denigma.semweb.rdf.IRI


trait CanReadSesame  extends Logged{

  /**
   * Reader connection type
   */
  type ReadConnection <: RepositoryConnection
  def readConnection: ReadConnection
}



trait SesameReader extends CanReadSesame{

  /**
   * Reading function type alias
   * TODO: rename
   * @tparam T
   */
  type Reading[T] = ReadConnection=>T

  /*
  * Provides readConnection
  * */
  def readConnection: ReadConnection

  def read[T](action:Reading[T]):Try[T]= {
    val con: ReadConnection= this.readConnection
    val res = Try {
      action(con)
    }

    con.close()
    res.recoverWith{case
      e=>
      lg.error("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }

}

/*
sends closures that deal with read requests
 */
trait SelectReader extends CanReadSesame
{

  type SelectQuery <: TupleQuery

  type SelectHandler[T] = (String,ReadConnection,SelectQuery)=>T

  def makeSelectQuery(con:ReadConnection,query:String)(implicit base:String = WI.RESOURCE):SelectQuery //con.prepareTupleQuery(QueryLanguage.SPARQL,str,base)

  /*
  readonly select query failed
   */
  def selectQuery[T](query:String)(select:SelectHandler[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con = this.readConnection
    val res = Try{
      select(query,con,this.makeSelectQuery(con,query))
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly SELECT query\n $query \nfailed because of \n"+e.getMessage)
      res
    }

  }

  def justSelect(select:String): Try[TupleQueryResult] = this.selectQuery(select){ (str,con,q)=>q.evaluate() }
}

/*
sends closure that deal with construct requests
 */
trait ConstructReader extends CanReadSesame {

  type ConstructQuery <:GraphQuery //TODO refactor

  type ConstructHandler[T] = (String,ReadConnection,ConstructQuery)=>T

  def makeConstructQuery(con:ReadConnection,query:String)(implicit base:String = WI.RESOURCE):ConstructQuery //con.prepareTupleQuery(QueryLanguage.SPARQL,str,base)


  def justConstruct(query:String) = this.graphQuery(query){ (str,con,q)=>q.evaluate() }

  def graphQuery[T](query:String)(selectGraph:ConstructHandler[T])(implicit base:String= WI.RESOURCE): Try[T] = {
    val con= this.readConnection
    val res = Try{
      selectGraph(query,con,this.makeConstructQuery(con,query))
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly GRAPH query\n $query \nfailed because of \n"+e.getMessage)
      res
    }

  }
}



trait AskReader extends CanReadSesame
{

  type AskQuery<:BooleanQuery

  type AskHandler[T] = (String,ReadConnection,AskQuery)=>T

  def makeAskQuery(con:ReadConnection,query:String)(implicit base:String = WI.RESOURCE):AskQuery //con.prepareTupleQuery(QueryLanguage.SPARQL,str,base)

  def justAsk(query:String): Try[Boolean] = this.askQuery(query){ (str,con,q)=>q.evaluate() }
  /**
   * Ask
   * @param query query string
   * @param ask handler function that receives connection, query string and query and returns a result
   * @param base basic string (required by bigdata database)
   * @tparam T return type
   * @return result of ask function
   */
  def askQuery[T](query:String)(ask:AskHandler[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con = this.readConnection
    val res = Try{
      ask(query,con,this.makeAskQuery(con,query))
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly ASK query\n $query \nfailed because of \n"+e.getMessage)
      res
    }

  }
}