package org.denigma.semweb.sesame.shapes

import java.util

import org.openrdf.model.Statement
import org.openrdf.query.{GraphQueryResult, GraphQuery}
import org.denigma.semweb.rdf.vocabulary.WI
import org.denigma.semweb.rdf.{Res, Quad}
import org.denigma.semweb.shex.validation.Failed
import org.denigma.semweb.shex.{Draft, PropertyModel, ArcRule, Shape}
import org.denigma.semweb.sparql._
import org.denigma.semweb.sesame._
import org.openrdf.model.Model
import org.openrdf.model.impl.LinkedHashModel
import scala.collection.JavaConversions._

class ModelStatementsExtractor {


  def modelsFromQuads(sts:Seq[Quad],valid:Boolean=true) = {
    val props = sts.groupBy(q=>q.sub)
    props.map{
      case (s,qu)=>
        val ps = qu.map(st=>st.pred->st.obj)//.toSeq
        val mod = PropertyModel(s,ps:_*)
        if(valid) mod else mod.copy(validation =  Draft)
    }.toSeq
  }
  
  def extractFromStatements[S<:Statement](sts:Seq[Statement],valid:Boolean = true) = modelsFromQuads(sts.map(st=>st:Quad),valid)

  
}


