package org.odfi.indesign.ide.core.ui

import org.odfi.wsb.fwapp.Site
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.ide.core.ui.projects.ProjectsListView
import org.odfi.indesign.ide.core.ui.plugins.PluginsListView
import org.odfi.indesign.ide.core.ui.main.IDEBaseView
import org.odfi.indesign.ide.core.ui.main.WelcomeIDEView
import org.odfi.indesign.core.brain.BrainRegion
import org.odfi.wsb.fwapp.assets.AssetsResolver
import org.odfi.indesign.ide.core.services.stash.StashTree
import org.odfi.indesign.ide.core.ui.main.FSConfigView
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.core.module.agent.ui.AgentUI
import org.odfi.wsb.fwapp.assets.generator.AssetsGenerator
import org.odfi.indesign.core.harvest.HarvestedResource

trait IDEGUI extends HarvestedResource {
  
}
object IDEGUI extends Site("/ide") with IDEGUI {

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
    val assetsResolver =  new AssetsResolver
    ("/assets" uses assetsResolver)
    assetsResolver is {

      "/generator" uses new AssetsGenerator

    }
    assetsResolver <= new ResourcesAssetSource("/")

    println("Assets resolver:" + assetsResolver.fwappIntermediary.intermediaries.size)

    //-- Indesign
    assetsResolver.addAssetsSource("/indesign-ide",new ResourcesAssetSource).addFilesSource("indesign-ide")
    
    //AssetsManager.addAssetsSource("indesign-ide", new ResourcesAssetSource("/")).addFilesSource("indesign-ide")

    //-- Semantic
    assetsResolver.addAssetsSource("/semantic",new ResourcesAssetSource).addFilesSource("indesign-ide/Semantic-UI-CSS-master")
    //AssetsManager.addAssetsSource("semantic", new ResourcesAssetSource("/")).addFilesSource("indesign-ide/Semantic-UI-CSS-master")

  }

  //-- Stash features
  "/stash" uses new StashTree

  //-- Errors handlers and such
  add404Intermediary
  //this.fwappIntermediariesStack.head.fwappIntermediary <= new Handle404

  // LFC
  //--------------

}