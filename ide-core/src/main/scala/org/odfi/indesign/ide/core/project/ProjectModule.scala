package org.odfi.indesign.ide.core.project

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.harvest.Harvest

object ProjectModule extends IndesignModule {
  
  this.onInit {
    Harvest.addHarvester(ProjectsHarvester)
  }
  
}