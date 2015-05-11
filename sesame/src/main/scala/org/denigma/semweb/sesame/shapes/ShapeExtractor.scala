package org.denigma.semweb.sesame.shapes

import org.denigma.semweb.commons.LogLike
import org.openrdf.model._
import org.openrdf.model.vocabulary
import org.openrdf.query.{TupleQuery, QueryLanguage}
import org.openrdf.repository.RepositoryConnection
import org.denigma.semweb.commons.LogLike
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.sesame._
import org.denigma.semweb.shex._
import org.openrdf.repository.RepositoryConnection
import org.denigma.semweb.rdf._
import org.denigma.semweb.sesame._
import org.denigma.semweb.shex._
import org.denigma.semweb.shex.validation.{Failed, Valid, ValidationResult}
import org.denigma.semweb.sparql
import org.denigma.semweb.sparql.SELECT
import org.denigma.semweb.sparql._
import scala.annotation.tailrec
import scala.util.Try



/**
 * Extracts shape having the connection
 * @param lg
 * @tparam ReadConnection
 */
class ShapeExtractor[ReadConnection<: RepositoryConnection](val lg:LogLike) extends 
ArcExtractor[ReadConnection]
{
  lazy val fieldRulesExtractor = new FieldRulesExtractor[ReadConnection]

  /**
   * Recursive function that extracts shape together with subshapes
   * @param refs
   * @param con
   * @param cache
   * @param contexts
   * @return
   */
  @tailrec
  final def shapesFrom(refs:List[Res],con:ReadConnection,cache:Map[Res,Shape] = Map.empty)
                (implicit contexts:Seq[Resource] = List.empty[Resource]): List[Shape] =
  refs match
  {
    case Nil=>cache.values.toList
    case head::tail=>
      if(cache.contains(head)) shapesFrom(tail,con,cache)(contexts) else{
        val sh = getShape(head,con)(contexts)
        shapesFrom(sh.shapeRefs.toList++refs,con,cache + (head->sh))(contexts)
      }
  }

  /**
   * Extracts Shape Expressions from the database
   * @param shapeRes
   * @param con
   * @param prefixes
   * @param contexts
   * @return
   */
  def getShEx(shapeRes:Res,con:ReadConnection,prefixes:Seq[(String,String)] = Seq.empty)
             (implicit contexts:Seq[Resource] = List.empty[Resource]): ShEx =
  {
    val rules: List[Shape] = shapesFrom(shapeRes::Nil,con)(contexts)
    val label:Label = shapeRes
    ShEx(label,rules,Some(label),prefixes = prefixes)
  }

  def getShape(shapeRes:Res,con:ReadConnection)(implicit contexts:Seq[Resource] = List.empty[Resource]): Shape =
  {

    val arcs = for{
      res<- con.resources(shapeRes:Resource,ArcRule.property:URI,contexts)
      arc <- getArc(res,con)(contexts)
    } yield arc

    val fieldRules: Set[FieldRule] = fieldRulesExtractor.extractFieldRules(shapeRes,con)
    val and = AndRule(arcs.toSet++fieldRules,shapeRes)
    
    Shape(shapeRes,and)
  }


  /**
   * Extracts properties depending on their value types
   * @param res Resource of the PropertyModel
   * @param p
   * @param valueClass oneOf(ValueReferene or ValueType or ValueSet)
   * @param con
   * @param contexts
   * @return
   */
  def propertyByValueClass(res:Res,p:IRI,valueClass:ValueClass)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]) = valueClass match
  {
    case ValueSet(s)=>
      p -> con.objects(res, p,contexts).filter(o => s.contains(o: RDFValue))

    case ValueType(i)=> i match
    {
      case RDF.VALUE=>
        val obj = ?("obj")

        //val q = SELECT(obj) WHERE Pat(res,p,obj)

        p -> con.objects(res,p,contexts)

      case XSD.StringDatatypeIRI=>

        p->con.objects(res,p,contexts).filter {
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

      case RDFS.RESOURCE=>
        p -> con.resources(res,p,contexts)

      case tp=>
        import org.denigma.semweb.sparql._
        p -> con.resources(res,p,contexts)
          .filter(o=>con.hasStatement(o,RDF.TYPE,tp,true,contexts:_*))
    }

    case stem:ValueStem =>p -> con.uris(res,p,contexts).filter{case obj=>
      //lg.error(s"\n${stem.s.stringValue} IS start OF ${obj.stringValue} EQUALS ${stem.matches(obj)} ")
      stem.matches(obj)
    }

    case ValueReference(ref)=>
      lg.error("value reference is not yet supported")
      ???

    case _ =>
      lg.error("another unknown ARC case")
      ???
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
  def propertyByArc(res:Res,p:IRI,arc:ArcRule)
                   (implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]): (IRI, Seq[Value]) =
    this.propertyByValueClass(res,p,arc.value)(con,contexts)
  /**
   * Loads property model by ArcValue
   * @param model
   * @param arc
   * @param con
   * @param contexts
   * @return
   */
  def modelWithArc(model:PropertyModel,arc:ArcRule)(implicit con:ReadConnection, contexts:Seq[Resource] = List.empty[Resource]): PropertyModel = {

    arc.name match {
      case NameTerm(prop)=>
        val (pr:IRI, values:Seq[Value]) =  propertyByValueClass(model.resource,prop,arc.value)(con,contexts)
        //lg.debug(s"\n${model.id.stringValue} WITH PROPERTY ${pr.stringValue} WITH VALUES ${values.toString}")
        val checks: ValidationResult = this.checkOccurrence(arc.occurs,pr,values)
        val vals: Set[RDFValue] = values.map(v=>v:RDFValue).toSet
        model.copy(properties = model.properties + (pr -> vals) ,validation =  model.validation.and(checks))

      case NameStem(stem)=>
        import org.denigma.semweb.sparql._
        import org.denigma.semweb.sesame._
        val p = ?("prop")
        val query = SELECT(p) WHERE FILTER(STR_STARTS(STR(p),stem.stringValue))
        val props = con.prepareTupleQuery(QueryLanguage.SPARQL,query.stringValue).evaluate().extractVars(p)
        lg.error("Name stems property extraction not implemented")
        ??? //TODO complete
      case _ =>
        lg.error("other nameterms are also not implemented")
        ??? //TODO complete

    }
  }



}
