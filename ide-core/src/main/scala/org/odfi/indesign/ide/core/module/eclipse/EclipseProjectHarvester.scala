package org.odfi.indesign.ide.core.module.eclipse

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.nio.file.Path
import com.idyria.osi.ooxoo.db.traits.DBLocalDocumentCache
import java.io.File
import org.odfi.indesign.ide.core.project.ProjectFolder

object EclipseProjectHarvester extends Harvester {
  
  this.onDeliverFor[EclipseWorkspaceFolder] {
    case workspace => 
      
      workspace.listProjects.foreach(gather)
      
      false
  } 
  
}


class EclipseProjectFolder(p:Path) extends ProjectFolder(p) with DBLocalDocumentCache {
  
  
  def getProjectDescription = this.getCachedDocument[projectDescription]("projectDescription",new File(p.toFile(),".project"))
  
  def getProjectName = getProjectDescription match {
    case Some(d) => d.name.toString()
    case None => 
  }
  
}