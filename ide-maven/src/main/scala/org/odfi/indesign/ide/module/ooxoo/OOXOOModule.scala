package org.odfi.indesign.ide.module.ooxoo

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.ide.module.scala.ScalaModule
import org.odfi.indesign.ide.module.scala.ScalaProjectHarvester
import org.odfi.indesign.core.harvest.fs.FSGlobalWatch

object OOXOOModule  extends IndesignModule {
  
  this.onLoad {
    requireModule(FSGlobalWatch)
    requireModule(ScalaModule)

  }
  
  this.onInit {
    
    ScalaProjectHarvester --> OOXOOModelHarvester
    
    
  }
  
  this.onShutdown {
    
    OOXOOModelHarvester.clean
    
  }
  
}