package org.scalax.semweb.sesame

import org.openrdf.model.{Resource, URI, Value}
import org.openrdf.repository.RepositoryConnection
import org.scalax.semweb.rdf._

trait ConnectionImplicits {

  /**
   * Adds some shortcut functions to connections to getStatements easier
   * @param con
   * @tparam TCon
   */
  implicit class ConnectionImplicit[TCon<:RepositoryConnection](con:TCon){

    def hasSubjectFor(prop:IRI,obj:Value,context:Res): Boolean = con.hasStatement(null,prop,obj,true,context)

    def hasObjectFor(sub:Resource,prop:URI,contexts:Seq[Resource]): Boolean = con.hasStatement(sub,prop,null,true,contexts:_*)
    def hasObjectFor(sub:Res,prop:IRI,contexts:Seq[Res]): Boolean = hasObjectFor(sub:Resource,prop:URI,contexts)



    def subjects(prop:URI,obj:Value,contexts:Seq[Resource]): Seq[Resource] = con.getStatements(null,prop,obj,true,contexts:_*).map(s=>s.getSubject).toList
    def subjects(prop:IRI,obj:RDFValue,contexts:Seq[Res] = List.empty[Res]): Seq[Resource] = this.subjects(prop:URI,obj:Value,contexts.map(r=>r:Resource))

    def objects(sub:Resource,prop:URI,contexts:Seq[Resource]): Seq[Value] = con.getStatements(sub,prop,null,true,contexts:_*).map(s=>s.getObject).toList
    def objects(sub:Res,prop:IRI,contexts:Seq[Res] = List.empty[Res]): Seq[Value] = objects(sub:Resource,prop:URI,contexts.map(r=>r:Resource))

    def resources(sub:Resource,prop:URI,contexts:Seq[Resource]): Seq[Resource] = con.getStatements(sub,prop,null,true,contexts:_*).collect{case st if st.getObject.isInstanceOf[Resource]=>st.getObject.asInstanceOf[Resource]}.toList
    def resources(sub:Res,prop:IRI,contexts:Seq[Res] = List.empty[Res]):Seq[Resource] = resources(sub:Resource,prop:URI,contexts.map(r=>r:Resource))

    def firstRes(sub:Resource,prop:URI,contexts:Seq[Resource]): Option[Resource] =  con.getStatements(sub,prop,null,true,contexts:_*).collectFirst{case st if st.getObject.isInstanceOf[Resource]=>st.getObject.asInstanceOf[Resource]}
    def firstRes(sub:Res,prop:IRI,contexts:Seq[Res] = List.empty[Res]):Option[Resource] =  this.firstRes(sub:Resource,prop:URI,contexts.map(r=>r:Resource))

    def uris(sub:Resource,prop:URI,contexts:Seq[Resource]): Seq[URI] = con.getStatements(sub,prop,null,true,contexts:_*).collect{case st if st.getObject.isInstanceOf[URI]=>st.getObject.asInstanceOf[URI]}.toList
    def uris(sub:Res,prop:IRI,contexts:Seq[Res] = List.empty[Res]): Seq[URI]  = uris(sub:Resource,prop:URI,contexts.map(r=>r:Resource))

    def firstURI(sub:Resource,prop:URI,contexts:Seq[Resource]): Option[URI] =  con.getStatements(sub,prop,null,true,contexts:_*).collectFirst{case st if st.getObject.isInstanceOf[URI]=>st.getObject.asInstanceOf[URI]}
    def firstURI(sub:Res,prop:IRI,contexts:Seq[Res] = List.empty[Res]): Option[URI] =  this.firstURI(sub:Resource,prop:URI,contexts.map(r=>r:Resource))
  }
}
