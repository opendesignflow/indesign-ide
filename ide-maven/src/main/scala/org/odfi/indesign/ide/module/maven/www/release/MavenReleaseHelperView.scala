package org.odfi.indesign.ide.module.maven.www.release

import org.odfi.indesign.ide.www.IDEBaseView

class MavenReleaseHelperView extends IDEBaseView {
   
  override def getDisplayName = "Release Helper"
 
  this.placePage {
    h1("Release Helper for Projects") {
      
    }
  }
  
}