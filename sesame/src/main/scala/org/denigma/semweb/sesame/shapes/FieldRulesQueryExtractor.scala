package org.denigma.semweb.sesame.shapes

import org.openrdf.model.{URI, Statement}
import org.openrdf.query.{QueryLanguage, TupleQuery}
import org.openrdf.repository.RepositoryConnection
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{WI, RDF, RDFS, DCElements}
import org.denigma.semweb.shex._
import org.denigma.semweb.sparql
import org.denigma.semweb.sparql._
import org.denigma.semweb.sesame._

import scala.annotation.tailrec


class FieldRulesExtractor[Con<:RepositoryConnection] extends FieldRulesQueryExtractor
{
  protected val valueTypes = Set(ValueStem.property,ValueSet.property,ValueType.property)
  protected val ruleTypes = Set(SubjectRule.property.stringValue,ContextRule.property.stringValue)

  def firstIRI(trips:Set[Statement]) =  trips.collectFirst{case st if st.getObject.isInstanceOf[URI]=> st.getObject.asInstanceOf[URI]:IRI}.get

  protected def valueClassFromStatements(props:Map[IRI,Set[Statement]]) = {
    props.collectFirst{
      case (p,sts) if valueTypes.contains(p)=> p match {
        case ValueStem.property => ValueStem(firstIRI(sts))
        case ValueSet.property=> ValueSet(sts.map(s => s.getObject: RDFValue))
        case ValueType.property => ValueType(firstIRI(sts))
      }
    }.getOrElse{
      println(s"CANNOT FIND valuetype from ${props.toString}")
      ValueType(RDF.VALUE)
    }
  }
  
  def subjectRuleFromStatements(sub:Res,props:Map[IRI,Set[Statement]]):SubjectRule = {
    val vc = this.valueClassFromStatements(props)
    SubjectRule(sub,
      value =vc,
      title =props.collectFirst{case (p,sts) if p==DCElements.title=>sts.head.getObject.label},
      base =props.collectFirst{case (p,sts) if p==WI.PLATFORM.BASE=>sts.head.getObject.label}
    )
    
  }

  def contextRuleFromStatements(sub:Res,props:Map[IRI,Set[Statement]]):ContextRule = {
    val vc = this.valueClassFromStatements(props)
    ContextRule(sub,vc,props.collectFirst{case (p,sts) if p==DCElements.title=>sts.head.getObject.label})

  }
  def extractFieldRules(sub:Res,con:Con) = {
    val q = fieldRulesQuery(sub)
    val query = con.prepareGraphQuery(QueryLanguage.SPARQL,q.stringValue)
    val groups: Map[Res, Set[Statement]] = query.evaluate().toSet.groupBy(st=>st.getSubject:Res)
    this.fieldRulesFromStatements(sub,groups)
  }
  
  def fieldRulesFromStatements(sub:Res,groups: Map[Res, Set[Statement]]):Set[FieldRule] =   groups.get(sub) match {
      case Some(sts)=> 
       // println(s"SUBJECT HAS BEEN FOUND! ${sub.stringValue}\n")
       val rules: Set[FieldRule] =  sts.collect{

          case st if st.getPredicate.stringValue()==SubjectRule.property.stringValue=>


            groups.collect{  case (s,stats) if s.stringValue==st.getObject.stringValue() =>
              this.subjectRuleFromStatements(s,stats.groupBy(s=>s.getPredicate:IRI))
            }

          case ct if ct.getPredicate.stringValue()==ContextRule.property.stringValue=>

            groups.collect{  case (s,stats) if s.stringValue==ct.getObject.stringValue() => this.contextRuleFromStatements(s,stats.groupBy(s=>s.getPredicate:IRI))
            }

      }.flatten
      rules
        
      case None=> Set.empty[FieldRule]
    }
    
  
  
  
}

trait FieldRulesQueryExtractor extends QueryExtractor
{
  val rp = ?("ruleProp")
  val rule = ?("rule")
  val valueProperty = ?("vp")
  val valueObject = ?("vo")
  val title = ?("title")
  val base = ?("base")

  def fieldRulesQuery(sub:CanBeSubject) = {
    val rulesPat =  Pat(sub,rp,rule)
    val titlePat = Pat(rule,DCElements.title,title)
    val basePat = Pat(rule,WI.PLATFORM.BASE,base)
    val valuesPat =   Pat(rule,valueProperty,valueObject)
    CONSTRUCT(
      rulesPat,
      titlePat,
      basePat,
      valuesPat
    ) WHERE (
      rulesPat,
      Optional(valuesPat,    valueProperties
      ),
      VALUES(rp)(SubjectRule.property)(ContextRule.property),
      Optional(titlePat),
      Optional(basePat)
      )

    
  }
  lazy val valueProperties: RDFElement = VALUES(valueProperty)(ValueType.property)(ValueSet.property)(ValueReference.property)(ValueStem.property)

  /*  val rule = ?("rule")
  //  val valueClass = ?("valueClass")
    val title = ?("title")
    val base = ?("base")
    val valueProperty = ?("vp")
    
    
    lazy val valueProperties: RDFElement = VALUES(valueProperty)(ValueType.property)(ValueSet.property)(ValueReference.property)(ValueStem.property)
  
    def subjectRuleQuery(sub:CanBeSubject) =   SELECT(
      rule,valueProperty,title,base) WHERE (
        Pat(sub,SubjectRule.property,rule),
        Pat(rule,RDF.TYPE,SubjectRule.clazz),
        Pat(rule,valueProperty,)
        Optional(Pat(rule,DCElements.title,title)),
        Optional(Pat(rule,WI.PLATFORM.BASE,base)),
        valueProperties
        )
    
    
    def contextRuleQuery(sub:CanBeSubject) =  SELECT(
      rule,valueProperty,title,base) WHERE (
        Pat(sub,ContextRule.property,valueProperty),
        Pat(rule,RDF.TYPE,ContextRule.clazz),
        Optional(Pat(rule,DCElements.title,title)),
        valueProperties
        )
      
    */


}
