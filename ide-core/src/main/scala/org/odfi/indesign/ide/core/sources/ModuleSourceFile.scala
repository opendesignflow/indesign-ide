package org.odfi.indesign.ide.core.sources

trait ModuleSourceFile extends CodeSource {
  
  def getDiscoveredModules : List[String]
}