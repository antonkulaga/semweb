package org.scalax.semweb.sesame.test

import org.openrdf.model.impl.{URIImpl, StatementImpl}
import org.openrdf.model.{Resource, Value, URI}
import scala.collection.immutable.List


/**
 * Just creates some test data to be mixed into TestSpec
 */
trait SimpleTestData
{

  type Triple = (Resource,URI,Value)

  def sub(str:String) = s"http://denigma.org/actors/resources/$str"
  def prop(rel:String) = s"http://denigma.org/relations/resources/$rel"
  def obj(str:String) = s"http://denigma.org/actors/resources/$str"

  def subject(str:String) = new URIImpl(sub(str))
  def property(str:String) = new URIImpl(prop(str))
  def predicate(str:String) = new URIImpl(obj(str))

  val Daniel = subject("Daniel")
  val Liz = subject("Liz")
  val Anton = subject("Anton")
  val Ilia = subject("Ilia")
  val Edouard = subject("Edouard")
  val RDF = predicate("RDF")
  val Immortality = predicate("Immortality")
  val loves = property("loves")
  val hates = property("hates")

  /**
   * Test data, arbitary statements to test on
   */
  val testData =
    List(
      (Daniel, loves, RDF),
      (Anton, hates, RDF),
      (Daniel,loves,Immortality),
      (Liz,loves,Immortality),
      (Anton,loves,Immortality),
      (Ilia, loves, Immortality),
      (Edouard, loves, Immortality))




  def add(statements:List[(Resource,URI,Value)])(db:BigData) = {
    db.write{
      implicit con=>
        statements.foreach{case (s,p,o)=>
          val st = new StatementImpl(s, p, o)
          con.add(st)
        }

    }
  }

  def addTestData(db:BigData) = add(testData)(db)


}
