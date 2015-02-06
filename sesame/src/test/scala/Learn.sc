import org.scalax.semweb.sparql.Prefix

case class Prefix[Rdf <: RDF](
                               prefixName: String,
                               prefixIri: String)

trait RDFOps[Rdf<:RDF]
{
  val base: Prefix[Bigdata]

}
trait RDF{
}

object Bigdata{

  implicit val ops = new BigdataOps
  implicit lazy val base:Prefix[Bigdata] = Prefix(":","http://example.org")
}
trait Bigdata extends RDF{

}


class BigdataOps(implicit val base: Prefix[Bigdata] ) extends RDFOps[Bigdata]
{
}


class Store[Rdf<:RDF](implicit val ops:RDFOps[Rdf])

val st = new Store[Bigdata]
st.ops.base

