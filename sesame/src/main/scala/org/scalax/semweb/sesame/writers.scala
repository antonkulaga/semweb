package org.scalax.semweb.sesame

import org.openrdf.repository.RepositoryConnection
import scala.util.Try
import org.scalax.semweb.commons.Logged


/**
Trait that can provide writeConnection. It is used everywhere where we need to write something into the database
  */
trait CanWriteSesame extends Logged{

  type WriteConnection<:RepositoryConnection

  def writeConnection:WriteConnection
}

/*
interface for data writing
 */
trait SesameDataWriter extends CanWriteSesame{


  def writeConnection: WriteConnection



  /*
 writes something and then closes the connection
  */
  def write[T](action:WriteConnection=>T):Try[T] =
  {
    val con = this.writeConnection
    con.setAutoCommit(false)
    val res = Try {
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error("read/write transaction from database failed because of \n"+e.getMessage)
      res
    }
  }


}
