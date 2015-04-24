package org.denigma.utils

import java.util.Date

/**
 * Allows to test Java Date
 */
trait DateFixer {

  def date(year:Int,month:Int,day:Int): Date = {
    new Date(year-1900,month-1,day)
    /*val c = Calendar.getInstance()
    c.set(year,month-1,day)
    new Date(c.getTimeInMillis)*/
  }


}
