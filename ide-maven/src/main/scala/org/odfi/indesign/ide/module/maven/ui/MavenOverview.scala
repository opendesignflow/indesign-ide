package org.odfi.indesign.ide.module.maven.ui

import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.apache.maven.project.MavenProject
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.indesign.ide.module.maven.region.MavenExternalBrainRegion
import org.odfi.indesign.ide.module.maven.MavenModule
import com.idyria.osi.tea.compile.ClassDomainContainer
import org.odfi.indesign.core.brain.artifact.ArtifactRegion
import org.odfi.indesign.ide.core.ui.main.IDEBaseView
import org.odfi.wsb.fwapp.module.jquery.JQueryTreetable
import org.odfi.indesign.ide.core.ui.tasks.TasksView
import org.odfi.indesign.ide.core.ui.utils.ErrorsHelpView

class MavenOverview extends IDEBaseView with JQueryTreetable with TasksView with ErrorsHelpView{

  override def getDisplayName = "Maven Overview"

  this.placePage {

    request match {

      case Some(req) if (req.getURLParameter("project").isDefined && MavenProjectHarvester.getResourceById[MavenProjectResource](req.getURLParameter("project").get).isDefined) =>

        val project = MavenProjectHarvester.getResourceById[MavenProjectResource](req.getURLParameter("project").get).get

        a(createCurrentViewLink())(text("Return"))
        ribbonHeaderDiv("green", s"Project: ${project.getDisplayName}") {

          // Rebuild
          //----------------

          // Update Libraries
          //-------
          project.classdomain match {
            case None =>
              "ui warning message" :: text("Libraries and compilation Classpath have not been prepared")
            case Some(cd) =>
          }

          // Generate Sources
          //--------------

        }

      // Default GUI, list of projects
      //-----------------
      case default =>
        //Thread.currentThread().setContextClassLoader(MavenModule.getClass.getClassLoader)
        div {
          h1("Maven Projects Overview") {

          }

          //-- Get Projects
          var projects = MavenProjectHarvester.getResourcesOfType[MavenProjectResource]

          projects.size match {
            case 0 =>
              "ui info message" :: p("No Maven Projects Detected")
            case _ =>
              "ui raised segment" :: div {

                importHTML(<a class="ui blue ribbon label">Maven Projects</a>)
                p("""Please find here a summary of the Detected Maven Projects""")

                "ui table  treetable" :: table {
                  thead("Name", "State", "Actions", "Location")
                  tbody {
                    projects.foreach {
                      p =>

                        //-- Project Line
                        "leaf expanded " :: tr {

                          treeTableLineId(p.getId)

                          //-- Name
                          td("") {
                            a(createCurrentViewLink(("project", p.getId)).toString())(text(p.getDisplayName))
                          }

                          //-- State
                          td("") {

                             errorsStat(p)

                          }
                          //-- Actions
                          td("") {

                            // Build Standard
                            taskButton(p.getId+":build")("Build Full", "Build in Progress") {
                              task => 
                                p.buildFully
                            }
                            
                            label("Enable Build") {
                              input {
                                bindValue {
                                  v: Boolean => p.setBuildEnabled(v)
                                }
                              }
                            }
                            
                          }
                          //-- Locaion
                          td(p.path.toFile().getCanonicalPath) {

                          }

                        }
                        // EOF Project Line

                        //-- Dependencies
                        MavenProjectHarvester.findUpstreamProjects(p) foreach {
                          upstreamProject =>
                            tr {
                              treeTableLineId(s"${p.getId}:${upstreamProject.getId}")
                              treeTableParent(p.getId)
                              
                              td("Depends on: " + upstreamProject.getDisplayName) {

                              }

                              // State
                              td("") {
                               
                                upstreamProject.hasErrors match {
                                  case true =>
                                    classes("negative")
                                    text("Check Build")
                                  case false =>
                                    classes("positive")
                                    "icon checkmark" :: i {

                                    }
                                    text("OK")

                                }
                              }
                              
                              // Actions
                              td("") {
                                
                              }
                              
                              // Location
                              td("") {
                                
                              }

                            }
                        }

                    }

                  }
                }
                // EOF Projects Table

              }
          }

        }
    }

  }

}