package org.odfi.indesign.ide.core.ui

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.wsb.fwapp.Site

class IndesignSiteModule(p:String) extends Site(p) with IndesignModule {
  
  override def getDisplayName = getClass.getName.replace("$","")
  
}