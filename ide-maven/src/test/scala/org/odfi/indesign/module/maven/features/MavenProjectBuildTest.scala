package org.odfi.indesign.module.maven.features

import org.scalatest.FunSuite
import org.odfi.indesign.ide.module.maven.MavenModule
import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.ide.module.scala.ScalaModule
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.indesign.core.brain.Brain
import java.io.File
import org.odfi.indesign.ide.core.project.ProjectsHarvester
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile

class MavenProjectBuildTest extends FunSuite {
  
  
  IndesignPlatorm use MavenModule
  IndesignPlatorm use ScalaModule
  
  test("Projects Downstream") {
    
    Brain.moveToStart
    
    // Create Projects
    //-------------
     var topProject = new MavenProjectResource(new HarvestedFile(new File("src/test/testFS/maven-app-dep/").toPath()))
    var dependendProject = new MavenProjectResource(new HarvestedFile(new File("src/test/testFS/maven-app-internaldep/").toPath()))
    
    MavenProjectHarvester.gatherPermanent(topProject)
    MavenProjectHarvester.gatherPermanent(dependendProject)
    
    // Check Maven Projects Status
    //------------
    assertResult(2)(MavenProjectHarvester.getResources.size)
    
    // Find dependencies
    //----------------
    var upStreamProjects = MavenProjectHarvester.findUpstreamProjects(dependendProject)
    assertResult(1)(upStreamProjects.size)
  }
  
  
}