package org.scalax.semweb.shex.utils

import java.io._
import scala.util._

object IO {

 /**
  * Ensures to close a file
  * [[https://wiki.scala-lang.org/display/SYGN/Loan Loan pattern]]
  */
 def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B = {
   try {
	 f(resource)
   } finally {
	 resource.close()
   }
 }
  

 def getContents(fileName: String): Try[CharSequence] = {
  try {
    using(io.Source.fromFile(fileName)){ source => 
      Success(source.getLines().mkString("\n"))
     }
  } catch {
  	case e: FileNotFoundException => { 
  	  Failure(e)
  	}
  	case e: IOException => {
  	  Failure(e)
  	}
  }
 }
}