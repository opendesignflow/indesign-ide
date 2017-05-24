package org.odfi.indesign.ide.module.scala.compiler

import org.apache.maven.model.Plugin
import org.odfi.indesign.ide.module.maven.compiler.MavenConfiguredLiveCompiler
import org.odfi.indesign.ide.module.maven.project
import org.apache.maven.project.MavenProject
import org.odfi.indesign.ide.module.maven.MavenProjectResource

class ScalaMavenLiveCompiler(project:MavenProjectResource) extends MavenConfiguredLiveCompiler(project) with ScalaLiveCompiler {
  
  def doRunFullBuild = {
    
  }
  
}