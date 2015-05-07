package org.denigma.semweb.sesame.test.data.schemas

trait Interventions extends BasicSchema{

}

/*
*
*
mouse:Wrn_Mutation rdfs:subClassOf :Loss-of-Function, :Mutation, :Deletion ;
    :of :Wrn ;
    :name "Wrn Mutation" ;
    :description "Mice lacking the helicase domain fo the WRN ortholog exhibit many phenotypic features of Werner Syndrom, including a pro-oxidant status and a shorter mean lifespan. Mice with a deletion in the helicase domain [9789047] recapitulates most of the Werner syndrome phenotypes, including an abnormal hyaluronic acid excretion, higher reactive oxygen species levels, dyslipidemia, increased genomic instability, and cancer incidence. Wrn(Dehl/Dehl) mutant mice have a 10 - 15% decrease in their mean lifespan [12707040; 19741171]." ;
    :organism :Mus_musculus ;
    :changes_mean_lifespan_by 0.1 ;
    :decreases_mean_lifespan_at_least_by 10 ;
    :changes_mean_lifespan_by 0.15 ;
    :decreases_mean_lifespan_up_to 15 ;
    :pmid :12707040, :19741171, :9789047 ;
    rdfs:seeAlso <http://denigma.de/lifespan/intervention/1486> .
* */