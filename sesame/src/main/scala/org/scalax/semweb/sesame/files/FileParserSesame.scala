package org.scalax.semweb.sesame.files

import scala.util.Try
import java.net.URL
import java.io.{InputStream, FileInputStream, File}
import org.openrdf.rio.Rio
import org.scalax.semweb.sesame.{SesameDataWriter, LogLike}
import org.scalax.semweb.rdf.IRI


abstract class SesameFileParser extends SesameDataWriter{


  def makeListener(path:String,con:WriteConnection,context:IRI,lg:LogLike):SesameFileListener
  /*
  parses file by its path
   */
  def parseFileByName(path:String,contextStr:String=""): Try[Unit] = {
    val url = if(path.contains(":")) new URL(path) else new File(path).toURI.toURL
    this.parseStream(url.toString,path,url.openStream(),contextStr)
  }

  /*
  parses file
   */
  def parseFile(file:File,contextStr:String=""): Try[Unit] = {
    if(!file.exists()) file.createNewFile()
    val stream: FileInputStream = new FileInputStream(file)
    this.parseStream(file.getName,file.getAbsolutePath,stream,contextStr)
  }

  /*
  parses input stream of data
   */
  def parseStream(fileName:String,path:String,inputStream:InputStream,contextStr:String=""): Try[Unit] = {


    val format = Rio.getParserFormatForFileName(fileName)
    val parser = Rio.createParser(format)
    this.write{con=>
      val context = if(contextStr=="") null else IRI(contextStr)
      val r = this.makeListener(path,con,context,lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, fileName)
    }

  }
}
