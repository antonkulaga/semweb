package org.scalax.semweb.sesame.shapes

import org.openrdf.model.{Literal, Resource, URI}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.RDF
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex._

import scala.util.Try

/**
 * Extracts arcs from shape
 */
trait ArcExtractor {

  self:SesameReader=>

  /**
   * Extracts Arc rule
   * @param id id of the arc rule
   * @param con connection
   * @param contexts context (optional)
   * @return
   */
  def getArc(id:Resource,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]):Option[ArcRule] = {
    val nco =    extractNameClass(id,con)(contexts)
    nco.map{ nameClass =>
      val occurs = this.getOccurs(id,con)(contexts)
      val valueClass = extractValueClass(id,con)(contexts)
      ArcRule(Some(id:Res),nameClass,valueClass,occurs)
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
      val min: Option[Long] = con.getStatements(id,Range.minProperty,null,true,contexts:_*)
        .collectFirst{
        case st if st.getObject.isInstanceOf[Literal]=>
          val lit = st.getObject.asInstanceOf[Literal]
          Try(lit.longValue()).getOrElse{ lg.error(s"cannot load literal for minOccurs"); 0:Long}
      }

      val max: Option[Long] = con.getStatements(id,Range.maxProperty,null,true,contexts:_*)
        .collectFirst{
        case st if st.getObject.isInstanceOf[Literal]=>
          val lit = st.getObject.asInstanceOf[Literal]
          Try(lit.longValue()).getOrElse{ lg.error(s"cannot load literal for maxOccurs"); Long.MaxValue}
      }
      Cardinality(min.getOrElse(Long.MinValue),max.getOrElse(Long.MaxValue))

    }

  }

}
