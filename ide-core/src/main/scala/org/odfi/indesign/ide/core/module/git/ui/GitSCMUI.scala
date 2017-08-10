package org.odfi.indesign.ide.core.module.git.ui

import org.odfi.indesign.ide.core.ui.IndesignSiteModule
import org.odfi.indesign.ide.core.ui.contrib.IDEGUIMenuProvider
import org.odfi.wsb.fwapp.assets.AssetsResolver

object GitSCMUI  extends IndesignSiteModule("/git") with IDEGUIMenuProvider {
  
  
  override def getDisplayName = "Git"
  
  // Site
  //--------------
  "/assets" uses new AssetsResolver
  
  view(classOf[GitOverview]) 
}