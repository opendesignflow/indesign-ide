package org.odfi.indesign.ide.core.project

import java.nio.file.Path

abstract class BuildableProjectFolder(p:Path) extends ProjectFolder(p) with BuildableProject {
  
  def isBuildEnabled = config match {
    case Some(config) => 
      config.getBoolean("enabled",false)
      true
    case None => false
  }
  
  def setBuildEnabled(v:Boolean) = config match {
    case Some(config) => 
      config.setBoolean("enabled",v)
    case None =>
  }
  
}