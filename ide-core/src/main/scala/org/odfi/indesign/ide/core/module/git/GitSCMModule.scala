package org.odfi.indesign.ide.core.module.git

import org.odfi.indesign.ide.core.plugins.IndesignIDEPlugin
import org.odfi.indesign.ide.core.module.git.ui.GitSCMUI
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.module.git.GitHarvester

object GitSCMModule extends IndesignIDEPlugin {
  
  
  this.onLoad {
    requireModule(GitSCMUI)
    
    FileSystemHarvester --> GitHarvester
  }
}