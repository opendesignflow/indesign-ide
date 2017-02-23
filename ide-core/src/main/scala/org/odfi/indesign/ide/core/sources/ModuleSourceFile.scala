package org.odfi.indesign.ide.core.sources

trait ModuleSourceFile extends SourceFile {
  
  def getDiscoveredModules : List[String]
}