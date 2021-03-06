package org.denigma.semweb.sesame

import org.openrdf.model.{Resource, Statement, URI, Value}
import org.openrdf.query.{BindingSet, GraphQueryResult, TupleQueryResult}
import org.openrdf.repository.RepositoryResult
import org.denigma.semweb.messages.Results
import org.denigma.semweb.rdf.RDFValue
import org.denigma.semweb.sparql.Variable

import scala.collection.JavaConversions._
import scala.collection.immutable._

trait ResultsImplicits extends Sesame2ScalaModelImplicits {
  /**
   * Implicit class that turns  query result into iterator (so methods toList, map and so on can be applied to it)
   * @param results results in sesame format
   */
  implicit class TupleResult(results: TupleQueryResult)  extends Iterator[BindingSet]
  {

    def extract(prop:String*): scala.Seq[(String, Value)] = this.flatMap
      {   
        case v=> 
          v.collect{
            case p if prop.contains(p.getName)=>
              p.getName->p.getValue
          }  
      }.toSeq

    def extractVars(vars:Variable*): scala.Seq[(String,Value)]  = this.extract(vars.map(v=>v.name):_*)


    /**
     * Extracts values of vars
     * TODO: decide if I should get rid of it
     * @param prop
     * @return
     */
    def extractValues(prop:String*): scala.Seq[Value] = this.flatMap{   
      case v=> 
        v.collect{
          case p if prop.contains(p.getName)=>p.getValue}  
    }.toSeq

    def extractVarValues(vars:Variable*): scala.Seq[Value]  = this.extractValues(vars.map(v=>v.name):_*)

    lazy val vars: List[String] = results.getBindingNames.toList

    def binding2Map(b:BindingSet): Map[String, Value] = b.iterator().map(v=>v.getName->v.getValue).toMap

    lazy val toListMap: List[Map[String, Value]] = this.map(v=>binding2Map(v)).toList

    lazy val toSelectResults = Results.SelectResults(
      results.getBindingNames.toList,
      results.toList.map{case b=>   b.iterator().map(v=>(v.getName,v.getValue:RDFValue)).toMap }
    )

    override def next(): BindingSet = results.next()

    override def hasNext: Boolean = results.hasNext
  }

  /*
implicit class for Repository results that adds some nice features there and turnes it into Scala Iterator
*/
  implicit class StatementsResult(results:RepositoryResult[Statement]) extends Iterator[Statement]{

    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext

    def toQuadList = results.map(Statement2Quad).toList

    def objects = results.map(st=>st.getObject).toList

    def resources = results.collect{case st if st.getObject.isInstanceOf[Resource]=>st.getObject.asInstanceOf[Resource]}.toList

    def uris = results.collect{case st if st.getObject.isInstanceOf[URI]=>st.getObject.asInstanceOf[URI]}.toList
    
    def toSortedList = {
      this.toList.sortWith{ (a,b)=> if(a.getSubject==b.getSubject){
        if(a.getPredicate==b.getPredicate){
          if(a.getObject==b.getObject) false else a.getObject.stringValue()<b.getObject.stringValue()
        } else a.getPredicate.stringValue()<b.getPredicate.stringValue()
      } else a.getSubject.stringValue()<b.getSubject.stringValue()
      }
      
    }

  }

  implicit class GraphResult(results:GraphQueryResult) extends Iterator[Statement]
  {
    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }



}

