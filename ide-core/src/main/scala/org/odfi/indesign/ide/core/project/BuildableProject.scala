package org.odfi.indesign.ide.core.project

import org.odfi.indesign.core.harvest.HarvestedResource
import org.eclipse.aether.artifact.Artifact
import org.odfi.indesign.ide.core.compiler.LiveCompiler
import scala.reflect.ClassTag
import org.odfi.indesign.core.brain.BrainLifecycle

trait BuildableProject extends HarvestedResource with BrainLifecycle {
  
  
  // Dependencies
  //--------------------
  def getDependencies : List[Artifact]
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
  
  // Live Compiler
  //-----------------
  def buildInvalidateLiveCompilers
  def buildLiveCompilers
  
  var liveCompilers = List[LiveCompiler]()
  
  def addLiveCompiler[T <: LiveCompiler](lc:T) =  this.liveCompilers = this.liveCompilers :+ lc
  
  def getLiveCompiler[T<:LiveCompiler](implicit tag : ClassTag[T]) = {
    this.liveCompilers.find (lc => tag.runtimeClass.isInstance(lc))
  }
  
}