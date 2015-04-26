package org.denigma.semweb.shex

import org.denigma.semweb.rdf.Res


case class Typing(map:Map[Res,Set[Res]]) {

 def addType(key:Res,value:Res): Option[Typing] = {
   if (map contains key) {
     Some(Typing(map.updated(key,map(key) + value)))  
   } else {
     Some(Typing(map + ((key,Set(value)))))
   }
 }

 def rmType(key:Res,value:Res): Option[Typing] = {
   if ((map contains key) && (map(key) contains value)) {
     val newSet = map(key) - value
     if (newSet.isEmpty) {
       Some(Typing(map - key))
     } else {
       Some(Typing(map.updated(key,newSet)))
     }
   } else None
 }
 
 def hasType(n: Res): Set[Res] = {
   if (map contains n) map(n)
   else Set()
 }
 
 def combine(other: Typing): Typing = {
   Typing(map ++ other.map)
 }

 def hasTypes(n:Res, nodes:Set[Res]): Boolean = {
   hasType(n) == nodes 
 }


}

object Typing {

  def emptyTyping : Typing = Typing(Map[Res,Set[Res]]())

}