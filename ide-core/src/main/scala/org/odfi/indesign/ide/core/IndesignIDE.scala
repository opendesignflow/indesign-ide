package org.odfi.indesign.ide.core

import java.io.File

import org.odfi.indesign.core.config.Config
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.ide.core.module.agent.AgentModule
import org.odfi.indesign.ide.core.module.intelliJ.IntelliJModule
import org.odfi.indesign.ide.core.project.ProjectsHarvester
import org.odfi.indesign.ide.core.ui.IDEGUI
import org.odfi.wsb.fwapp.FWappTreeBuilder
import com.idyria.osi.tea.files.FileWatcherAdvanced
import org.odfi.wsb.fwapp.FWappIntermediary
import org.odfi.wsb.fwapp.views.FWAppViewIntermediary

object IndesignIDE extends App with FWappTreeBuilder {

  /*args.foreach {
    arg => 
      println("arg: "+arg)
  }
  
  sys.props.names.toList.sorted.foreach {
    case name => 
      println(s"$name = ${sys.props.get(name).get}")
  }
  println("")
  
  
  AgentModule.moveToStart
  
  Console.readLine()
  sys.exit */

  IndesignPlatorm.prepareDefault
  IndesignPlatorm.stopHarvest

  //TLog.setLevel(classOf[ResourcesAssetSource], TLog.Level.FULL)
  //TLog.setLevel(classOf[Brain], TLog.Level.FULL)
  //TLog.setLevel(classOf[FWappTreeBuilder],TLog.Level.FULL)

  //tlogEnableFull[FileWatcherAdvanced]
  //tlogEnableFull[FWappIntermediary]
  //tlogEnableFull[FWAppViewIntermediary]
  /*
  tlogEnableFull[Brain] 
  tlogEnableFull[FWappIntermediary]
  tlogEnableFull[FWappTreeBuilder]
  tlogEnableFull[Site]
  tlogEnableFull[AssetsResolver]*/
  
 // tlogEnableFull[Site]
  //tlogEnableFull[FWappIntermediary]
  

  // Developement options
  //--------------
  IndesignPlatorm use AgentModule

  // Setup Indesign
  //---------------

  //-- Configuration
  IndesignPlatorm use Config
  IndesignPlatorm use OOXOOConfigModule
  OOXOOConfigModule.configFolder = new File("indesign-ide-config")
  //Config.setImplementation(new OOXOOFSConfigImplementation(new File("indesign-ide-config")))

  //-- Projects and FS Harvester
  IndesignPlatorm use FileSystemHarvester
  IndesignPlatorm use ProjectsHarvester
  
  //-- Others
  IndesignPlatorm use IntelliJModule

  // Create FWapp Server
  //---------------
  IndesignPlatorm use IDEGUI
  IDEGUI.listen(8400)

  // Start
  //-------------
  IndesignPlatorm.start
  Harvest.run
  Harvest.run
  Harvest.printHarvesters
 // sys.exit()

}