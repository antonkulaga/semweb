package org.denigma.semweb.sesame.test.data.genes

import java.io.InputStream

import org.denigma.semweb.rdf.{BasicTriplet, IRI, Quad, Trip}
import org.denigma.semweb.sesame.test.classes.{Ontology, TurtleMaster}
import org.denigma.semweb.shex.PropertyModel
import org.openrdf.model.Statement

import scala.io.Source

/**
 * Literature controller
 */
object Genes extends LoadGenAge{




  def writeGenes() = {
      //val fileName = "resources/data_from_geneage.csv"
      val fileName = "/annotations.tsv"
      val str = readFrom(fileName)

      val indexed: List[PropertyModel] = testGenesTable(str)
      val trips = (for{
        mod <- indexed
        id = mod.id
        (prop,values) <- mod.properties
        obj <-values
      } yield Trip(id,prop,obj)).toSet
    this.writeTurtle(trips,this.prefixes)
  }

  /**
   * Writes turtle to string
   * @param trips
   * @param prefs
   * @tparam T
   * @return
   */
  def writeTurtle[T<:BasicTriplet](trips:Set[T],prefs:Seq[(String,String)]): String =  TurtleMaster.write(trips,prefs:_*).get



  def ontology() = {
      import org.denigma.semweb.sesame._

      import scala.collection.JavaConversions._
      val statements: List[Statement] = Ontology.allFacts.flatMap(g=>g.graph)

      val sts = statements.map(st=>Trip(st.getSubject,st.getPredicate,st.getObject))
      val facts:Set[Trip] = Set(sts:_*)
        //.map(st:Statement=>Trip(st.getSubject,st.getPredicate,st.getObject))
      this.writeTurtle[Trip](facts,this.prefixes)

  }


  def showEvidence() = {
    val quads: Set[Quad] = this.evidenceShape.asQuads(IRI("http://denigma.org/resource/"))
    this.writeTurtle(quads,this.prefixes)//TurtleMaster.simpleWrite(quads)
  }


  def readFrom(filename:String): String = {
    val st: InputStream = getClass.getResourceAsStream(filename)
    //val url: String = controllers.routes.Assets.at(path).absoluteURL(secure = false)(request)
    //Source.fromURL(url).getLines().reduce(_+"\n"+_)
    Source.fromInputStream(st).getLines().reduce(_+"\n"+_)
  }



}
