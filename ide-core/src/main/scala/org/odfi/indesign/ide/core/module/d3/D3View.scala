package org.odfi.indesign.ide.core.module.d3

import org.odfi.indesign.ide.core.ui.lib.IndesignIDELibView

trait D3View extends IndesignIDELibView {

  var d3Modules = List("d3")

  this.addLibrary("d3") {
    case (_, targetNode) =>

      onNode(targetNode) {

        d3Modules.foreach {
          name =>
            script(createAssetsResolverURI(s"/indesign-ide/external/node/node_modules/$name/build/$name.min.js")) {

            }
        }

        /*script(createAssetsResolverURI("/indesign-ide/external/node/node_modules/d3-transition/build/d3.min.js")) {
          
        }
        script(createAssetsResolverURI("/indesign-ide/external/node/node_modules/d3/build/d3.min.js")) {
          
        }*/

      }

  }

  def d3UseZoom = {
    d3Modules = d3Modules :+ "d3-zoom"
  }

}