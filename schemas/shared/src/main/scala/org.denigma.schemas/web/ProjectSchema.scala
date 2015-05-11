package org.denigma.schemas.web

import org.denigma.schemas.common.BasicSchema
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.shex
import org.denigma.semweb.shex._
import WI.PLATFORM
trait ProjectSchema extends UserSchema {

  val task = WI.pl("Task")
  val project = WI.pl("Project")
  val depends_on = WI.pl("depends_on")
  val part_of = WI.pl("part_of")


  val (later,low,normal,high,asap) = (WI.pl("DoItLater"),WI.pl("Low"),WI.pl("Normal"),WI.pl("High"),WI.pl("ASAP"))

  val (title,text,creator,priority) = (DCTerms.title,DCTerms.text,DCElements.creator,PLATFORM.priority)

  val dueDate = WI.pl("has_due_date")

  val dependsOn = WI.pl("depends_on")

  val taskShapeRes = WI.pl("TaskShape")

  val completed = (WI.PLATFORM / "is_completed").iri
  val assigned = (WI.PLATFORM / "assigned_to").iri


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


  //SIOC.has_reply ofShape

}