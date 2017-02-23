package org.odfi.indesign.ide.core.project

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.config.ConfigSupport
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.nio.file.Path
import java.io.FileNotFoundException
import org.odfi.indesign.core.config.FolderWithConfig

object ProjectsHarvester extends Harvester with ConfigSupport {
  
  
  /*override def doHarvest = {
    
    this.config.get.getKeys("project", "file").foreach {
      projectKey => 
        
        
        
    }
    
    
  }*/
  
  this.onDeliverFor[ProjectFolder] {
    case project if (project.originalHarvester.isDefined) => 
      
      //println("***** PRH Delivered")
      gather(new ProjectFolder(project.path).deriveFrom(project))
      true
    case project => 
      
      //println("***** PRH Delivered")
      gather(new ProjectFolder(project.path).deriveFrom(project))
      true
  }
  
  
  
}

class ProjectFolder(p:Path) extends FolderWithConfig(p) {
  
  p.toFile().exists() match {
    case true => 
    case false => this.addError(new FileNotFoundException(s"Project Folder: ${p.toFile} does not exist" ))
  }
  
  
}