package org.denigma.semweb.composites

import org.denigma.semweb.shex._
import prickle.{CompositePickler, Pickler, PicklerPair, Unpickler}

import scala.reflect._


trait ShapePicklers extends RDFComposites{
  self:RDFComposites=>



  implicit lazy val labelPickler = CompositePickler[Label].concreteType[IRILabel].concreteType[BNodeLabel]

  implicit lazy val valueTypePickler = Pickler.materializePickler[ValueType]
  implicit lazy val valueTypeUnpickler = Unpickler.materializeUnpickler[ValueType]



  implicit lazy val valueClassPickler = CompositePickler[ValueClass].concreteType[ValueType].concreteType[ValueSet].concreteType[ValueStem].concreteType[ValueAny].concreteType[ValueReference]

  implicit lazy val nameClassPickler = CompositePickler[NameClass].concreteType[NameTerm].concreteType[NameStem].concreteType[NameAny]

  implicit lazy val cardinalityPickler = CompositePickler[Cardinality]
   .concreteType[Plus.type].concreteType[Star.type].concreteType[Opt.type].concreteType[ExactlyOne.type].concreteType[org.denigma.semweb.shex.Range]

  implicit lazy val actionPickler = CompositePickler[Action]


  /* and example how Arc rules look like when it is pickled
  {"name": {"#cls": "org.denigma.semweb.shex.NameStem",
  "#val": {"#id": "4", "s": {"#id": "3", "uri": "http:\/\/ncbi.nlm.nih.gov\/gene\/"}}},
  "priority": {"#id": "7", "#elems": [0.0]}, "#id": "11",
  "id": {"#cls": "org.denigma.semweb.shex.IRILabel",
  "#val": {"#id": "2", "iri": {"#id": "1", "uri": "http:\/\/gero.longevityalliance.org\/"}}},
  "occurs": {"#cls": "org.denigma.semweb.shex.ExactlyOne$",
  "#val": {"#scalaObj": "org.denigma.semweb.shex.ExactlyOne"}},
  "default": {"#id": "10", "#elems": [{"#cls": "org.denigma.semweb.rdf.IRI",
  "#val": {"#id": "9", "uri": ":hello"}}]}, "actions":
  {"#id": "6", "#elems": []},
  "title": {"#id": "8", "#elems": ["Hello world"]},
  "value": {"#cls": "org.denigma.semweb.shex.ValueStem",
  "#val": {"#id": "5", "s": {"#ref": "1"}}}}
  */


  implicit lazy val arcPickler = Pickler.materializePickler[ArcRule]
  implicit lazy val arcUnpickler = Unpickler.materializeUnpickler[ArcRule]


  implicit lazy val subPickler = Pickler.materializePickler[SubjectRule]
  implicit lazy val subUnpickler = Unpickler.materializeUnpickler[SubjectRule]

  implicit lazy val conPickler = Pickler.materializePickler[ContextRule]
  implicit lazy val conUnpickler = Unpickler.materializeUnpickler[ContextRule]


  implicit lazy val rulePickler: PicklerPair[Rule] = CompositePickler[Rule]
    .concreteType[ArcRule].concreteType[SubjectRule].concreteType[ContextRule]
    .concreteType[AndRule](andPickler,andUnpickler, classTag[AndRule]  )
    .concreteType[OrRule](orPickler,orUnpickler, classTag[OrRule]  )


  implicit lazy val andPickler: Pickler[AndRule] = Pickler.materializePickler[AndRule]
  implicit lazy val andUnpickler: Unpickler[AndRule] = Unpickler.materializeUnpickler[AndRule]

  implicit lazy val orPickler: Pickler[OrRule] = Pickler.materializePickler[OrRule]
  implicit lazy val orUnpickler: Unpickler[OrRule] = Unpickler.materializeUnpickler[OrRule]


  implicit lazy val shapePickler = Pickler.materializePickler[Shape]
  implicit lazy val shapeUnpickler = Unpickler.materializeUnpickler[Shape]

  implicit lazy val shexPickler = Pickler.materializePickler[ShEx]
  implicit lazy val shexUnpickler = Unpickler.materializeUnpickler[ShEx]

  import org.denigma.semweb.shex.validation._
  import org.denigma.semweb.shex.{Draft, PropertyModel}

  implicit lazy val justFailedPickler = Pickler.materializePickler[JustFailure]
  implicit lazy val justFailedUnpickler = Unpickler.materializeUnpickler[JustFailure]

  implicit lazy val violationPickler: PicklerPair[Violation] = CompositePickler[Violation].concreteType[JustFailure]

  implicit lazy val failedPickler = Pickler.materializePickler[Failed]
  implicit lazy val failedUnpickler = Unpickler.materializeUnpickler[Failed]

  implicit lazy val validationPickler = CompositePickler[ValidationResult].concreteType[Failed].concreteType[Valid.type ].concreteType[Draft.type]

  implicit lazy val propertyModelPickler = Pickler.materializePickler[PropertyModel]
  implicit lazy val propertyModelUnpickler = Unpickler.materializeUnpickler[PropertyModel]


}
