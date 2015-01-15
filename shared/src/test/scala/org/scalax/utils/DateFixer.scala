package org.scalax.utils

import java.util.{Date, Calendar}

/**
 * Allows to test Java Date
 */
trait DateFixer {

  def date(year:Int,month:Int,date:Int): Date = {
    val c = Calendar.getInstance()
    c.set(year,month-1,date)
    new Date(c.getTimeInMillis)
  }


}
