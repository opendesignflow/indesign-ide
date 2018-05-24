package org.odfi.indesign.ide.module.maven.www

import org.odfi.indesign.ide.core.ui.IndesignSiteModule
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.wsb.fwapp.assets.AssetsResolver
import org.odfi.indesign.ide.core.ui.contrib.IDEGUIMenuProvider
import org.odfi.indesign.ide.module.maven.ui.MavenOverview
import org.odfi.indesign.ide.module.maven.ui.MavenProjectView
import org.odfi.indesign.ide.module.maven.www.release.MavenReleaseHelperView

object MavenGUI extends IndesignSiteModule("/maven") with IDEGUIMenuProvider {
 
  override def getDisplayName = "Maven"
  
  // Gui Providing
  //-------------------
  override def getMenuLinks= {
    
    val projects =  List( ("Release Helper", s"//release/help") ) ::: MavenProjectHarvester.getResourcesOfType[MavenProjectResource].map {
      mp => 
      (mp.getDisplayName, s"/${MavenGUI.fullURLPath}/projects/${mp.getGroupId}/${mp.getArtifactId}/${mp.getVersion}")
    }.toList
     
    projects
  }
  
  // Site
  //--------------
  view(classOf[MavenOverview])  
  
  "/assets" uses new AssetsResolver
  
  "/release" is {
    
    "/help" is {
      view(classOf[MavenReleaseHelperView])
    }
  }
  
  "/projects" is {  
    
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