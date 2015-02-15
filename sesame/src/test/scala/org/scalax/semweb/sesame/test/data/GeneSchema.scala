package org.scalax.semweb.sesame.test.data

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDF, RDFS, WI, XSD}
import org.scalax.semweb.sesame.test.classes.Ontology
import org.scalax.semweb.shex._
import org.scalax.semweb.sesame._


trait GeneSchema extends ItemsMock with GeneSchemaData{

  val entrez = IRI("http://ncbi.nlm.nih.gov/gene/")

  object Codes {
    abstract class Evidence {
      lazy val code =  gero / this.getClass.getName.split('$').last
    }

    abstract class Experimental extends Evidence
    abstract class Literature extends Evidence
    abstract  class NoEvidence extends Evidence
    case object IMP extends Experimental
    case object IGI extends Experimental
    case object IPI extends Experimental
    case object IDA extends Experimental
    case object IEP extends Experimental
    case object TAS extends Literature
    case object NAS extends Literature
    case object IC extends NoEvidence
    case object ND extends NoEvidence

  }
  
  lazy val subjectDefs =  SubjectRule(id = IRILabel(gero / "EvidenceShape" / "EntrezGene"))
    .startsWith(entrez).hasBase(entrez).isCalled("ENTREZ ID")

  lazy val evidenceForm = new ShapeBuilder(gero / "Evidence_Shape")

  val cls = Ontology.classes.map(cl=>cl:IRI).toSeq

  val prefixes: Seq[(String, String)] =       Seq("rdf"->RDF.namespace,
  "rdfs"->RDFS.namespace,
  "xsd"->XSD.namespace,
  "owl"->vocabulary.OWL.namespace,
  "dc"->vocabulary.DCElements.namespace,
  "pl"->(WI.PLATFORM.namespace+"/"),
  "shex"-> org.scalax.semweb.shex.rs.stringValue,
  "def"->org.scalax.semweb.shex.se.stringValue,
  "pmid"->"http://ncbi.nlm.nih.gov/pubmed/",
  "gero"->gero.stringValue,
  "entrez"->entrez.stringValue
  )

  import Codes._
  //evidenceForm has entrezId isCalled "ENTREZID" startsWith entrez occurs ExactlyOne hasPriority 1
  evidenceForm has clazz isCalled "Class" startsWith gero occurs ExactlyOne hasPriority 2// from(cls:_*)
  evidenceForm has adb isCalled "DB" occurs ExactlyOne hasPriority 3
  evidenceForm has objId isCalled "DB Object ID" occurs ExactlyOne hasPriority 4
  evidenceForm has symbol isCalled "DB Object Symbol" occurs ExactlyOne  hasPriority 5
  //form has qualifier isCalled "Qualifier" occurs Star hasPriority 5
  //form has go isCalled "GO ID" occurs ExactlyOne
  evidenceForm has ref isCalled "Publication"  occurs Plus hasPriority 6
  evidenceForm has code isCalled "Evidence Code" occurs ExactlyOne hasPriority 7  from(IMP code, IGI code, IPI code, IDA code, IEP code, TAS code,  NAS code,  IC code, ND code)
  //form has from isCalled "With (or) From" occurs Star
  //form has aspect isCalled "Aspect" occurs ExactlyOne hasPriority 8
  evidenceForm has dbObjectName startsWith gero isCalled "DB object Name" occurs Opt hasPriority 8
 // evidenceForm has synonym isCalled "synonym" occurs Star hasPriority 9
  //form has tp isCalled "DB Object Type" occurs ExactlyOne hasPriority 11
  evidenceForm has tp isCalled "Gene product type" startsWith gero occurs ExactlyOne hasPriority 11 from(
    gero / "protein_complex", gero / "protein", gero / "transcript",
      gero / "ncRNA", gero / "rRNA", gero / "tRNA", gero / "snRNA", gero / "snoRNA", gero / "microRNA", gero / "gene_product")
  evidenceForm has taxon isCalled "Organism" startsWith gero occurs Cardinality(1,2) hasPriority 12
  evidenceForm has date isCalled "Date" of XSD.Date occurs ExactlyOne hasPriority 13
  //evidenceForm has assigned isCalled "Assigned by" occurs ExactlyOne hasPriority 14
  //form has extension isCalled "Annotation Ext/ension" occurs Star
  //evidenceForm has product isCalled "Gene Product Form ID" occurs Opt hasPriority 15
  evidenceForm has tissue isCalled "Tissue" occurs Plus hasPriority 17
  evidenceForm has influence isCalled "Influence" occurs ExactlyOne hasPriority 18 from(gero / "Pro-Longevity", gero / "Anti-Longevity")
  evidenceForm hasRule this.subjectDefs
  
  lazy val evidenceShape = evidenceForm.result


  val interventions = gero / "intervention"
  val effect = gero / "intervention_effect"

  lazy val interventionsForm = new ShapeBuilder(gero / "Interventions_Shape")
  interventionsForm has symbol isCalled "Gene Symbol" occurs ExactlyOne hasPriority 1
  interventionsForm has adb


}
