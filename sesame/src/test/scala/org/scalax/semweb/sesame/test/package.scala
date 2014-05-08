package org.scalax.semweb.sesame

import com.bigdata.rdf.sail.{BigdataSailRepositoryConnection, BigdataSailTupleQuery}
import org.openrdf.model.{Statement, Value}
import org.openrdf.query.{BindingSet, TupleQueryResult}
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable._
import scala.collection.JavaConversions._ //converts Java collection into scala's


/**
 * Package object
 */
package object test {
  /**
   * Type alias for BigDataTupleQuery
   */
  type SelectQuery = BigdataSailTupleQuery

  type SelectHandler[T] = (String,BigdataSailRepositoryConnection,SelectQuery)=>T

  type BigDataConnection  = BigdataSailRepositoryConnection

  type Reading[T] = BigDataConnection=>T

  /**
   * turns RepositoryResult into scala iterator and adds some other sueful methods
   * @param results
   */
  implicit class TupleResult(results: TupleQueryResult)  extends Iterator[BindingSet]
  {

    lazy val vars:List[String] = results.getBindingNames.toList

    def binding2Map(b:BindingSet): Map[String, Value] = b.iterator().map(v=>v.getName->v.getValue).toMap

    lazy val toListMap: List[Map[String, Value]] = this.map(v=>binding2Map(v)).toList


    override def next(): BindingSet = results.next()

    override def hasNext: Boolean = results.hasNext
  }

  /**
   * turns RepositoryResult into scala iterator
   * @param results
   */
  implicit class StatementsResult(results:RepositoryResult[Statement]) extends Iterator[Statement]{

    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }

}
