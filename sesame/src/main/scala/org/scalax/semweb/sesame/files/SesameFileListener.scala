package org.scalax.semweb.sesame.files

import org.{openrdf=>se}
import org.openrdf.model._
import se.rio._
import org.scalax.semweb.sesame.CanWriteSesame
import org.scalax.semweb.commons.LogLike


/**
class that reads RDF
@param fileName name of the file
@param context Context with which all triplets from the file will be writeen as quads
@param lg Logger to log what is happening
 */
abstract class SesameFileListener(fileName:String, context: se.model.Resource = null)(implicit lg:LogLike) extends RDFHandler with CanWriteSesame with ParseErrorListener{


  lazy val f = this.writeConnection.getValueFactory

  override def handleComment(comment: String): Unit = {
    //comment

  }

  /*
  adds parsed statements
   */
  override def handleStatement(st: Statement): Unit =  if(context==null){
    this.writeConnection.add(st)

  }  else {
    val stq= f.createStatement(st.getSubject,st.getPredicate,st.getObject,context)
    this.writeConnection add stq
  }

  override def handleNamespace(prefix: String, uri: String): Unit = if(prefix!=null && this.writeConnection.getNamespace(prefix)==null) {
    this.writeConnection.setNamespace(prefix,uri)
  }

  override def endRDF(): Unit = {
    this.writeConnection.commit()
  }

  override def startRDF(): Unit = {
    lg.info(s"$fileName parsing has started\n")

  }

  override def fatalError(msg: String, lineNo: Int, colNo: Int): Unit = {
    lg.error(s"FATAL error $msg at LINE $lineNo COL $colNo occurred when parsing:  $fileName")
    this.writeConnection.rollback()
  }

  override def error(msg: String, lineNo: Int, colNo: Int): Unit = {
    lg.error(s"nonfatal error $msg at LINE $lineNo COL $colNo occurred when parsing:  $fileName")
  }

  override def warning(msg: String, lineNo: Int, colNo: Int): Unit = {
    lg.warn(s"WARNING $msg at LINE $lineNo COL $colNo occurred when parsing:  $fileName")
  }
}

