package org.odfi.indesign.ide.core.module.git.ui

import org.odfi.indesign.ide.core.ui.IndesignFrameworkView
import org.odfi.wsb.fwapp.module.jquery.JQueryTreetable
import org.odfi.indesign.ide.core.ui.tasks.TasksView
import org.odfi.indesign.ide.core.ui.utils.ErrorsHelpView
import org.odfi.indesign.ide.www.IDEBaseView
import org.odfi.indesign.module.git.GitHarvester
import org.odfi.indesign.module.git.GitRepository

class GitOverview extends IDEBaseView with JQueryTreetable with TasksView with ErrorsHelpView {
  
  override def getDisplayName = "Git Overview"

  this.placePage {
    
    
    withEmpty(GitHarvester.getResourcesOfType[GitRepository]) {
      case None => 
        
        "ui warning message" :: "No GIT Repositories"
        
      case Some(lst) => 
        
        semanticDefinitionTable("Path", "Branch", "Modified","Untracked") {
          
          tbodyTrLoop(lst) {
            gitp => 
              
              rtd {
                
              }
              td(gitp.path.toString) {
                
              }
              td(gitp.getCurrentBranch.get) {
                
              }
              
              // Status
              //------------
              val status = gitp.git.get.status().call()
              
              // Modified
              rtd {
                status.getModified.size() match {
                  case 0 => 
                    classes("success")
                    text("-")
                  case other => 
                    classes("warning")
                    text(other.toString)
                }
              }
              
              td(status.getUntracked.size().toString) {
                
              }
          }
          
        }
    }
    
    
  }
}