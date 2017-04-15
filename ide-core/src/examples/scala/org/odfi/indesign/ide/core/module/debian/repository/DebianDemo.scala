package org.odfi.indesign.ide.core.module.debian.repository

import java.io.File

object DebianDemo extends App {
  
  val site = new DebianRepository(new File("repository/debian").getCanonicalFile)
  
  site.listen(8585)
  
  site.moveToStart
  
}