package org.odfi.indesign.ide.core.compiler

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.indesign.core.brain.LFCDefinition
import org.odfi.indesign.core.brain.BrainLifecycle

trait LiveCompiler extends HarvestedResource with BrainLifecycle   {
  
  def getId = getClass.getCanonicalName+"@"+hashCode()

  
  // LFC
  //-----------
   this.onClean {
     this.moveToShutdown 
  }
  
  // Manual Triggers
  //------------------
  def doRunFullBuild
  def runFullBuild = {
    
    
    //-- Make sure started
    this.moveToStart
    
    //-- Reset errors
    resetErrorsOfType[LiveCompilerError]
    keepErrorsOn(this) {
      try {
      doRunFullBuild
      } catch {
        case e : Throwable => throw new LiveCompilerError(e)
      }
    }
  }
  
  
}

