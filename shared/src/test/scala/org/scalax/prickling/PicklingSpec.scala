package org.scalax.prickling

import java.util.Date

import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.composites.SemanticComposites
import org.scalax.semweb.messages.StringQueryMessages
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{XSD, FOAF}

import prickle._
import utest.framework.TestSuite
import Pickler._
import Unpickler._
import scala.util._
import utest._
import org.scalax.semweb.shex.{BNodeLabel, IRILabel,Label,ValueClass,
ValueType,ValueAny,ValueSet,ValueStem,
NameClass,NameStem,NameAny,ExactlyOne,Plus,Star,Opt,
NameTerm,Cardinality,Action,ArcRule,Rule,SubjectRule,ContextRule,ShapeBuilder,Shape,ShEx
} //because of crazy behaviour of prickle macro and implicit search

//because of crazy behaviour of prickle macro
//!Don't do this. Not Necessary



object PicklingSpec extends TestSuite
 {
  
  def tests =  TestSuite{

     "pickling of RDF types" - {
       import SemanticComposites._
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



     }
    "pickling messages" -{
      import SemanticComposites._
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

    "pickling ArcRule" -{
      import SemanticComposites._
      //this two lines lead to crazy macro errors

      val gero = IRI("http://gero.longevityalliance.org/")
      val entrez = IRI("http://ncbi.nlm.nih.gov/gene/")
      val ns = NameStem(entrez)
      val vs = ValueStem(gero)
      val sex = Pickle.intoString[Cardinality](ExactlyOne)

      val arc: ArcRule = ArcRule(id = IRILabel(gero),
        name = ns,vs,
        ExactlyOne,
        title = Some("Hello world"),
        priority = Some(0),
        default = Some(IRI(":hello"))
      )


      val sns = Pickle.intoString(arc.name)
      val nso = Unpickle[NameClass].fromString(sns)
      assert(nso.isSuccess)
      assert(nso.get==ns)

      val svs = Pickle.intoString(arc.value)
      val vso = Unpickle[ValueClass].fromString(svs)
      assert(vso.isSuccess)
      assert(vso.get==vs)

      val soc = Pickle.intoString(arc.occurs)
      val exo = Unpickle[Cardinality].fromString(soc)
      assert(exo.isSuccess)
      assert(exo.get == arc.occurs)

      val st = Pickle.intoString(arc.title)
      val to = Unpickle[Option[String]].fromString(st)
      assert(to.isSuccess)
      assert(to.get==arc.title)

      val si = Pickle.intoString(arc.id)
      val sio = Unpickle[Label].fromString(si)
      assert(sio.isSuccess)
      assert(sio.get==arc.id)

      val sa = Pickle.intoString(arc.actions)
      val sao =Unpickle[Seq[Action]].fromString(sa)
      assert(sao.isSuccess)
      assert(sao.get==arc.actions)

      val sp =  Pickle.intoString(arc.priority)
      val spo = Unpickle[Option[Int]].fromString(sp)
      assert(spo.isSuccess)
      assert(spo.get==arc.priority)

      val dp = Pickle.intoString(arc.default)
      val dpo = Unpickle[Option[RDFValue]].fromString(dp)
      assert(dpo.isSuccess)
      assert(dpo.get==arc.default)

      val sarc = Pickle.intoString(arc)
      val arco = Unpickle[ArcRule].fromString(sarc)
      assert(arco.isSuccess)
      assert(arco.get==arc)


    }
    
    "pickling shape" -{
      object shape extends ShapeBuilder(IRI("http://myshape.com"))
      shape has FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne
      shape has FOAF.KNOWS oneOf (FOAF.PERSON,FOAF.Group) occurs Plus
      shape.hasRule(SubjectRule())

      val res: Shape = shape.result
      
    }

 }

}