package org.odfi.indesign.ide.module.maven

import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.io.File
import org.odfi.indesign.core.module.lucene.LuceneIndexProvider
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.core.project.ProjectHarvesterTrait

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
    

      println(s"Delivered Maven Project ")
      var mp = new MavenProjectResource(r.path.toFile.getParentFile.getCanonicalFile.toPath)
      gather(mp.deriveFrom(r))
      mp.onGathered {
        case h if (h == this) =>
         // WWWViewHarvester.deliverDirect(mp.view)
      }
      true
    case r if (new File(r.path.toFile(), "pom.xml").exists()) =>

      var pomFile = new File(r.path.toFile(), "pom.xml")

      println(s"Delivered Maven Project : "+r.path.toFile())
      var mp = new MavenProjectResource(r.path)
      gather(mp.deriveFrom(r))
      mp.onGathered {
        case h if (h == this) =>
         /// WWWViewHarvester.deliverDirect(mp.view)
      }

      true
    case other => 
      
      println("Maven ignored file: "+other)
      false

  }

  //Harvest.updateAutoHarvesterOn(this)

}
