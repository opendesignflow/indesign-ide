package org.odfi.indesign.ide.module.maven

import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.io.File
import org.odfi.indesign.core.module.lucene.LuceneIndexProvider
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.core.project.ProjectHarvesterTrait
import org.odfi.indesign.ide.module.maven.resolver.MavenProjectWorkspaceReader
import org.odfi.indesign.core.artifactresolver.AetherResolver

object MavenProjectHarvester extends FileSystemHarvester with LuceneIndexProvider with ProjectHarvesterTrait {
 
  //this.addChildHarvester(new POMFileHarvester)

  override def doHarvest = {
   
    //println(s"Running Do Harvest on Maven havester, resources: " + this.getResources)
    //println("Testing for double resources")
    /*this.getResources.groupBy { r => r.getId }.foreach {
      case (id,all) if (all.size>1) => 
        println("Found double: "+id)
      case _ => 
    }*/
    super.doHarvest
  }

  /**
   * If the Fiel is a folder, and has a POM,xml in it -> go
   */
  this.onDeliverFor[HarvestedFile] {
    case r if (r.path.toFile().getName == "pom.xml") =>
    

      //println(s"Delivered Maven Project ")
      gather(new MavenProjectResource(new HarvestedFile(r.path.toFile().getParentFile.toPath())))
      /*mp.onGathered {
        case h if (h == this) =>
         // WWWViewHarvester.deliverDirect(mp.view)
      }*/
      true
    case r if (new File(r.path.toFile(), "pom.xml").exists()) =>

      var pomFile = new File(r.path.toFile(), "pom.xml")

      //println(s"Delivered Maven Project : "+r.path.toFile())
      gather(new MavenProjectResource(r))
      /*mp.onGathered {
        case h if (h == this) =>
         /// WWWViewHarvester.deliverDirect(mp.view)
      }*/

      true
      
   
    case other => 
      
      //println("Maven ignored file: "+other)
      false

  }
  
  this.onGatheredResources {
    res => 
      MavenProjectWorkspaceReader.resetAllProjects
  }
  
  this.onRemovedResources {
    res => 
      MavenProjectWorkspaceReader.resetAllProjects
  }
  
  
  this.onClean {
    AetherResolver.session.setWorkspaceReader(null)
  }
  
  

  //Harvest.updateAutoHarvesterOn(this)

}
