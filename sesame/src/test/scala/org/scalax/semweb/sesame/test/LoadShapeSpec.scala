package org.scalax.semweb.sesame.test

import java.io.InputStream

import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf._
import org.scalax.semweb.sparql.{GRAPH, DATA, INSERT}
import org.openrdf.model.{Value, URI, Resource}
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import org.scalax.semweb.rdf.Quad
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sesame._
import scala.io.Source
import scala.util.Try

class LoadShapeSpec  extends  WordSpec with Matchers {

  "Shapes" should {


    "be loaded by name" in {

      val db = BigData(true)
      val st: InputStream = getClass.getResourceAsStream("/shapes.ttl")
      db.parseStream("shapes.ttl",st)
      val res = IRI("http://gero.longevityalliance.org/Evidence_Shape")

      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      shape.id.asResource shouldEqual res
      shape.arcSorted().size shouldEqual 13

/*
      val shops = db.loadAllShapes
      shops.isSuccess shouldEqual true
      shops.get.size shouldEqual 1
      shops.get.head.id.asResource shouldEqual res
*/
      db.shutDown()

    }
  }
}
