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

import org.odfi.wsb.fwapp.module.jquery.JQueryTreetable
import org.odfi.indesign.ide.core.ui.tasks.TasksView
import org.odfi.indesign.ide.core.ui.utils.ErrorsHelpView
import org.odfi.indesign.ide.module.scala.ScalaProjectHarvester
import org.odfi.indesign.ide.module.scala.ScalaAppHarvester
import org.odfi.indesign.ide.module.scala.ScalaAppSourceFile
import org.odfi.indesign.ide.www.IDEBaseView

class MavenOverview extends IDEBaseView with JQueryTreetable with TasksView with ErrorsHelpView {

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

              //-- Look up Main files
              ScalaProjectHarvester.getChildHarvesters[ScalaAppHarvester] match {
                case Some(appHarvesters) =>
                  ul {
                    appHarvesters.foreach {
                      appH =>
                        appH.getResourcesByTypeAndUpchainParent[ScalaAppSourceFile,MavenProjectResource](project).foreach {
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

          // Generate Sources
          //--------------

        }

      // Default GUI, list of projects
      //-----------------
      case default =>
        //Thread.currentThread().setContextClassLoader(MavenModule.getClass.getClassLoader)
        div {

          h2("Maven Projects Overview") {

          }

          //-- Get Projects
          //-------------------
          var projects = MavenProjectHarvester.getResourcesOfType[MavenProjectResource]

          projects.size match {
            case 0 =>
              "ui info message" :: p(s"""
                
                
                No Maven Projects Detected
                
                To Add a project, you could add a Source ${a("@/site/sources")(text("here"))}
                
                """)
            case _ =>

              // Projects Menu
              //----------
              "ui menu" :: div {
                projects.foreach {
                  p =>

                    "item" :: a("#")(text(p.getDisplayName))

                }
              }

              "ui raised segment" :: div {

                importHTML(<a class="ui blue ribbon label">Maven Projects</a>)
                p("""Please find here a summary of the Detected Maven Projects""")

                "ui table  treetable" :: table {
                  thead("Name", "State", "Live Builders", "Actions", "Location")
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

                          //-- Live Builders
                          td("") {

                            p.liveCompilers.foreach {
                              lc =>
                                "ui button" :: button(lc.getDisplayName) {

                                }
                            }

                            resourceTaskButton(p, "livecompilers.refresh")("Refresh", "Running...") {
                              task =>
                                p.buildLiveCompilers
                            }
                            //taskButton((p.getId+":build")

                          }

                          //-- Actions
                          td("") {

                            // Build Standard
                            taskButton(p.getId + ":build")("Build Full", "Build in Progress") {
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