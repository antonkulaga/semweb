package org.scalax.semweb.sesame.shapes

import org.openrdf.model.{Literal, Resource, URI, Value}
import org.openrdf.repository.RepositoryConnection
import org.scalax.semweb.commons.LogLike
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
trait ShapeReader extends SesameReader {

  val extractor = new ShapeExtractor[ReadConnection](this.lg)

/*
  def loadShex(shex:Resource) = this.read{
    con=>
      con.getStatements(shex,)
  }
*/


  def loadShapes(shapes:Resource*)(implicit contexts:Seq[Resource] = List.empty[Resource]) = this.read
  {
    con=>shapes.map(sh=>extractor.getShape(sh,con)(contexts)).toList
  }

  def loadAllShapeIds(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[List[Res]] = this.read{
    con=>con.getStatements(null,RDF.TYPE,Shape.rdfType,true,contexts:_*).map{st=>   st.getSubject:Res   }.toList
  }

  def loadAllShapes(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[List[Shape]] = this.read{
    con=>
      val sts = con.getStatements(null,RDF.TYPE, Shape.rdfType,true,contexts:_*).toList
      sts.map{st=>   extractor.getShape(st.getSubject,con)(contexts)   }
  }

  def loadShape(resource:Res)(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[Shape] =this.read{con=>
    extractor.getShape(resource,con)(contexts)
  }


  def loadPropertyModelsByShape(sh:Shape,res:Set[Resource])(implicit contexts:Seq[Resource] = List.empty[Resource]):Try[Set[PropertyModel]] =
    this.read{ con=> for(r<-res) yield modelByShape(sh,r,con)(contexts)
  }

  def modelByShape(sh:Shape,res:Resource,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]): PropertyModel  =
  sh.arcSorted()
    .foldLeft[PropertyModel](PropertyModel.clean(res))
    {
      case (model: PropertyModel,arc)=>  extractor.modelWithArc(model,arc)(con,contexts)
    }
  /**
   * Functions to load properties by shape
   * WARNING: BUGGY!
   * @param sh
   * @param res
   * @param contexts
   * @return
   */
  def loadModelByShape(sh:Shape,res:Resource)(implicit contexts:Seq[Resource] = List.empty[Resource]): Try[PropertyModel] =
    this.read{con=>   modelByShape(sh,res,con)(contexts) }

  /**
   * Loads shapes that are associated with this type
   * @param tp RDF type
   * @param contexts named graphs
   * @return Seq of Shapes
   */
  def loadShapesForType(tp:URI)(implicit contexts:Seq[Resource] = List.empty) = this.read{implicit con=>
    con.resources(tp,WI.PLATFORM.HAS_SHAPE,contexts).map(extractor.getShape(_,con))

  }

}




