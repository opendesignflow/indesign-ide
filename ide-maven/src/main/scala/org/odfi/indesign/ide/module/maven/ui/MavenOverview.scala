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

class MavenOverview extends IDEBaseView {

  override def getDisplayName = "Maven Overview"

  this.placePage {

    request match {

      case Some(req) if(req.getURLParameter("project").isDefined && MavenProjectHarvester.getResourceById[MavenProjectResource](req.getURLParameter("project").get).isDefined) =>
        
        val project = MavenProjectHarvester.getResourceById[MavenProjectResource](req.getURLParameter("project").get).get
        
        a(createCurrentViewLink())(text("Return"))
        ribbonHeaderDiv("green", s"Project: ${project.getDisplayName}") {
          
        }

      
      // Default GUI
      //-----------------
      case default =>
        //Thread.currentThread().setContextClassLoader(MavenModule.getClass.getClassLoader)
        div {
          h1("Maven Projects Overview") {

          }

          // Harvest.onHarvesters[MavenProjectHarvester

          /*
      //var mavenRegions = Brain.getResourcesOfTypeClass(classOf[MavenExternalBrainRegion])
      var mavenRegions = Brain.getResourcesOfLazyType[MavenExternalBrainRegion]
      mavenRegions.size match {
        case 0 =>
        case _ =>
          "ui raised segment" :: div {

            importHTML(<a class="ui blue ribbon label">Maven External Regions</a>)

            var regionsContainer = mavenRegions.map {
              r => r.asInstanceOf[ArtifactRegion]
            }

            ul {
              regionsContainer.foreach {
                region =>
                  region.rebuildDependencies 
                  li {
                    textContent(region.toString)
                    ul {
                      region.classdomain.get.getURLs.foreach {
                        u =>
                          li {
                            textContent(u.toString)
                          }
                      }
                    }

                  }
              }

            }
          }

      }*/

          //-- Get Projects
          var projects = MavenProjectHarvester.getResourcesOfType[MavenProjectResource]

          projects.size match {
            case 0 =>
              "ui info message" :: p("No Maven Projects Detected")
            case _ =>
              "ui raised segment" :: div {

                importHTML(<a class="ui blue ribbon label">Maven Projects</a>)
                p("""Please find here a summary of the Detected Maven Projects""")

                "ui table datatable" :: table {
                  thead("Name", "State", "Actions", "Location")
                  tbody {
                    projects.foreach {
                      p =>

                        tr {
                          //-- Name
                          td("") {
                            a(createCurrentViewLink(("project",p.getId)).toString())(p.getDisplayName)
                          }

                          //-- State
                          td("") {

                            label("Enable Build") {
                              input {
                                bindValue {
                                  v: Boolean => p.setBuildEnabled(v)
                                }
                              }
                            }

                          }
                          //-- Actions
                          td("") {

                          }
                          //-- Locaion
                          td(p.path.toFile().getCanonicalPath) {

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