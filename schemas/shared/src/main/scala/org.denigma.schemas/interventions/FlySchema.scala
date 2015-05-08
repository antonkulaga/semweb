package org.denigma.schemas.interventions

import org.denigma.schemas.common.BasicSchema
import org.denigma.semweb.rdf.vocabulary.{RDF, XSD}
import org.denigma.semweb.shex._

trait FlySchema extends BasicSchema{

  val lifespanChange = gero / "lifespan_change"
  val avgLifespanChange = gero / "avg_lifespan_change"
  val maxLifespanChange= gero / "max_lifespan_change"
  val uas =  gero / "has_UAS"
  val gal = gero / "has_GAL"
  val research = gero / "Research"
  val experiment = gero / "Experiment"
  val source = gero / "source"
  val uasConstruct = gero / "UAS"
  val gal4Construct = gero / "GAL4"
  val gene = gero / "Gene"

  val uasShape = ShapeBuilder(gero / "UAS_Shape") has
    source of RDF.VALUE occurs Opt and
    RDF.TYPE of uasConstruct shape

  val galShape = ShapeBuilder(gero / "GAL4_Shape") has
    source of RDF.VALUE occurs Opt and
    RDF.TYPE of gal4Construct shape

  val lifespanShape = ShapeBuilder(gero / "lifespan_change") has
    avgLifespanChange of XSD.DoubleDatatypeIRI occurs Star isCalled "Avg. lifespan change +%" and
    maxLifespanChange of XSD.DoubleDatatypeIRI occurs Star isCalled "Max. lifespan change +%" and
    source of RDF.VALUE occurs Opt shape



  lazy val flyShape=  ShapeBuilder(gero / "Fly_Shape") has
  RDF.TYPE of research occurs  ExactlyOne default experiment and
  RDF.TYPE of gene occurs  ExactlyOne and
  uas of uasConstruct occurs Star and
  gal ofShape galShape occurs Star and
  lifespanChange ofShape lifespanShape occurs ExactlyOne isCalled "Lifespan change +%" shape


}

/*
*
Ген
% Увеличения ПЖ
UAS-конструкция (Источник)
Драйвер GAL4 (Источник)
Ссылка на статью
Atg8a (autophagy-related 8a)
СПЖ +56%
EP‑UAS‑Atg8a (Bloomington)
APPL-Gal4
(Dr. Kalpana
White)

(Simonsen et al., 2008)
Gclc (Glutamate-Cysteine Ligase)

/*  lazy val (entrezId:IRI,adb:IRI,objId:IRI,symbol:IRI, qualifier:IRI, go:IRI,
  ref:IRI, code:IRI , from:IRI, aspect:IRI,  dbObjectName:IRI, synonym:IRI, tp:IRI,
  taxon:IRI, date:IRI, assigned:IRI, extension:IRI, product:IRI,
  clazz:IRI, tissue:IRI, influence:IRI
    ) =
    (gero / "has_ENTREZID",gero / "from_db" ,  gero / "is_DB_object" ,  gero / "has_symbol", gero / "has_qualifier", gero / "has_GO",
      gero / "has_ref", gero / "has_code" , gero / "is_from",
      gero / "has_aspect", gero / "has_name", gero / "has_synonym", gero / "of_type",
      gero / "from_model_organism", gero / "added_on", gero / "is_assigned_by",
      gero / "has_extension", gero / "is_product",
      RDFS.SUBCLASSOF, gero / "from_tissue", gero / "has_influence"
      )
  */



Max ПЖ + 50%









UAS-GCLc  (Glutamate-cysteine ligase catalytic subunit)
(Dr. William C. Orr)





Tub-GAL4 (повсеместно)
 elav-GAL4 (нейроны)
Appl-GAL4 (нейроны)
 D42-GAL4 (нейроны)
(Dr. Blanka Rogina) 
(Orr et al., 2005)














* */