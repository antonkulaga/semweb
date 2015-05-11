package org.denigma.semweb.sesame.test.data

import org.denigma.semweb.sesame.test.classes.BigData
import org.openrdf.model.impl.StatementImpl
import org.openrdf.model.{Value, URI, Resource}

import scala.collection.immutable.List
import scala.util.Try

/**
 * Interface that adds test data
 */
trait JustTestData {


  def testData:List[(Resource,URI,Value)]

  def add(statements:List[(Resource,URI,Value)])(db:BigData) = {
    db.write{
      implicit con=>
        statements.foreach{case (s,p,o)=>
          val st = new StatementImpl(s, p, o)
          con.add(st)
        }

    }
  }

  def addTestData(db:BigData): Try[Unit] = add(testData)(db)

}
