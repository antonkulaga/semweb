package org.scalax.semweb.sesame.shapes

import org.openrdf.model.Statement
import org.openrdf.query.{QueryLanguage, TupleQuery}
import org.openrdf.repository.RepositoryConnection
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{WI, RDF, RDFS, DCElements}
import org.scalax.semweb.shex._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import org.scalax.semweb.sesame._

import scala.annotation.tailrec


class FieldRulesExtractor[con<:RepositoryConnection] extends FieldRulesQueryExtractor{
  
  def extractSubjectRules(sub:CanBeSubject,con:RepositoryConnection) = {
    val sq = this.subjectRuleQuery(sub)
    val q = con.prepareTupleQuery(QueryLanguage.SPARQL,sq.stringValue)
    val res = q.evaluate().toListMap
    
  }
  
  
}

trait FieldRulesQueryExtractor extends QueryExtractor
{
  val rule = ?("rule")
  val valueClass = ?("valueClass")
  val title = ?("title")
  val base = ?("base")
  val valueProperty = ?("vp")
  
  
  lazy val valueProperties: RDFElement = VALUES(valueProperty)(ValueType.property)(ValueSet.property)(ValueReference.property)(ValueStem.property)

  def subjectRuleQuery(sub:CanBeSubject) =   SELECT(
    rule,valueProperty,valueClass,title,base) WHERE (
      Pat(sub,SubjectRule.property,valueProperty),
      Pat(rule,RDF.TYPE,SubjectRule.clazz),
      Optional(Pat(rule,DCElements.title,title)),
      Optional(Pat(rule,WI.PLATFORM.BASE,base)),
      valueProperties
      )
  
  
  def contextRuleQuery(sub:CanBeSubject) =  SELECT(
    rule,valueProperty,valueClass,title,base) WHERE (
      Pat(sub,ContextRule.property,valueProperty),
      Pat(rule,RDF.TYPE,ContextRule.clazz),
      Optional(Pat(rule,DCElements.title,title)),
      valueProperties
      )
    
  


}
