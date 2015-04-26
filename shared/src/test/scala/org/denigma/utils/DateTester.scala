package org.denigma.utils

import java.util.Date
import utest.framework.TestSuite

trait DateTester {
  self:TestSuite=>

  def testDate(date:Date,year:Int,month:Int,day:Int) = {
    assert(date.getYear + 1900==year)
    assert(date.getMonth + 1==month)
    assert(date.getDate == day)
  }

}
