package org.denigma.semweb.sesame.test.classes

/**
 * Console file for testing
 */
object Console {

  import org.denigma.semweb.rdf._
  import org.denigma.semweb.rdf.vocabulary._
  import org.denigma.semweb.sesame._
  import org.denigma.semweb.shex._
  import org.denigma.semweb.sparql._

  val page = WI.re("Page")

  object shape extends ShapeBuilder(page)

  val title= shape has WI.pl("title") of XSD.StringDatatypeIRI occurs ExactlyOne result
  val text = shape has WI.pl("text") of XSD.StringDatatypeIRI occurs ExactlyOne result
  val author = shape has WI.pl("author") of FOAF.PERSON occurs Plus result
  //val pub = shape has WI.pl("published") of XSD.Date  occurs ExactlyOne result

  val sh = shape.result
  val c = WI.re("context")
  val quads: Set[Quad] = sh.asQuads(c)


  val ins = INSERT (
    DATA (
      GRAPH(c,quads.map(q=>Trip(q.sub,q.pred,q.obj)).toList)
    )
  )
  val db = BigData(true) //cleaning the files and initializing the database

  db.read{con=>
    con.hasStatement(page,RDF.TYPE,rs / "ResourceShape",true,c) 
    con.hasStatement(title.get.me, rs / "propDefinition" ,   WI.pl("title"), true, c) 
    con.hasStatement(title.get.me, rs / "valueType" ,   XSD.StringDatatypeIRI, true, c) 
    con.hasStatement(title.get.me, rs / "occurs" ,   rs / "Exactly-one" , true, c) 

    con.hasStatement(author.get.me, rs / "valueType" ,   FOAF.PERSON, true, c) 
    con.hasStatement(author.get.me, rs / "occurs" ,   rs / "One-or-many" , true, c) 
  }



  val u = db.update(ins.stringValue)
  
  db.shutDown() // shutting down

}
