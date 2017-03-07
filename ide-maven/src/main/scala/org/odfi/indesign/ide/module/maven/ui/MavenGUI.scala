package org.odfi.indesign.ide.module.maven.ui

import org.odfi.wsb.fwapp.Site
import org.odfi.indesign.ide.core.ui.IndesignSiteModule
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.wsb.fwapp.assets.AssetsResolver
import org.odfi.indesign.ide.core.ui.contrib.IDEGUIMenuProvider

object MavenGUI extends IndesignSiteModule("/maven") with IDEGUIMenuProvider {
 
  override def getDisplayName = "Maven"
  
  // Gui Providing
  //-------------------
  override def getMenuLinks= {
    MavenProjectHarvester.getResourcesOfType[MavenProjectResource].map {
      mp => 
      (mp.getDisplayName, s"/${MavenGUI.fullURLPath}/projects/${mp.getGroupId}/${mp.getArtifactId}/${mp.getVersion}")
    }
  }
  
  // Site
  //--------------
  view(classOf[MavenOverview])  
  
  "/assets" uses new AssetsResolver
  
  "projects" is {  
    
    view(classOf[MavenProjectView])
    /*withCurrentIntermediary {
      i =>

        Harvest.onHarvestDone {
          i.intermediaries.clear()
          MavenProjectHarvester.getResourcesOfType[MavenProjectResource].foreach {
            mp => 
              onIntermediary(i) {
                
                
                s"/${mp.getGroupId}/${mp.getArtifactId}/${mp.getVersion}" is {
                  //view(new MavenProjectView(mp))
                }
                
              }
          }
        }

    }*/

  }

}