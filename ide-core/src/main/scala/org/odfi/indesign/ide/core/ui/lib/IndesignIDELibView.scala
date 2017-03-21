package org.odfi.indesign.ide.core.ui.lib

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView

trait IndesignIDELibView extends FWAppFrameworkView {
   
  this.addLibrary("indesign-ide") {

    case (_, targetNode) =>

      onNode(targetNode) {
        script(createAssetsResolverURI("indesign-ide/js/indesign-ide.js")) {

        }
      }

  }
  
}