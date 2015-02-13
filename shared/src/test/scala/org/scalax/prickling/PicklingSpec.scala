package org.scalax.prickling

import java.util.Date

import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.composites.SemanticComposites
import org.scalax.semweb.messages.StringQueryMessages
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf._

import prickle._
import utest.framework.TestSuite
import Pickler._
import scala.util._
import utest._

//!Don't do this. Not Necessary

object PicklingSpec extends TestSuite
 {
  import SemanticComposites._


  def tests =  TestSuite{

     "pickling of RDF types" - {
       val bar = IRI("http://bar")
       val ur: String = Pickle.intoString(bar)
       val unp:Try[IRI] = Unpickle[IRI].fromString(ur)
       assert(unp.isSuccess)
       assert( unp.get.stringValue == bar.stringValue )

       /*
       * TODO: write abut weird results:
       * URI IS {"#id": "1", "uri": "http:\/\/bar"}
       * But Res string is 222STRING IS  {"#cls": "org.scalax.semweb.rdf.IRI", "#val": {"#id": "1", "uri": "http:\/\/bar"}}
       * * * * */

       val re: String = Pickle.intoString[Res](bar)

       val unp2:Try[Res] = Unpickle[Res].fromString(re)
       assert(unp2.isSuccess)
       assert( unp2.get.stringValue == bar.stringValue )

       val va: String = Pickle.intoString[RDFValue](bar)

       val unp3:Try[RDFValue] = Unpickle[RDFValue].fromString(va)
       assert(unp3.isSuccess)
       assert( unp3.get.stringValue == bar.stringValue )
       
       val hello = "hello_world"
       val stLit = StringLiteral(hello)
       
       val st = Pickle.intoString[RDFValue](stLit)
       val unps3 = Unpickle[RDFValue].fromString(st)
       assert(unps3.isSuccess)
       assert( unps3.get.stringValue.contains(hello))
       
/*       val tryLit = Unpickle[StringLiteral].fromString(st)
       assert(tryLit.isSuccess)
       assert(tryLit.get.label==hello)*/
       


     }
    "pickling messages" -{
      
      val q1 = "query1"
      val id = "myId"
      val ask = StringQueryMessages.Ask(q1,id)
      val stAsk = Pickle.intoString[StringQueryMessages.StringMessage](ask)
      val askT = Unpickle[StringQueryMessages.StringMessage].fromString(stAsk)
      val res = askT.get
      assertMatch(res){case StringQueryMessages.Ask("query1","myId",_,_)=>}
      
      val construct = StringQueryMessages.Construct(q1,id)
      val stCon = Pickle.intoString[StringQueryMessages.StringMessage](construct)
      val conT = Unpickle[StringQueryMessages.StringMessage].fromString(stCon)
      assertMatch(conT.get){
        case StringQueryMessages.Construct("query1","myId",_,d:Date)=>
      }

    }


    "pickling arc" -{

      //this two lines lead to crazy macro errors
     lazy val gero = IRI("http://gero.longevityalliance.org/")
     val entrez = IRI("http://ncbi.nlm.nih.gov/gene/")
      
      /*
     val arc = ArcRule(id = IRILabel(gero),name = NameStem(entrez),ValueStem(gero),Plus,title = Some("Hello world"),priority = Some(0))

     val str = Pickle.intoString[ArcRule](arc)
     val arco = Unpickle[ArcRule].fromString(str)
     assert(arco.isSuccess)
     val arc2 = arco.get
     assert(arc == arc2)
     */
   }



 }

}