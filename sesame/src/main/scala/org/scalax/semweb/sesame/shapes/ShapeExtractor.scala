package org.scalax.semweb.sesame.shapes
import org.openrdf.model._
import org.openrdf.model.vocabulary
import org.openrdf.repository.RepositoryConnection
import org.scalax.semweb.commons.{LogLike, Logged}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.RDF
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex._
import org.openrdf.repository.RepositoryConnection
import org.scalax.semweb.commons.LogLike
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{XSD, RDF, WI}
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex._
import org.scalax.semweb.shex.validation.{Failed, Valid, ValidationResult}

import scala.util.Try

/**
 * Extracts shape having the connection
 * @param lg
 * @tparam ReadConnection
 */
class ShapeExtractor[ReadConnection<: RepositoryConnection](val lg:LogLike) extends ArcExtractor[ReadConnection]
{
  def getShape(shapeRes:Res,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]): Shape = {
    object shape extends ShapeBuilder(shapeRes)
    val props: Seq[Resource] = con.resources(shapeRes:Resource,ArcRule.property:URI,contexts)

    props.foreach{   case res: Resource =>   getArc(res,con)(contexts).foreach(arc=>shape.hasRule(arc))  }
    val sh = shape.result
    sh
  }

/*  def getShex(shexRes:Res,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]):ShEx = {

    val shapes: Seq[Shape] = con.resources(shexRes,ShEx.hasShape,contexts).toSeq.map(r=>getShape(r,con)(contexts))
    val start = con.resources(shexRes,ShEx.start,contexts).headOption.map{
      case iri:URI=> IRILabel(URI2IRI(iri))
      case bnode:BNode=>BNodeLabel(bnode:BlankNode)
    }
    val title = con.resources(shexRes,ShEx.startTitle,contexts).headOption
    ShEx(shapes,start,title)

  }*/

  /**
   * Loads a property by arc rule
   * @param res
   * @param p
   * @param arc
   * @param con
   * @param contexts
   * @return
   */
  def propertyByArc(res:Res,p:IRI,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]): (IRI, Seq[Value]) =  arc.value match {
    case ValueSet(s)=>
      p -> con.objects(res, p,contexts).filter(o => s.contains(o: RDFValue))

    case ValueType(i)=> i match {
      case RDF.VALUE=> p -> con.objects(res,p,contexts)
      case XSD.StringDatatypeIRI=>

        p->con.objects(res,p,contexts).filter
        {
          case l:Literal=>
            l.getDatatype match {
              case null=>true
              case vocabulary.XMLSchema.STRING=>true
              case vocabulary.XMLSchema.NORMALIZEDSTRING=>true
              case vocabulary.XMLSchema.LANGUAGE=>true
              case vocabulary.XMLSchema.NAME=>true
              case _=>false
            }
          case other=>false
        }
      case x if x.stringValue.contains(XSD.namespace)=> p-> con.objects(res,p,contexts)  //TODO: check XSDs

      case tp=>      p -> con.resources(res,p,contexts).filter(o=>con.hasStatement(o,RDF.TYPE,tp,true,contexts:_*))
    }

    case ValueStem(stem)=>p -> con.uris(res,p,contexts).filter(obj=>stem.matches(obj))

    case _ =>
      lg.error("another unknown ARC case")
      ???

  }

  /**
   * Loads property model by ArcValue
   * @param model
   * @param arc
   * @param con
   * @param contexts
   * @return
   */
  def modelByArc(model:PropertyModel,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]) = {
    arc.name match {
      case NameTerm(prop)=>
        val (pr:IRI, values:Seq[Value]) = this.propertyByArc(model.resource,prop,arc)(con,contexts)
        val checks: ValidationResult = this.checkOccurrence(arc.occurs,pr,values)
        val vals: Set[RDFValue] = values.map(v=>v:RDFValue).toSet
        model.copy(properties = model.properties + (pr -> vals) ,validation =  model.validation.and(checks))
      case NameStem(stem)=>
        lg.error("Name stems property extraction not implemented")
        ??? //TODO complete
      case _ =>
        lg.error("other nameterms are also not implemented")
        ??? //TODO complete

    }
  }



}
