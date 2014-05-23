package org.scalax.semweb.sesame.shapes

import org.scalax.semweb.sesame._
import org.scalax.semweb.rdf._
import org.openrdf.model.{Value, URI, Literal, Resource}
import org.scalax.semweb.shex._
import scala.util.{Success, Try}
import org.scalax.semweb.rdf.vocabulary.{WI, RDF}
import org.scalax.semweb.shex.validation.{ValidationResult, Valid, Failed}

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
   * Checks if there is violation with occurence
   * @param c
   * @param prop
   * @param values
   * @return
   */
  def checkOccurrence(c:Cardinality,prop:IRI, values:Seq[Value]):ValidationResult= c match {

    case ExactlyOne=>if(values.size!=1) Failed(s"exactly-one rule",prop) else Valid
    case Plus=>if(values.size<1) Failed(s"one or more rule",prop) else Valid
    case Opt=>if(values.size>1) Failed(s"optional rule",prop) else Valid
    case Star=>Valid
    case Range(min,max)=>if(values.size<min || values.size>max) Failed(s"range rule",prop) else Valid
    case _=> Failed(s"uknown cardinality type",prop)

      //    case Plus=> if(prop._2.size<1) throw new Exception(s"one-or-many rule is broken for ${prop._1.toString}") else prop
//    case Opt=> if(prop._2.size>1) throw new Exception(s"zero-or-one rule is broken for ${prop._1.toString}") else prop
//    case Star=> prop
//    case Range(min,max)=> if(prop._2.size<min || prop._2.size>max) throw new Exception(s"cardinality rule is broken for ${prop._1.toString}") else prop
//    case _=>throw new Exception("uknown cardinality type")
//
 }

  def propertyByArc(res:Res,p:IRI,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]): (IRI, Seq[Value]) =  arc.value match {
      case ValueSet(s)=>
        p -> con.objects(res, p,contexts).filter(o => s.contains(o: RDFValue))

      case ValueType(i)=>
      if(i.stringValue.contains(vocabulary.XSD.namespace)) {
        lg.warn("XSD IS NOT YET CHECKED")
        p ->con.objects(res,p,contexts)

      }
      else {
      p -> con.resources(res,p,contexts).filter(o=>con.hasStatement(o,RDF.TYPE,i,true,contexts:_*))
      }

      case _ => ???

  }


//  def propertyByArc(res:Res,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]): (IRI, Seq[Value]) = arc.name match  {
//
//    case NameTerm(p)=>
//     this.propertyByArc(res,p,arc)
//
//    case _ =>
//      lg.error("names other then nameterm are not implemented in arc")
//      ???
//
//  }

  /**
   * Functions to load properties by shape
   * WARNING: BUGGY!
   * @param sh
   * @param res
   * @param contexts
   * @return
   */
  def loadPropertiesByShape(sh:Shape,res:Resource)(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[PropertyModel] = this.read{con=>
    sh.rule match {
      case and:AndRule=>
        val arcs = and.conjoints.collect{   case arc:ArcRule=>  arc }

        val result = arcs.foldLeft[PropertyModel](PropertyModel.empty){ case (model,arc)=>
          arc.name match {
            case NameTerm(prop)=>
              val (pr:IRI, values:Seq[Value]) = this.propertyByArc(res,prop,arc)(con,contexts)
              val v = this.checkOccurrence(arc.occurs,pr,values)
              val vals: Set[RDFValue] = values.map(v=>v:RDFValue).toSet
              model.copy(properties = model.properties + (pr -> vals) , model.validation.and(v))

            case _=>model
          }
        }
        result




      case r =>
        lg.warn(s"or rule is not yet supported, passed rule is ${r.toString}")
        ???
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
