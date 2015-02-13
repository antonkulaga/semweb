package org.scalax.semweb.sesame.test.classes

import java.io.{Reader, File, InputStream}
import java.util.Properties

import com.bigdata.rdf.rio.turtle.BigdataTurtleParser
import com.bigdata.rdf.sail._
import org.apache.commons.io.FileUtils
import org.openrdf.query.QueryLanguage
import org.openrdf.rio.RDFParser.DatatypeHandling
import org.scalax.semweb.commons.LogLike
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sesame._
import org.scalax.semweb.sesame.files.{SesameFileListener, SesameFileParser}
import org.scalax.semweb.sesame.shapes.ShapeReader

import scala.util.Try


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
class BigData(url:String="./sesame/db/test",dbFileName:String="bigdata.jnl") extends SesameDataWriter with SesameFileParser
with SesameReader with SelectReader with AskReader with ConstructReader with ShapeReader {
  self=>

  type WriteConnection = BigdataSailRepositoryConnection
  type ReadConnection = BigdataSailRepositoryConnection

  override def lg: LogLike = TestLog

  type SelectQuery = BigdataSailTupleQuery

  type AskQuery = BigdataSailBooleanQuery

  type ConstructQuery = BigdataSailGraphQuery


  override def makeSelectQuery(con: ReadConnection, query: String)(implicit base: String = WI.RESOURCE): SelectQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,query,base)

  override def makeAskQuery(con: ReadConnection, query: String)(implicit base: String = WI.RESOURCE): AskQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL,query,base)

  override def makeConstructQuery(con: ReadConnection, query: String)(implicit base: String = WI.RESOURCE): ConstructQuery = con.prepareGraphQuery(QueryLanguage.SPARQL,query,base)


  /**
  BigData settings
   */
  lazy val properties:Properties = {
    val props = new Properties()
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.quadsMode","true")
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex","true")
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.storeBlankNodes","true")

    props.setProperty("com.bigdata.rdf.sail.truthMaintenance","false")
    props.setProperty("com.bigdata.rdf.sail.statementIdentifiers","true")
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


  def readConnection= repo.getReadOnlyConnection //for convenience, provides read connection

  /**
   * //for convenience, WARNING 1 writer per database
   * @return bigdata run collection
   * @note Bigdata works in OneWriter -> ManyReaders mode. That is why you should check that only one
   * write connection is used at each moment.So it is not safe to use write connection in a way as it is in this example/test
   * Possible solution maybe wrapping getUnisolatedConnection in one akka actor
   */
  def writeConnection = repo.getUnisolatedConnection

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
  
  def makeParser() = {
    val parser = new BigdataTurtleParser()
    parser.setPreserveBNodeIDs(true)
    parser.setDatatypeHandling(DatatypeHandling.VERIFY)
    parser.setParserConfig(config)
    parser
  }
  
  def parseReader(reader:Reader,name:String = "turtle", contextStr:String="") = {
    val parser = this.makeParser()
    this.write{con=>
      val context = if(contextStr=="") null else IRI(contextStr)
      val r = this.makeListener(name,con,context,lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(reader,contextStr)
    }
  }

  override def parseStream(fileName:String,inputStream:InputStream,contextStr:String=""): Try[Unit] = {
    val parser = this.makeParser()
    this.write{con=>
      val context = if(contextStr=="") null else IRI(contextStr)
      val r = this.makeListener(fileName,con,context,lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, fileName)
    }

  }



  /**
  shutdowns the database
   */
  def shutDown() = this.repo.shutDown()

  override def makeListener(filename: String, con: WriteConnection, context: IRI, lg: LogLike): SesameFileListener = new TestListener(filename,con,lg)
}

class TestListener(fileName:String,val writeConnection:BigdataSailRepositoryConnection, val lg:LogLike) extends SesameFileListener(fileName)(lg)
{
  type WriteConnection = BigdataSailRepositoryConnection
}