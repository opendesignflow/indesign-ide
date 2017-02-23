package org.odfi.indesign.ide.core

import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.wsb.fwapp.FWappTreeBuilder
import org.odfi.indesign.core.config.Config
import org.odfi.indesign.core.config.ooxoo.OOXOOFSConfigImplementation
import java.io.File
import org.odfi.indesign.ide.core.module.agent.AgentModule
import org.odfi.indesign.ide.core.services.stash.StashTree
import org.odfi.indesign.ide.core.ui.main.WelcomeIDEView
import org.odfi.wsb.fwapp.FWappApp
import org.odfi.indesign.ide.core.ui.projects.ProjectsListView
import org.odfi.indesign.ide.core.project.ProjectsHarvester
import org.odfi.wsb.fwapp.assets.AssetsResolver
import com.idyria.osi.tea.logging.TLog
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource
import org.odfi.indesign.ide.core.module.agent.ui.AgentUI
import org.odfi.indesign.ide.core.ui.plugins.PluginsListView
import org.odfi.indesign.core.brain.Brain
import org.odfi.wsb.fwapp.errors.Handle404
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.brain.BrainRegion
import org.odfi.indesign.ide.core.ui.main.IDEBaseView
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.ide.core.ui.main.FSConfigView
import org.odfi.wsb.fwapp.assets.generator.AssetsGenerator
import org.odfi.wsb.fwapp.FWappIntermediary
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import org.odfi.indesign.ide.core.ui.IDEGUI
import org.odfi.wsb.fwapp.Site
import org.odfi.indesign.ide.core.module.intelliJ.IntelliJModule

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
  /* var server = new FWappApp {
    
  }
  server.listen(8400)

  IndesignPlatorm use server

  // App Map
  //-------------
  val websiteTree = server :: "/" is {

    //-- Site
    "/site" is {

      view(classOf[WelcomeIDEView])

      "/sources" view (classOf[FSConfigView])
      "/projects" view (classOf[ProjectsListView])

      "/plugins" is {
        view(classOf[PluginsListView])

        //-- Populate brain regions which have some views
        withCurrentIntermediary {
          pluginsIntermdiary =>

            //-- Clear all intermediaries of 
            pluginsIntermdiary.intermediaries.clear()

            Harvest.onHarvestDone {
              //-- Clear all intermediaries of 
              pluginsIntermdiary.intermediaries.clear()
              Brain.walkResourcesOfType[BrainRegion] {
                region =>
                  println("Walked found region: " + region)
                  region.getDerivedResources[IDEBaseView].foreach {
                    pluginView =>

                      onIntermediary(pluginsIntermdiary) {

                        s"/${region.getDisplayName}/${pluginView.getDisplayName}" view (pluginView.getClass)

                      }

                      println(s"Found view: ${region.getDisplayName} -> " + pluginView.getDisplayName)
                  }
              }
            }
        }

      }

      "/agent" view (classOf[AgentUI])

      // Assets
      //----------------------
      println(s"Adding assets resolver")
      val assetsResolver = ("/assets" uses new AssetsResolver) 
      assetsResolver is {
        
        "/generator" uses new AssetsGenerator
        
      }
      
      println("Assets resolver:"+assetsResolver.fwappIntermediary.intermediaries.size)
      

      //-- Indesign
      AssetsManager.addAssetsSource("indesign-ide", new ResourcesAssetSource("/")).addFilesSource("indesign-ide")

      //-- Semantic
      AssetsManager.addAssetsSource("semantic", new ResourcesAssetSource("/")).addFilesSource("indesign-ide/Semantic-UI-CSS-master")

    }

    //-- Stash features
    "/stash" uses new StashTree

    //-- Errors handlers and such
    add404Intermediary
    //this.fwappIntermediariesStack.head.fwappIntermediary <= new Handle404
  }

  IndesignPlatorm.start
  Harvest.run
  println(s"Found: " + websiteTree.fwappIntermediary.findIntermediaryForPath("/site/plugins"))
  // sys.exit()*/

}