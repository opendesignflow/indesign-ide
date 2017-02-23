package org.odfi.indesign.ide.core.module.agent.ui

import org.odfi.indesign.ide.core.ui.main.IDEBaseView
import javax.swing.JFileChooser
import javax.swing.JFrame
import org.odfi.indesign.ide.core.module.eclipse.EclipseWorkspaceHarvester
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.core.module.eclipse.EclipseWorkspaceFolder
import org.odfi.indesign.ide.core.module.eclipse.EclipseProjectHarvester
import org.odfi.indesign.ide.core.module.eclipse.EclipseProjectFolder

class AgentUI extends IDEBaseView {

  placePage {
    div {

      ribbonHeaderDiv("blue", "Eclipse Workspace") {

        var workspaces = EclipseWorkspaceHarvester.getResourcesOfType[EclipseWorkspaceFolder]
        
        
        workspaces.size match {
          case 0 =>
            "ui warning message" :: "No Eclipse Workspace Set"
          case other =>
            "ui success message" :: "Eclipse Workspace set"
            
            //-- Show Table of projects
            "ui table" :: table {
              
              tr {
                th("Project Name") {
                  
                }
                th("Location") {
                  
                }
                th("Status") {
                  
                }
              }
              
              // Group projects by workspace
              var projectsByWorkspace = EclipseProjectHarvester.getResourcesOfType[EclipseProjectFolder].groupBy( ep => ep.parentResource.get)
              projectsByWorkspace.foreach {
                case (ws : EclipseWorkspaceFolder,projects) => 
                  
                  //-- Workspace name
                  trtd(ws.path.toFile().getCanonicalPath) {
                     colspan(3)
                  }
                 
                  //-- Projects
                  
                    projects.foreach {
                      projectFolder => 
                        trvalues(projectFolder.getProjectName,projectFolder.path.toFile().getCanonicalPath,"Open") 
                    
                  }
                  
                case other => 
              }
            }
            
        }

        isLocalRequestWithDisplay match {
          case true =>
            "ui button" :: button("Select Directory") {
              onClick {
                println(s"Action called")
                
                var frame = new JFrame
                frame.setVisible(true)
                var fc = new JFileChooser
                fc.setDialogType(JFileChooser.OPEN_DIALOG)
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showOpenDialog(frame) match {
                  case JFileChooser.APPROVE_OPTION =>
                    
                    //-- take file and add to config
                    var eclipseDirectory = fc.getSelectedFile
                    EclipseWorkspaceHarvester.config.get.addKey("workspace", "file").values.add.set(eclipseDirectory.getCanonicalPath)
                    EclipseWorkspaceHarvester.config.get.resyncToFile
                    Harvest.run
                    
                  case other => 
                }
                frame.dispose()
              }
            }
          case false =>
        }

      }

    }
  }
}