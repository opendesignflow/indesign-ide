package org.odfi.indesign.ide.core.project

import org.odfi.indesign.core.harvest.Harvester

/**
 * Trait with utility methods for Harvesters which contain projects
 */
trait ProjectHarvesterTrait extends Harvester {
  
  
  
  def findUpstreamProjects(baseProject:BuildableProject) = {
    
    var deps = baseProject.getDependencies
    
    /*deps.foreach {
      f => 
        println(s"Dep: "+f)
    }*/
    
    // Collect all projects which are in the tested project's dependencies
    this.getResourcesOfType[BuildableProject].collect {
      case project if(project!=baseProject && deps.find {dep => project.isArtifact(dep)}.isDefined) => project 
    }
    
  }
  
  
  
  
}