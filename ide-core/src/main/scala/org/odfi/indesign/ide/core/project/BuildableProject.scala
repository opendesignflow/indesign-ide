package org.odfi.indesign.ide.core.project

import org.odfi.indesign.core.harvest.HarvestedResource
import org.eclipse.aether.artifact.Artifact
import org.odfi.indesign.ide.core.compiler.LiveCompiler
import scala.reflect.ClassTag
import org.odfi.indesign.core.brain.BrainLifecycle
import org.eclipse.aether.graph.Dependency

trait BuildableProject extends Project with BrainLifecycle {
  
  
  // Dependencies
  //--------------------
  def getDependencies : List[Dependency]
  
  /**
   * If the project supports sub projects, get dependencies accross them all
   * Defaults to normal getDependencies
   */
  def getDependenciesAccrossSubProjects = getDependencies
  
  def isArtifact(art:Artifact) : Boolean
  
  // Build Interface
  //----------------------
  
  def isBuildEnabled : Boolean
  def setBuildEnabled(v:Boolean)
  
  
  def buildInvalidateDependencies
  def buildDependencies
  
  /**
   * Remove compiler
   */
  def buildInvalidateCompiler
  
  /**
   * Create compiler
   */
  def buildCompiler
  
  /**
   * Build Prepare is used to detect generated sources folders and such
   */
  def buildPrepare
  
  /**
   * Full build, should always work even if slower
   */
  def buildFully
  
  
  
  /**
   * "Daily life" build
   */
  def buildStandard
  def runBuildStard = this.runSingleTask("build.standard") {
    buildStandard
  }
  
  // Build Watch interface
  //-----------------
  def onBuildOutputChanged(cl: => Any) = {
    this.on("build.output.changed") {
      cl
    }
  }
  
  // Live Compiler
  //-----------------
  def buildInvalidateLiveCompilers = {
    this.liveCompilers.foreach {
      lc => 
        lc.moveToShutdown
        lc.clean
    }
    this.liveCompilers =  List[LiveCompiler]()
  }
  def buildLiveCompilers
  
  var liveCompilers = List[LiveCompiler]()
  
  /**
   * Add live compiler and move to start
   */
  def addLiveCompiler[T <: LiveCompiler](lc:T) =   {
    this.liveCompilers = this.liveCompilers :+ lc
    lc.moveToStart
    lc
  }
  
  def getLiveCompiler[T<:LiveCompiler](implicit tag : ClassTag[T]) = {
    this.liveCompilers.find (lc => tag.runtimeClass.isInstance(lc))
  }
  
}