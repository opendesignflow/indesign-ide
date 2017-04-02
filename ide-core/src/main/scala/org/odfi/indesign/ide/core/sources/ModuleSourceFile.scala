package org.odfi.indesign.ide.core.sources

trait ModuleSourceFile extends SourceCodeProvider {
  
  def getDiscoveredModules : List[String]
}