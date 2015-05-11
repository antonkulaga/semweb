package org.denigma.semweb.sesame.shapes

import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.WI
import org.denigma.semweb.shex._
import org.denigma.semweb.sparql._

/**
 * Creates queries that extracts shaped models
 */
class ShapedModelsQueryExtractor extends ArcPropertiesQueryExtractor
{

  /**
   * Provides only valid ShEx Expressions
   * @param shexp
   * @return
   */
/*  def validShexQuery(shexp:ShEx) =  shexp.headOption.map{
    case shape=>
      val arcs = shape.arcSorted
      val sub = ?("sub")

      this.varsForArcs(shape.arcSorted,sub)


      //split arc with valuereferences from others to process separately
      val (refs: List[ArcRule],other: List[ArcRule]) = arcs.partition(a=>a.value.isInstanceOf[ValueReference])

      val refVars =    this.varsForArcs(refs,sub)
      val otherVars: Seq[(ArcRule, (Variable,Variable, Variable))] = this.varsForArcs(other,sub)


      CONSTRUCT(sts:_*).WHERE(conds:_*)
      //validShapeQuery(sh)(shexp)
  }.getOrElse(CONSTRUCT.empty)*/

  def invalidShexQuery(shexp:ShEx,draftsOnly:Boolean = true): ConstructQuery = shexp.headOption.map{
    case shape=> invalidShapeQuery(shape,draftsOnly)(shexp)
  }.getOrElse(CONSTRUCT.empty)


  protected def varsForArcs(arcSorted:Seq[ArcRule],sub:Variable =  ?("sub")) = arcSorted.map{case arc=>
    val cap: String = arcCaption(arc)
    arc -> (sub,?("p_"+cap),?("o_"+cap))
  }


  protected def validShapePatterns(vars: Seq[(ArcRule, (Variable,Variable, Variable))] )(implicit shexp:ShEx): Seq[RDFElement] = vars.foldLeft(Seq.empty[RDFElement]) {
    case (acc, (arc,(s,p,o))) =>
      val ps: Seq[RDFElement] = this.arcValuePatterns(arc,s,o)(shexp)++arcNamePatterns(arc,s,p,o)
      acc++this.arcPatterns(arc,s,p,o)(shexp)
  }

/*  protected def validShapeTuples(vars: Seq[(ArcRule, (Variable,Variable, Variable))] )(implicit shexp:ShEx) =
    vars.foldLeft(Seq.empty[Pat],Seq.empty[RDFElement]) {
      case ( (ac1,ac2), (arc,(s,p,o))) =>
        val ps: Seq[RDFElement] = this.arcValuePatterns(arc,s,o)(shexp)++arcNamePatterns(arc,s,p,o)
        val apats =this.arcPatterns(arc,s,p,o)(shexp)
        arc.value match {
          case ValueReference(ref)=>
            val vars = this.varsForArcs()
            (ac1,ac2++this.arcPatterns(arc,s,p,o)(shexp))
          case other => (ac1,ac2++this.arcPatterns(arc,s,p,o)(shexp))
        }
    }*/

  /**
   * Finds all values that match shapes
   * NOTE: Occurs have not taken into account yet
   * @param shape
   * @return
   */
  def validShapeQuery(shape:Shape)(implicit shexp:ShEx):ConstructQuery = shape.arcSorted match
  {
    case no if no.isEmpty=> CONSTRUCT.empty
    case arcs=>

      //val sub = ?("sub")

      val vars:Seq[(ArcRule, (Variable,Variable, Variable))] = this.varsForArcs(arcs,?("sub"))

      val conds: Seq[RDFElement] = this.validShapePatterns(vars)
      val sts = vars.map{   case (arc,(s,p,o))=>Pat(s,p,o)    }

      CONSTRUCT(sts:_*).WHERE(conds:_*)
  }

  /**
   * Creates arc patterns, that include all: name/value/occurs patterns
   * @param arc arcRule
   * @param sub subject variable
   * @param pred predicate variable
   * @param obj object variable
   * @param shexp implicit ShEx
   * @return
   */
  protected def arcPatterns(arc:ArcRule,sub:Variable, pred:Variable,obj:Variable)(implicit shexp:ShEx) = {
    this.withOccurence(arc, arcNamePatterns(arc,sub,pred,obj)++this.arcValuePatterns(arc,sub,obj)(shexp))
  }

  def valueReferenceTuples(res:Res,sub:Variable,obj:Variable, shapeExp:ShEx)=
  {
    shapeExp.findShape(res) match {
      case Some(shape)=>
        val vars: Seq[(ArcRule, (Variable, Variable, Variable))] = this.varsForArcs(shape.arcSorted,obj)
        val conds = this.validShapePatterns(vars)
        vars.map{ case (arc,(s,p,o)) => Pat(s,p,o)}->conds
      case None=>
        println(s"Cannot find ValueReference for ${res.stringValue}")
        (Seq.empty[Pat],Seq.empty[RDFElement])
    }
  }

  override def valueReferencePatterns(res:Res,sub:Variable,obj:Variable, shapeExp:ShEx): Seq[RDFElement]=
  {
    shapeExp.findShape(res) match {
      case Some(shape)=>
/*        val vars = this.varsForArcs(shape.arcSorted,obj)
        val conds = this.validShapePatterns(vars)*/
        val (pats,conds) = this.valueReferenceTuples(res,sub,obj,shapeExp)
        Seq(SELECT (obj) WHERE(conds:_*))
      case None=>
        println(s"Cannot find ValueReference for ${res.stringValue}")
        Seq.empty[RDFElement]
    }
  }


  /**
   * Loads those shapes that are not valid
   * @param shape
   * @param draftsOnly
   * @param shexp
   * @return
   */
  def invalidShapeQuery(shape:Shape,draftsOnly:Boolean = true)(implicit shexp:ShEx):ConstructQuery = shape.arcSorted match
  {
    case no if no.isEmpty=>CONSTRUCT.empty
    case arcs=>

      val sub = ?("sub")
      val pred = ?("pred")
      val obj = ?("obj")

      val qs: List[SelectQuery] = arcs.map(a=>this.arcQuery(a,sub,pred,obj))
      val united = this.foldUnion(qs)

      if(draftsOnly)         
        CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),Pat(sub,WI.PLATFORM.DRAFT_OF,shape.id.asResource),united)
      else  CONSTRUCT(Pat(sub,pred,obj)).WHERE(Pat(sub,pred,obj),united)
  }

}
