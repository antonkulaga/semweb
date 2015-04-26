package org.denigma.shapes

import org.denigma.semweb.rdf.{IRI, IntLiteral, Quad}
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.shex._
import utest._

object ShapesToRDFSuite extends TestSuite{

  def tests = TestSuite{

    val subject = IRI("http://foo.bar")
    implicit val context = IRI("http://in.bar.we.trust")

    val core = "http://open-services.net/ns/core#"

    val occurs = IRI(core+"occurs")
    val minOccurs = IRI(core+"minoccurs")
    val maxOccurs = IRI(core+"maxoccurs")
    val once = IRI(core+"Exactly-one")
    val oneOrMany = IRI(core+"One-or-many")
    val zeroOrMany = IRI(core+"Zero-or-many")
    val zeroOrOne = IRI(core+"Zero-or-one")

   // val one = IRI(core+"One")



    "quads conversions" - {

      "should work well for cadinality constrains:" - {
        "ExactlyOne and Plus" - {
          val q1 = ExactlyOne.toQuads(subject).head

          assert( q1.sub == subject)
          assert( q1.pred == occurs)
          assert( q1.obj == once)
          assert( q1.cont == context)

          val q2 = Plus.toQuads(subject).head

          assert( q2.sub == subject)
          assert( q2.pred == occurs)
          assert( q2.obj == oneOrMany)
          assert( q2.cont == context)
        }

        "zero, zero-one and opt constrains" - {
          val q3 = Star.toQuads(subject).head

          assert( q3.sub == subject)
          assert( q3.pred == occurs)

          assert( q3.obj == zeroOrMany)
          assert( q3.cont == context)

          val q4 = Opt.toQuads(subject).head

          assert( q4.sub == subject)
          assert( q4.pred == occurs)
          assert( q4.obj == zeroOrOne)
          assert( q4.cont == context)
        }

        "bounded constrains" - {
          val q5: Set[Quad] = Cardinality(2,3).toQuads(subject)
          val q = q5.toList
          assert(q.size==2)
          assert( q(0) == Quad(subject,minOccurs,IntLiteral(2),context) )
          assert( q(1) == Quad(subject,maxOccurs,IntLiteral(3),context) )
        }


        val page = WI.re("Page")
        val c = WI.re("context")

        "write simple shapes" - {



          object shape extends ShapeBuilder(page)

          val title= shape has WI.pl("title") of XSD.StringDatatypeIRI occurs ExactlyOne result
          val text = shape has WI.pl("text") of XSD.StringDatatypeIRI occurs ExactlyOne result
          val author = shape has WI.pl("author") of FOAF.PERSON occurs Plus result
          //val pub = shape has WI.pl("published") of XSD.Date  occurs ExactlyOne result

          val sh = shape.result

          val quads: Set[Quad] = sh.asQuads(c)


          assert { quads.exists(v=>v.sub==page&& v.pred==RDF.TYPE && v.obj== rs / "ResourceShape") }
          val tq = title.get.toQuads(page)(c)
          assert { tq.size == 5} //used to be 4

          assert { title.get.toQuads(page).exists(v=>v.sub==page && v.obj==title.get.me) }


          assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==title.get.me) }
          assert { quads.count(v=>v.sub==title.get.me) == 4 } //used to be 3
          assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "valueType" && v.obj ==  XSD.StringDatatypeIRI )}
          assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("title") )}
          assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "occurs" && v.obj == rs / "Exactly-one" )}


          assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==text.get.me) }
          assert { quads.count(v=>v.sub==text.get.me) == 4 } //used to be 3
          assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "valueType" && v.obj ==  XSD.StringDatatypeIRI )}
          assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("text") )}
          assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "occurs" && v.obj ==  rs / "Exactly-one" )}

          assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==author.get.me) }
          assert { quads.count(v=>v.sub==author.get.me) == 4 } //used to be 4
          assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "valueType" && v.obj ==  FOAF.PERSON )}
          assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("author") )}
          assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "occurs" && v.obj ==  rs / "One-or-many" )}

        }

        "write shape with context and subject rules" - {
          val page = WI.re("Page")

          object shape extends ShapeBuilder(page)

          val title= shape has WI.pl("title") of XSD.StringDatatypeIRI occurs ExactlyOne result
          val text = shape has WI.pl("text") of XSD.StringDatatypeIRI occurs ExactlyOne result
          val author = shape has WI.pl("author") of FOAF.PERSON occurs Plus result
          //val pub = shape has WI.pl("published") of XSD.Date  occurs ExactlyOne result

          val sh = shape.result
          val c = WI.re("context")
          val quads: Set[Quad] = sh.asQuads(c)


          assert { quads.exists(v=>v.sub==page&& v.pred==RDF.TYPE && v.obj== rs / "ResourceShape") }
          val tq = title.get.toQuads(page)(c)
          assert { tq.size == 5} //used to be 4

          assert { title.get.toQuads(page).exists(v=>v.sub==page && v.obj==title.get.me) }


          assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==title.get.me) }
          assert { quads.count(v=>v.sub==title.get.me) == 4 } //used to be 3
          assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "valueType" && v.obj ==  XSD.StringDatatypeIRI )}
          assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("title") )}
          assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "occurs" && v.obj == rs / "Exactly-one" )}


          assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==text.get.me) }
          assert { quads.count(v=>v.sub==text.get.me) == 4 } //used to be 3
          assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "valueType" && v.obj ==  XSD.StringDatatypeIRI )}
          assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("text") )}
          assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "occurs" && v.obj ==  rs / "Exactly-one" )}

          assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==author.get.me) }
          assert { quads.count(v=>v.sub==author.get.me) == 4 } //used to be 3
          assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "valueType" && v.obj ==  FOAF.PERSON )}
          assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("author") )}
          assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "occurs" && v.obj ==  rs / "One-or-many" )}
        }
      }



    }


  }

}

