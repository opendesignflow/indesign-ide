package org.odfi.indesign.ide.module.scala


import org.odfi.indesign.core.brain.BrainRegion

import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.ide.module.scala.ui.ScalaOverview

object ScalaModule extends IndesignModule {

  this.onLoad {

    println("Loading Scala : " + this.getClass.getClassLoader)
  }
  
  this.onInit {
    println("Init Scala : " + this.getClass.getClassLoader)
    
    // Add UI
    

    
    // Register Harvesters
    //---
    MavenProjectHarvester --> ScalaProjectHarvester
    

  }

}

class ScalaSourceFileHarvester extends Harvester {

  this.onDeliverFor[HarvestedFile] {

    case r =>

      r.path.toString.endsWith(".scala") match {
        case true =>

          gather(new ScalaSourceFile(r.path))
          true
        case _ =>
          false
      }
  }

}