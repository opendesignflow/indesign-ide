package org.odfi.indesign.ide.core.sources

import org.odfi.indesign.core.harvest.fs.HarvestedFile

trait SourceFile extends HarvestedFile {
  
   def onChange(cl: => Unit) : Unit
  
}

trait SourceContent {
  
}

trait JavaSourceFile extends SourceFile {
  
  def ensureCompiled
  def loadClass: Class[_]
}