package org.odfi.indesign.ide.agent

import java.lang.instrument.Instrumentation
import java.lang.management.ManagementFactory
import org.odfi.indesign.ide.agent.reload.IndesignClassReloader
import java.io.File

object IndesignIDEAgent {
  
  
  def agentmain( agentArgs : String ,  inst : Instrumentation) = {
    
    println(s"Hello From Agent 2");
    println("Can agent redefine classes: "+inst.isRedefineClassesSupported())
    
    println(s"Started: "+ManagementFactory.getRuntimeMXBean.getStartTime)
    
    
    var runFolder = new File("").getCanonicalFile
    
    //-- Create Class Reloader
     var reloader = new IndesignClassReloader(inst)
     
    //-- Handle different build systems but maven for now
    new File(runFolder,"target/classes") match {
      case f if (f.exists()) => 
        reloader.watchFolder(f.getCanonicalFile)
      case f => 
    }
    
    new File(runFolder,"target/test-classes") match {
      case f if (f.exists()) => 
        reloader.watchFolder(f.getCanonicalFile)
      case f => 
    }
    
    
    println("Running from: "+runFolder)
    
    
    
   
    
    
    //Thread.sleep(2000)
    //println(s"Killing you")
    //sys.exit()
    
  }
  
  
  
}