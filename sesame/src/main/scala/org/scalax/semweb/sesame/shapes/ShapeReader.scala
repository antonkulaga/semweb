package org.scalax.semweb.sesame.shapes

import org.openrdf.model.{Literal, Resource, URI, Value}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{XSD, RDF, WI}
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex._
import org.scalax.semweb.shex.validation.{Failed, Valid, ValidationResult}
import org.openrdf.model.vocabulary

import scala.util.Try



/**
 * Ugly written
 * TODO: rewrite in future
 * Class that reads shapes from the database and does some other operations (like  loading props from shape)
 */
trait ShapeReader extends SesameReader with ArcExtractor{



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

      //    case Plus=> if(prop._2.size<1) throw new Exception(s"one-or-many rule is broken for ${prop._1.toString}") else prop
//    case Opt=> if(prop._2.size>1) throw new Exception(s"zero-or-one rule is broken for ${prop._1.toString}") else prop
//    case Star=> prop
//    case Range(min,max)=> if(prop._2.size<min || prop._2.size>max) throw new Exception(s"cardinality rule is broken for ${prop._1.toString}") else prop
//    case _=>throw new Exception("uknown cardinality type")
//
 }


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

        val result: PropertyModel = arcs.foldLeft[PropertyModel](PropertyModel.clean(res)){ case (model: PropertyModel,arc)=> this.modelByArc(model,arc)(con,contexts)}
        result

      case r =>
        lg.warn(s"or rule is not yet supported, passed rule is ${r.toString}")
        ???
    }


  }

  protected def modelByArc(model:PropertyModel,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]) = {
    arc.name match {
      case NameTerm(prop)=>
        val (pr:IRI, values:Seq[Value]) = this.propertyByArc(model.resource,prop,arc)(con,contexts)
        val v = this.checkOccurrence(arc.occurs,pr,values)
        val vals: Set[RDFValue] = values.map(v=>v:RDFValue).toSet
        model.copy(properties = model.properties + (pr -> vals) ,validation =  model.validation.and(v))
      case NameStem(stem)=>
        lg.error("Name stems property extraction not implemented")
        ??? //TODO complete
    }
  }

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

    case _ =>
      lg.error("another unknown ARC case")
      ???

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


}


