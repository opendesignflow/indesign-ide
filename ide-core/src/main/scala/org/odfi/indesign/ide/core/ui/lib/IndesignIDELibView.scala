package org.odfi.indesign.ide.core.ui.lib

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource

trait IndesignIDELibView extends FWAppFrameworkView {

  this.addLibrary("indesign-ide") {

    case (_, targetNode) =>

      getAssetsResolver match {
        case Some(a) =>
          a.ifNoAssetSource("/indesign-ide") {
            a.addAssetsSource("/indesign-ide", new ResourcesAssetSource).addFilesSource("indesign-ide")
          }
        case None =>
      }

      onNode(targetNode) {
        script(createAssetsResolverURI("indesign-ide/js/indesign-ide.js")) {

        }
      }

  }

}