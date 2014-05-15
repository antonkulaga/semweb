package org.scalax.semweb.sesame.shapes

import org.scalax.semweb.sesame._
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf._
import org.openrdf.model.{URI, Literal, Resource}
import org.openrdf.repository.RepositoryConnection
import org.scalax.semweb.shex._
import org.scalax.semweb.shex
import scala.util.Try
import org.scalax.semweb.rdf.vocabulary.{RDFS, WI, RDF}


/**
 * Ugly written
 * TODO: rewrite in future
 * Class that reads shapes from the database and does some other operations (like  loading props from shape)
 */
trait ShapeReader extends SesameReader{



  def loadShapes(shapes:IRI*)(implicit contexts:Seq[Resource] = List.empty[Resource]) = this.read{
    con=>shapes.map(sh=>getShape(sh,con)(contexts)).toList
  }

  def loadAllShapeIds(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[List[Res]] = this.read{
    con=>con.getStatements(null,RDF.TYPE,Shape.rdfType,true,contexts:_*).map{st=>   st.getSubject:Res   }.toList
  }

  def loadAllShapes(implicit contexts:Seq[Resource] = List.empty[Resource]) = this.read{
    con=>con.getStatements(null,RDF.TYPE, Shape.rdfType,true,contexts:_*).map{st=>   getShape(st.getSubject,con)(contexts)   }
  }

  def loadShape(iri:Res)(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[Shape] =this.read{con=>
    this.getShape(iri,con)(contexts)
  }

  /**
   * Functions to load properties by shape
   * WARNING: BUGGY!
   * @param sh
   * @param res
   * @param contexts
   * @return
   */
  def loadPropertiesByShape(sh:Shape,res:Resource)(implicit contexts:Seq[Resource] = List.empty[Resource]) = this.read{con=>
    sh.rule match {
      case and:AndRule=>
        val arcs = and.conjoints.collect{   case arc:ArcRule=>  arc }


      case r => lg.warn(s"or rule is not yet supported, passed rule is ${r.toString}")
    }


  }


  /**
   * Loads shapes that are associated with this type
   * @param tp RDF type
   * @param contexts named graphs
   * @return Seq of Shapes
   */
  def loadShapesForType(tp:URI)(implicit contexts:Seq[Resource] = List.empty) = this.read{implicit con=>
    con.resources(tp,WI.PLATFORM.HAS_SHAPE,contexts).map(getShape(_,con))

  }
//  def loadShapesFor(res:URI)(implicit contexts:Seq[Resource] = List.empty) = this.read{implicit con=>
//    for {
//      tp <-con.resources(res,RDF.TYPE:URI,contexts)
//      sh <- con.resources(tp,WI.PLATFORM.HAS_SHAPE)
//    } yield this.getShape(sh,con)(contexts)
//  }

  def getShape(shapeRes:Res,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]): Shape = {
    object shape extends ShapeBuilder(shapeRes)
    val props = con.resources(shapeRes:Resource,ArcRule.property:URI,contexts)
    props.foreach{
      case res: Resource =>
        val arco = getArc(res,con)(contexts)
        for(arc<-arco) shape.hasRule(arc)
    }
    shape.result
  }


  /**
   * Extracts Arc rule
   * @param id id of the arc rule
   * @param con connection
   * @param contexts context (optional)
   * @return
   */
  def getArc(id:Resource,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]):Option[ArcRule] = {
    val term: Option[URI] = con firstURI (id,NameTerm.property:URI,contexts)
    term.map{name=>
      val value = con firstURI(id,ValueType.property:URI,contexts)
      val occurs = this.getOccurs(id,con)(contexts)
      ArcRule(Some(id:Res),NameTerm(name),value.fold(ValueType(RDF.VALUE))(v => ValueType(v)),occurs)
    }
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

//
//  id: Option[Label],
//  name: NameClass,
//  value: ValueClass,
//  occurs: Cardinality,
//  actions: Seq[Action] = List.empty
}
