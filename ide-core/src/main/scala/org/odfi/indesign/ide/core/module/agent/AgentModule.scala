package org.odfi.indesign.ide.core.module.agent





import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.odfi.indesign.core.module.IndesignModule
import com.idyria.osi.tea.logging.TLog
import org.odfi.indesign.core.artifactresolver.ArtifactResolverModule
import scala.io.Source


class AgentModule {
  
}
object AgentModule extends IndesignModule {
  
  var projectVersion = ""
  
  /**
   * Add Agent
   */
  this.onLoad {
    
    TLog.setLevel(classOf[AgentModule],TLog.Level.FULL)
    
    //-- Require
    this.requireModule(ArtifactResolverModule)
    
    //-- Get project version
    projectVersion = Source.fromURL(getClass.getClassLoader.getResource("org.odfi.indesign.ide.version.txt")).mkString
    
    logInfo[AgentModule]("Project version: "+projectVersion)
    
    
    
    
  }
  
  this.onInit {
    
    //-- Use Aether to find agent
    AetherResolver.getArtifactPath("org.odfi.indesign.ide", "indesign-ide-agent", projectVersion) match {
      case Some(agentFile) => 
        
        logInfo[AgentModule]("Found Agent JAR at: "+agentFile)
        
      case None => 
        
        logWarn[AgentModule]("Could not find Agent Jar")
    }
    
  }
  
}