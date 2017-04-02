package org.odfi.indesign.ide.core.regexp

import scala.util.matching.Regex

trait RegexpUtils {

  def lineOfMatch(m: Regex.Match) = {
    m.before.toString.filter {
      c => c == '\n'.toChar
    }.length() + 1
  }

}