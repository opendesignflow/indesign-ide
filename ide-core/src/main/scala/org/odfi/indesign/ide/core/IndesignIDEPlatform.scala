package org.odfi.indesign.ide.core

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import org.odfi.indesign.ide.core.module.intelliJ.IntelliJModule
import org.odfi.indesign.ide.core.project.ProjectsHarvester
import org.odfi.indesign.ide.core.module.agent.AgentModule
import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.ide.www.IDEGUI
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import java.io.File

object IndesignIDEPlatform extends IndesignModule {

  this.onLoad {

    IndesignPlatorm.prepareDefault

    // Developement options
    //--------------
    requireModule(AgentModule)

    // Setup Indesign
    //---------------

    //-- Configuration
    requireModule(OOXOOConfigModule)
    OOXOOConfigModule.setConfigFolder(new File("indesign-ide-config"))
    //Config.setImplementation(new OOXOOFSConfigImplementation(new File("indesign-ide-config")))

    //-- Projects and FS Harvester
    IndesignPlatorm use FileSystemHarvester
    IndesignPlatorm use ProjectsHarvester

    //-- Others
    requireModule(IntelliJModule)
    
    // Load GUi
    IndesignPlatorm use IDEGUI
    IDEGUI.listenWithJMXClose(8400)

  }


  this.onStart {

    // Create FWapp Server
    //---------------

    IndesignPlatorm.stopHarvest
    Harvest.run
    Harvest.run
    Harvest.printHarvesters

  }

}