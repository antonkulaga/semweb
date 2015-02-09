package org.scalax.semweb.sesame.shapes

import org.scalax.semweb.rdf.RDFElement
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.shex.Shape
import org.scalax.semweb.sparql._

class ShapeQueryExtractor extends ArcQueryExtractor{


  def validShapeQuery(shape:Shape) = shape.arcSorted() match
  {
    case no if no.isEmpty=> None
    case arcs=>

      val sub = ?("sub")

      val vars = arcs.map{case arc=> 
        val cap = arcCaption(arc)
        arc -> (?("p_"+cap),?("o_"+cap))
      }
      
      val pats = vars.map{
        case (arc,(p,o))=>Pat(sub,p,o)
      }
      val conds = vars.foldLeft(Seq.empty[RDFElement]) {
        case (acc, (arc,(p,o))) =>
          val ps: Seq[RDFElement] = this.arcValuePatters(arc,sub,o)++arcNamePatterns(arc,sub,p,o)
          acc++ps
      }

      val con: ConstructQuery = CONSTRUCT(pats:_*).WHERE(conds:_*)
      Some(con)
  }


  def invalidShapeQuery(shape:Shape,draftsOnly:Boolean = true) = shape.arcSorted() match
  {
    case no if no.isEmpty=> None
    case arcs=>

      val sub = ?("sub")
      val pred = ?("pred")
      val obj = ?("obj")

      val united: RDFElement= arcs.size match {
        case 1 => Br(this.arcQuery(arcs.head,sub,pred,obj))
        case num=>
          val start = Union(Br(this.arcQuery(arcs(0),sub,pred,obj)),Br(this.arcQuery(arcs(1),sub,pred,obj)))
          if(num==2) start else arcs.tail.tail.foldLeft(start)(  (acc,el)=> Union(acc,Br(this.arcQuery(el,sub,pred,obj))))
      }

      val con: ConstructQuery = if(draftsOnly)         CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),Pat(sub,WI.PLATFORM.HAS_STATUS,WI.PLATFORM.DRAFT),united)
      else{
        CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),united)
      }
      Some(con)
  }

}
