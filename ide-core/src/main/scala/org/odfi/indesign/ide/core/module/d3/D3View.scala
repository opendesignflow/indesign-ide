package org.odfi.indesign.ide.core.module.d3

import org.odfi.indesign.ide.core.ui.lib.IndesignIDELibView

trait D3View extends IndesignIDELibView {
  
  
  this.addLibrary("d3") {
    case (_,targetNode) => 
      
      onNode(targetNode) {
        
        List("d3","d3-transition").foreach {
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
}