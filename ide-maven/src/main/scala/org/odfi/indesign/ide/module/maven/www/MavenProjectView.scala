package org.odfi.indesign.ide.module.maven.ui

import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.wsb.fwapp.views.FWAppCatchAllView
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.ide.module.scala.ScalaProjectHarvester
import org.odfi.indesign.ide.module.scala.ScalaAppHarvester
import org.odfi.indesign.ide.module.scala.ScalaAppSourceFile
import org.odfi.indesign.ide.core.ui.utils.ErrorsHelpView
import org.odfi.indesign.ide.www.IDEBaseView

class MavenProjectView extends IDEBaseView with FWAppCatchAllView with ErrorsHelpView {

  this.placePage {
    div {
      h1("Maven Single Project Overview 2") {

      }

      div {
        text(getViewPath)
      }

      div {
        text(getViewSubPath)
      }

      // Create Project ID
      //---------------
      var projectId = getViewSubPath.replaceAll("//+", "/").replace("/", ":")
      MavenProjectHarvester.getResourcesOfType[MavenProjectResource].find { p => p.getProjectId == projectId } match {
        case None =>
          "ui error message" :: s"Project ${projectId} could not be found"
        case Some(project) =>

          "ui info message " :: "Project : " + projectId

          //-- Errors
          project.hasErrors match {
            case true =>
              errorsStat(project)
            case false =>
          }

          project.classdomain match {
            case None =>
              "ui warning message" :: text("Libraries and compilation Classpath have not been prepared")
            case Some(cd) =>

              //-- Look up Main files
              ScalaProjectHarvester.getChildHarvesters[ScalaAppHarvester] match {
                case Some(appHarvesters) =>
                  ul {
                    appHarvesters.foreach {
                      appH =>
                        appH.getResourcesByTypeAndUpchainParent[ScalaAppSourceFile, MavenProjectResource](project).foreach {
                          mainFile =>
                            li {
                              text(mainFile.path.toFile().getCanonicalPath)
                            }
                        }
                    }
                  }

                case None =>

              }

          }
      }
    }
  }
}