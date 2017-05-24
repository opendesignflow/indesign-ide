package org.odfi.indesign.ide.module.maven.compiler

import org.apache.maven.project.MavenProject
import org.odfi.indesign.ide.core.compiler.LiveCompiler
import org.odfi.indesign.ide.module.maven.MavenProjectResource

abstract class MavenConfiguredLiveCompiler(var project:MavenProjectResource) extends LiveCompiler {
 
  
  def triggerProjectUpdated = {
    this.@->("project.updated")
    this.moveToShutdown
    this.moveToStart
  }
  
  def onProjectUpdated(cl: => Any) = {
    this.on("project.updated") {
      cl
    }
  }
  
}