package org.odfi.indesign.ide.core.ui.main

import org.odfi.indesign.core.module.ui.www.external.SemanticUIView
import com.idyria.osi.vui.html.basic.BasicHTMLView
import org.odfi.wsb.fwapp.views.FWappView

class IDEBaseView extends FWappView {
  
  
  this.viewContent {
    html {
      head {
        
      }
      
      body {
        
        "ui segment" :: div {
          text("IndesignIDE")
        }
        
        div {
          placePart("page-body")
        }
        
        
      }
    }
    
  }
  
}