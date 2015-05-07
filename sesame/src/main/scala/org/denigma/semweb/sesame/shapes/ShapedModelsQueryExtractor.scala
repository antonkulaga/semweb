package org.denigma.semweb.sesame.shapes

import org.denigma.semweb.rdf.RDFElement
import org.denigma.semweb.rdf.vocabulary.WI
import org.denigma.semweb.shex.Shape
import org.denigma.semweb.sparql._

class ShapedModelsQueryExtractor extends ArcPropertiesQueryExtractor
{


  /**
   * Finds all values that match shapes
   * NOTE: Occurs have not taken into account yet
   * @param shape
   * @return
   */
  def validShapeQuery(shape:Shape):ConstructQuery = shape.arcSorted match
  {
    case no if no.isEmpty=> CONSTRUCT.empty
    case arcs=>

      val sub = ?("sub")

      val vars = arcs.map{case arc=> 
        val cap = arcCaption(arc)
        arc -> (?("p_"+cap),?("o_"+cap))
      }
      
      val pats = vars.map{
        case (arc,(p,o))=>Pat(sub,p,o)
      }
      val conds: Seq[RDFElement] = vars.foldLeft(Seq.empty[RDFElement]) {
        case (acc, (arc,(p,o))) =>
          val ps: Seq[RDFElement] = this.arcValuePatterns(arc,sub,o)++arcNamePatterns(arc,sub,p,o)
          acc++this.withOccurence(arc,ps)
      }
      
      CONSTRUCT(pats:_*).WHERE(conds:_*)
  }

  

  def invalidShapeQuery(shape:Shape,draftsOnly:Boolean = true):ConstructQuery = shape.arcSorted match
  {
    case no if no.isEmpty=>CONSTRUCT.empty
    case arcs=>

      val sub = ?("sub")
      val pred = ?("pred")
      val obj = ?("obj")

      val qs = arcs.map(a=>this.arcQuery(a,sub,pred,obj))
      val united = this.foldUnion(qs)

      if(draftsOnly)         
        CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),Pat(sub,WI.PLATFORM.DRAFT_OF,shape.id.asResource),united)
      else  CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),united)
  }

}
