package org.scalax.semweb.sesame.test

import com.bigdata.rdf.sail.{BigdataSailRepositoryConnection, BigdataSailRepository, BigdataSail}
import java.io.File
import java.util.Properties
import org.apache.commons.io.FileUtils
import scala.util.Try
import org.openrdf.query.{TupleQueryResult, QueryLanguage}


/**
 * Factory companion object (static class with the same name)
 */
object BigData {

  /**
   * Factory
   * @param clean if true => delete database journal file (if exists)
   * @param url path to database journal
   * @param dbFileName database journal filename
   * @return
   */
  def apply(clean:Boolean,url:String="./sesame/db/test",dbFileName:String="bigdata.jnl"): BigData = {
    if(clean) cleanLocalDb(url,dbFileName)
    new BigData(url,dbFileName)
  }


  /*
deletes local db file (used mostly in tests)
 */
  def cleanLocalDb(url:String,dbFileName:String): Boolean =  {
    val f = new File(url+"/"+dbFileName)
    if(f.exists()) {
      if(FileUtils.deleteQuietly(f)) {
        println(s"previous database was successfully deleted")
        true
      } else false
    } else false
    //FileUtils.cleanDirectory(new File(dbConfig.url))
  }
}

/**
 * Simpliest as possible BigDataSetup
 */
class BigData(url:String="./sesame/db/test",dbFileName:String="bigdata.jnl")
{


  /**
  BigData settings
   */
  lazy val properties:Properties = {
    val props = new Properties()
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.quadsMode","true")
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex","true")
    props.setProperty("com.bigdata.rdf.sail.truthMaintenance","false")
    props.setProperty("com.bigdata.rdf.sail.statementIdentifiers","false")
    props.setProperty("com.bigdata.journal.AbstractJournal.file",url+"/"+dbFileName)
    props
  }



 /*
  initiates embeded bigdata database
   */
  val sail: BigdataSail = {
     val journal = new File(dbFileName)
      if(!journal.exists())journal.createNewFile()
      properties.setProperty(BigdataSail.Options.DEFAULT_FILE, journal.getAbsolutePath)

    new BigdataSail(properties)
  }


  /*
  Bigdata Sesame repository
   */
  lazy val repo: BigdataSailRepository = {
    val repo = new BigdataSailRepository(sail)
    repo.initialize()
    repo
  }

  def readConnection: BigdataSailRepositoryConnection = repo.getReadOnlyConnection //for convenience, provides read connection

  /**
   * //for convenience, WARNING 1 writer per database
   * @return bigdata run collection
   * @note Bigdata works in OneWriter -> ManyReaders mode. That is why you should check that only one
   * write connection is used at each moment.So it is not safe to use write connection in a way as it is in this example/test
   * Possible solution maybe wrapping getUnisolatedConnection in one akka actor
   */
  def writeConnection: BigdataSailRepositoryConnection = repo.getUnisolatedConnection


  /**
   * Just does select
   * @param query query
   * @param base basic value
   * @return Try with results of query execution
   */
  def select(query:String)(implicit base:String = "http://testme.bigdata.com"): Try[TupleQueryResult] = this.read{
    con=>
      val q = con.prepareTupleQuery(QueryLanguage.SPARQL,query,base)
      q.evaluate()
  }


  /**
   * Note: for tests only
   * @param updateStr insert or delete query
   * @param base base uri,
   * @return
   */
  def update(updateStr:String)(implicit base:String = "http://testme.bigdata.com"): Try[Unit] = this.write{
    con=>
      val u = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,updateStr,base)
      u.execute()
  }

  /**
   * Reads something
   * @param action function that reads
   * @tparam T what it returns
   * @return Try of T
   * @note Reading[T] type alias is defined at simple package object
   */
  def read[T](action:Reading[T]):Try[T]= {
    val con: BigdataSailRepositoryConnection = this.readConnection
    val res = Try {
      action(con)
    }

    con.close()
    res.recoverWith{case
      e=>
      print("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }


  /**
   * Writes something and then closes the connection
   * @param action function to run
   * @tparam T what we want to get in return
   * @return Try of T
   */
  def write[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = this.writeConnection
    con.setAutoCommit(false)
    val res = Try {
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
      e=>
      print("read/write transaction from database failed because of \n"+e.getMessage)
      res
    }
  }



  /**
  shutdowns the database
   */
  def shutDown() = this.repo.shutDown()

}
