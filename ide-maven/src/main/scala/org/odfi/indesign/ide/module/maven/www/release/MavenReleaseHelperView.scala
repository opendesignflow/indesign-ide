package org.odfi.indesign.ide.module.maven.www.release

import org.odfi.indesign.ide.www.IDEBaseView
import org.odfi.wsb.fwapp.lib.markdown.MarkdownView
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.ide.module.maven.MavenProjectResource

class MavenReleaseHelperView extends IDEBaseView with MarkdownView {

  override def getDisplayName = "Release Helper"

  this.placePage {
    h1("Release Helper for Projects") {

    }

    markdown("""|
                |Use the table to perform simple Git Flow Release
                |
                |""".stripMargin)

    // Table
    //------------
    withEmpty(MavenProjectHarvester.getResourcesOfType[MavenProjectResource]) {
      case None =>
        "ui warning message" :: "No Projects Defined"
      case Some(projects) =>

        table {
          thead("Project", "Version", "Snapshot Free", "Git Flow Enabled", "Perform Release")
          
          trLoop(projects) {
            project => 
              
              td(project.getName) {
                
              }
              
              td(project.getVersion) {
                
              }
              
              rtd {
                project.hasSnapshotDependencies match {
                  case true => 
                    "ui error message" :: "Remove Snapshot Dependencies"
                  case false => 
                    "ui success message" :: "No Snapshots"
                }
                
              }
              
              td("") {
                
              }
              
              rtd {
                
              }
          }
          
        }

    }

  }

}