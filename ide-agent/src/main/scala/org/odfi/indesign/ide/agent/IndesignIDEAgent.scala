package org.odfi.indesign.ide.agent

import java.lang.instrument.Instrumentation
import java.lang.management.ManagementFactory
import org.odfi.indesign.ide.agent.reload.IndesignClassReloader
import java.io.File
import imported.com.idyria.osi.tea.logging.TLogSource
import imported.com.idyria.osi.tea.logging.TLog
import imported.com.idyria.osi.tea.listeners.ListeningSupport

class IndesignIDEAgent {
  
}
object IndesignIDEAgent extends TLogSource with ListeningSupport {
  
  
  TLog.setLevel(classOf[IndesignIDEAgent], TLog.Level.FULL)
  
  var mainClass = ""
  var jrePath = ""
  var startTime = 0L
  
  var _restartRequired = false
  
  
  def restartRequired = {
    this._restartRequired = true
    this.@->("restart")
  }
  
  def startRemoteInterface = {
    
  }
  
  def agentmain( agentArgs : String ,  inst : Instrumentation) = {
    
    startTime = System.nanoTime()
    
    
    logInfo[IndesignIDEAgent](s"Hello From Agent 2");
    logInfo[IndesignIDEAgent](s"Start Time: "+startTime);
    logInfo[IndesignIDEAgent]("Can agent redefine classes: "+inst.isRedefineClassesSupported())
    logInfo[IndesignIDEAgent](s"Started: "+ManagementFactory.getRuntimeMXBean.getStartTime)
    
    
    //-- Get JRE Path
    jrePath = sys.props("java.home")
    logInfo[IndesignIDEAgent](s"JRE: $jrePath")
    
    //-- Get main class
    mainClass = sys.props(" sun.java.command")
    logInfo[IndesignIDEAgent](s"Main: $jrePath")
    
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