package org.scalax.semweb.sesame.test.classes
import java.io.InputStream

import org.openrdf.model.{Statement, Resource}
import org.openrdf.query.GraphQueryResult
import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.{IRI, _}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame.test.classes.{BigData, GeneLoader}
import org.scalax.semweb.shex.{Shape, _}
import org.scalax.semweb.sesame._
import org.scalax.semweb.sparql.SelectQuery
import org.scalax.semweb.sesame._

trait GeneSchemaData {
  lazy val gero = IRI("http://gero.longevityalliance.org/")

  lazy val (entrezId:IRI,adb:IRI,objId:IRI,symbol:IRI, qualifier:IRI, go:IRI,
  ref:IRI, code:IRI , from:IRI, aspect:IRI,  dbObjectName:IRI, synonym:IRI, tp:IRI,
  taxon:IRI, date:IRI, assigned:IRI, extension:IRI, product:IRI,
  clazz:IRI, tissue:IRI, influence:IRI
    ) =
    (gero / "has_ENTREZID",gero / "from_db" ,  gero / "is_DB_object" ,  gero / "has_symbol", gero / "has_qualifier", gero / "has_GO",
      gero / "has_ref", gero / "has_code" , gero / "is_from",
      gero / "has_aspect", gero / "has_name", gero / "has_synonym", gero / "of_type",
      gero / "from_model_organism", gero / "added_on", gero / "is_assigned_by",
      gero / "has_extension", gero / "is_product",
      RDFS.SUBCLASSOF, gero / "from_tissue", gero / "has_influence"
      )
}
