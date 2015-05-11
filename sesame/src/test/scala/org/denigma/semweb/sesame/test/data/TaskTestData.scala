package org.denigma.semweb.sesame.test.data

import org.denigma.schemas.web.ProjectSchema
import org.denigma.semweb.rdf.Quads
import org.denigma.semweb.rdf.vocabulary.RDF
import org.denigma.semweb.shex.ShapeBuilder

trait TaskTestData extends JustTestData with ProjectSchema{



  /*
  *
  val taskShape = ShapeBuilder(taskShapeRes) has
    title of XSD.StringDatatypeIRI occurs Opt hasPriority 0 and
    text of XSD.StringDatatypeIRI  occurs ExactlyOne hasPriority 1  and
    creator ofShape this.userShortShape occurs Plus hasPriority 2  and
    priority oneOf(later,low,normal,high,asap) isCalled "has priority" occurs ExactlyOne hasPriority 3  and
    completed of XSD.BooleanDatatypeIRI isCalled "completed" occurs ExactlyOne hasPriority 4 and
    assigned of USERS.UserClass occurs Star isCalled "assignedTo" hasPriority 5 and
    depends_on ofShape taskShapeRes  occurs Opt isCalled "depends on" hasPriority 6 and
    dueDate of XSD.Date occurs Opt isCalled "due date" hasPriority  7 and
    RDF.TYPE of task occurs Plus isCalled "class" hasPriority -1 default task shape


  val projectShapeRes = WI.pl("ProjectShape")
  val projectShape = ShapeBuilder(projectShapeRes) has
  title of XSD.StringDatatypeIRI occurs Opt hasPriority 0 and
  text of XSD.StringDatatypeIRI  occurs ExactlyOne hasPriority 1  and
  creator ofShape this.userShortShape occurs Plus hasPriority 2  and
  dueDate of XSD.Date occurs Opt isCalled "due date" hasPriority  3 and
  depends_on ofShape taskShapeRes  occurs Opt isCalled "depends on" hasPriority 4 and
  RDF.TYPE of project occurs Plus isCalled "class" hasPriority -1 default project shape
  *
  * */

  ShapeBuilder


  //lazy val testData =  List.empty //TODO


}
