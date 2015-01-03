package org.scalax.semweb.sesame.files

import org.openrdf.rio.RDFParser.DatatypeHandling
import org.openrdf.rio.turtle.TurtleParser

import scala.util.Try
import java.net.URL
import java.io.{InputStream, FileInputStream, File}
import org.openrdf.rio.{ParserConfig, Rio}
import org.scalax.semweb.sesame.SesameDataWriter
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.commons.LogLike


trait SesameFileParser extends SesameDataWriter{

  val config:ParserConfig =  new ParserConfig(false,false,true, DatatypeHandling.NORMALIZE)

  def makeListener(filename:String,con:WriteConnection,context:IRI,lg:LogLike):SesameFileListener
  /*
  parses file by its path
   */
  def parseFileByName(path:String,contextStr:String=""): Try[Unit] = {
    val url = if(path.contains(":")) new URL(path) else new File(path).toURI.toURL
    this.parseStream(url.toString,url.openStream(),contextStr)
  }

  /**
   * Parses files
   * @param file file
   * @param contextStr context
   * @return
   */
  def parseFile(file:File,contextStr:String=""): Try[Unit] = {
    if(!file.exists()) file.createNewFile()
    val stream: FileInputStream = new FileInputStream(file)
    this.parseStream(file.getName,stream,contextStr)
  }

  /**
   * Parses input streams of data
   * @param fileName
   * @param inputStream
   * @param contextStr
   * @return
   */
  def parseStream(fileName:String,inputStream:InputStream,contextStr:String=""): Try[Unit] = {


    val format = Rio.getParserFormatForFileName(fileName)
    val parser = Rio.createParser(format)
    parser.setParserConfig(config)
    this.write{con=>
      val context = if(contextStr=="") null else IRI(contextStr)
      val r = this.makeListener(fileName,con,context,lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, fileName)
    }

  }
}
