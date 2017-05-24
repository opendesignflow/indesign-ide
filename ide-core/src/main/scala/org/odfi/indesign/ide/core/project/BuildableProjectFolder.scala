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

class DefaultBuildableProjectFolder(p:Path) extends BuildableProjectFolder(p) {
  
   // Members declared in org.odfi.indesign.ide.core.project.Project
  def getProjectId: String =  getId
  
  
  // Members declared in org.odfi.indesign.ide.core.project.BuildableProject
  def buildCompiler: Unit = {
    
  }
  def buildDependencies: Unit = {
    
  }
  def buildFully: Unit ={
    
  }
  def buildInvalidateCompiler: Unit = {
    
  }
  def buildInvalidateDependencies: Unit ={
    
  }
 
  def buildLiveCompilers: Unit = {
    
  }
  def buildPrepare: Unit = {
    
  }
  def buildStandard: Unit = {
    
  }
  
  def getDependencies: List[org.eclipse.aether.graph.Dependency] = List() 
    
 
  def isArtifact(art: org.eclipse.aether.artifact.Artifact): Boolean = false
  
 

  
  
}