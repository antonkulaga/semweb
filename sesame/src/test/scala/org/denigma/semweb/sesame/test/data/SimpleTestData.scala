package org.denigma.semweb.sesame.test.data

import org.denigma.semweb.sesame.test.classes.BigData
import org.openrdf.model.impl.{StatementImpl, URIImpl}
import org.openrdf.model.{Resource, URI, Value}

import scala.collection.immutable.List


/**
 * Just creates some test data to be mixed into TestSpec
 */
trait SimpleTestData extends JustTestData
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
  lazy val testData:List[(Resource,URI,Value)] =
    List(
      (Daniel, loves, RDF),
      (Anton, hates, RDF),
      (Daniel,loves,Immortality),
      (Liz,loves,Immortality),
      (Anton,loves,Immortality),
      (Ilia, loves, Immortality),
      (Edouard, loves, Immortality))



}
