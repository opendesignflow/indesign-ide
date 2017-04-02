package org.odfi.indesign.ide.module.maven.www

import org.odfi.indesign.ide.module.scala.ScalaAppSourceFile
import org.odfi.indesign.ide.module.scala.ScalaAppHarvester
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.www.IDEBaseView
import org.odfi.indesign.ide.module.maven.MavenProjectResource


class MavenWWWView (val mavenProject: MavenProjectResource) extends IDEBaseView {

  //override def getUIViewName = mavenProject.projectModel.artifactId
  override def getId = mavenProject.getId


  this.placePage {

    div {
      h1("Maven Project 2") {

      }
      "ui segment" :: div {

        p {
          span {
            textContent("POM File Path: " + mavenProject.pomFile.getAbsolutePath)
          }
        }

      }
      
      "ui segment" :: div {
        importHTML(<a class="ui blue ribbon label">Dependencies</a>)
        
        "ui table" :: table {
          thead {
            
            tr {
              th("Group Id") {
                
              }
              th("Artifact Id") {
                
              }
              th("Version Id") {
                
              }
              th("File") {
                
              }
            }
            
          }
          tbody {
            
            mavenProject.getDependencies.foreach {
              dep => 
                tr {
                  td(dep.getArtifact.getArtifactId) {
                    
                  }
                  td(dep.getArtifact.getGroupId) {
                    
                  }
                  td(dep.getArtifact.getVersion) {
                    
                  }
                  td(dep.getArtifact.getFile.toString()) {
                    
                  }
                }
            }
            
          }
        }
        
      }

      "ui segment" :: div {

        Harvest.onHarvesters[ScalaAppHarvester] {
          case appHarvester if (appHarvester.getResources.size > 0) =>

            println(s"Found Scala App sources: " + appHarvester.getResources)
            appHarvester.onResources[ScalaAppSourceFile] {
              case r if(r.findUpchainResource[MavenProjectResource] == Some(mavenProject)) =>
                
                div {
                  span {
                    textContent("Found Main 1: "+r.getDefinedPackage)
                  }
                  button("Run") {
                    onClick {
                      println(s"**************** Running ****************")
                      r.ensureCompiled
                      r.run
                    }
                  }
                }
                
             
                
              case _ => 
            }

        }

      }
    }

  }

}