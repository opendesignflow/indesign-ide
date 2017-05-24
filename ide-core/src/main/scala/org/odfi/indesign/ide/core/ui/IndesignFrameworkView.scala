package org.odfi.indesign.ide.core.ui

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView

trait IndesignFrameworkView extends FWAppFrameworkView {
  
  this.addLibrary("indesign-ide") {
    case (_,target) => 
      
      onNode(target) {
        
        script(createAssetsResolverURI("/indesign-ide/js/indesign-ide.js")) {
          
        }
        stylesheet(createAssetsResolverURI("/indesign-ide/css/indesign-ide.css")) {
          
        }
        
      }
      
  }
}