package org.denigma.schemas.common

import java.io.FileWriter


trait Write2File {

   def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B =
     try { f(param) } finally { param.close() }

   def writeToFile(fileName:String, data:String) =
     using (new FileWriter(fileName)) {
       fileWriter => fileWriter.write(data)
     }
 }
