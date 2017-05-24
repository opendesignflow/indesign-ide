package org.odfi.indesign.ide.core.ui.powergrid

import org.odfi.wsb.fwapp.views.LibraryView

trait PowerGridView extends LibraryView {
  
  
  this.addLibrary("indesign.ide.powergrid") {

    case (Some(lib), node) =>

      script(createAssetsResolverURI("indesign.ide.powergrid/powergrid.js")) {

      }

    case (_, targetNode) =>

      onNode(targetNode) {

        script(createAssetsResolverURI("indesign-ide/powergrid/powergrid.js")) {

        }
        stylesheet(createAssetsResolverURI("indesign-ide/powergrid/powergrid.css")) {

        }
      }

  }
  
  
}