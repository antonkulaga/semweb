package org.scalax.semweb.sesame.shapes

import org.scalax.semweb.rdf.RDFElement
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.shex.Shape
import org.scalax.semweb.sparql._

class ShapedModelsQueryExtractor extends ArcPropertiesQueryExtractor{


  def validShapeQuery(shape:Shape):ConstructQuery = shape.arcSorted() match
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
      val conds = vars.foldLeft(Seq.empty[RDFElement]) {
        case (acc, (arc,(p,o))) =>
          val ps: Seq[RDFElement] = this.arcValuePatters(arc,sub,o)++arcNamePatterns(arc,sub,p,o)
          acc++ps
      }

      CONSTRUCT(pats:_*).WHERE(conds:_*)
  }
  
  /*
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
   */


  def invalidShapeQuery(shape:Shape,draftsOnly:Boolean = true):ConstructQuery = shape.arcSorted() match
  {
    case no if no.isEmpty=>CONSTRUCT.empty
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

      if(draftsOnly)         
        CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),Pat(sub,WI.PLATFORM.DRAFT_OF,shape.id.asResource),united)
      else  CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),united)
  }

}
