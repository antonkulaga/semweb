package org.denigma.semweb.sesame.shapes

import org.denigma.semweb.commons.Logged
import org.openrdf.model.{Value, Literal, Resource, URI}
import org.openrdf.repository.RepositoryConnection
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{RDFS, RDF}
import org.denigma.semweb.sesame._
import org.denigma.semweb.shex._
import org.denigma.semweb.shex.validation.{Valid, Failed, ValidationResult}
import org.denigma.semweb.sparql
import org.denigma.semweb.sparql._
import scala.util.Try



/**
 * Extracts arcs from shape
 */
trait ArcExtractor[ReadConnection<: RepositoryConnection] extends Logged {


  def arc2Shape(arc:ArcRule) = Shape(arc.id,AndRule(Set(arc),arc.id))


  
  /**
   * Extracts Arc rule
   * @param id id of the arc rule
   * @param con connection
   * @param contexts context (optional)
   * @return
   */
  def getArc(id:Resource,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]):Option[ArcRule] = {
    val nco =    extractNameClass(id,con)(contexts)
    nco.map{case  nameClass =>
      val occurs = this.getOccurs(id,con)(contexts)
      val valueClass = extractValueClass(id,con)(contexts)
      val priority: Option[Int] = con.objects(id,ArcRule.priority).collectFirst{
        case lit:Literal if Try(lit.intValue()).isSuccess=>
          lit.intValue()
      }
      val title: Option[String] = con.objects(id,vocabulary.DCElements.title).headOption.map(v=>v.label)
      ArcRule(Label.apply(id:Res),nameClass,valueClass,occurs,Seq.empty[Action],priority,title)
    }
  }

  protected def extractNameClass(id:Resource,con:ReadConnection)(implicit contexts:Seq[Resource]) =  con
    .firstURI(id,NameTerm.property:URI,contexts).map[NameClass](NameTerm(_))
    .orElse(con.firstURI(id,NameStem.property:URI,contexts).map(NameStem(_)))



  /**
   * Extracts value class for shape
   * @param id
   * @param con
   * @param contexts
   * @return
   */
  protected def extractValueClass(id:Resource,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]):ValueClass =
    if(con.hasObjectFor(id,ValueType.property,contexts))
    {
      con.firstURI(id,ValueType.property:URI,contexts).fold(ValueType(RDF.VALUE))(v => ValueType(v))
    }
    else
    if(con.hasObjectFor(id,ValueSet.property,contexts)){
      ValueSet(con.objects(id,ValueSet.property,contexts).map(v=>v:RDFValue).toSet)
    }
    else
    if(con.hasObjectFor(id,ValueReference.property,contexts)){ //todo:unsafe, rewrite!
      import org.denigma.semweb.sesame._
      ValueReference(con.resources(id,ValueReference.property,contexts).map(v=>v:Res).head)
    }
    else
    {
      if(con.hasObjectFor(id,ValueStem.property,contexts))  con.firstURI(id,ValueStem.property:URI,contexts) match {
        case Some(uri)=>ValueStem(uri)
        case None=>
          lg.info(s"cannot find stem for ${id.stringValue()}")
          ValueType(RDF.VALUE)
      } else ValueType(RDF.VALUE)
    }

  def getOccurs(id:Resource,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]):Cardinality = {
    if(con.hasStatement(id,ExactlyOne.property,ExactlyOne.obj,true,contexts:_*)) ExactlyOne else
    if(con.hasStatement(id,Plus.property,Plus.obj,true,contexts:_*)) Plus else
    if(con.hasStatement(id,Star.property,Star.obj,true,contexts:_*)) Star else
    if(con.hasStatement(id,Opt.property,Opt.obj,true,contexts:_*)) Opt else
    {
      val min = con.getStatements(id,Range.minProperty,null,true,contexts:_*)
        .collectFirst{
        case st if st.getObject.isInstanceOf[Literal]=>
          val lit = st.getObject.asInstanceOf[Literal]
          Try(lit.intValue()).getOrElse{ lg.error(s"cannot load literal for minOccurs"); 0}
      }

      val max= con.getStatements(id,Range.maxProperty,null,true,contexts:_*)
        .collectFirst{
        case st if st.getObject.isInstanceOf[Literal]=>
          val lit = st.getObject.asInstanceOf[Literal]
          Try(lit.intValue()).getOrElse{
            lg.error(s"cannot load literal for maxOccurs")
            Int.MaxValue
          }
      }
      Cardinality(min.getOrElse(Int.MinValue),max.getOrElse(Int.MaxValue))

    }

  }


  /**
   * Checks if there is violation with occurence
   * @param c cardinality values against the check should be done
   * @param prop property IRI
   * @param values values that were found
   * @return
   */
  def checkOccurrence(c:Cardinality,prop:IRI, values:Seq[Value]):ValidationResult= c match {

    case ExactlyOne=>if(values.size!=1) Failed(s"exactly-one rule",prop) else Valid
    case Plus=>if(values.size<1) Failed(s"one or more rule",prop) else Valid
    case Opt=>if(values.size>1) Failed(s"optional rule",prop) else Valid
    case Star=>Valid
    case Range(min,max)=>if(values.size<min || values.size>max) Failed(s"range rule",prop) else Valid
    case _=> Failed(s"uknown cardinality type",prop)
  }

}
