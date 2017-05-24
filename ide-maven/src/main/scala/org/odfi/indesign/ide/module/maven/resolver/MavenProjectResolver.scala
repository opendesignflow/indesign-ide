package org.odfi.indesign.ide.module.maven.resolver

import org.eclipse.aether.repository.WorkspaceReader
import org.eclipse.aether.repository.WorkspaceRepository
import java.io.File
import org.eclipse.aether.artifact.Artifact
import com.idyria.osi.tea.logging.TLogSource

import scala.collection.JavaConversions._
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.indesign.ide.module.maven.project
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.brain.external.FolderOutputBrainRegion
import org.odfi.indesign.core.brain.BrainRegion
import org.odfi.indesign.ide.module.maven.region.MavenExternalBrainRegion
import org.apache.maven.project.MavenProject

trait MavenProjectWorkspaceReader {

}
object MavenProjectWorkspaceReader extends WorkspaceReader with TLogSource with MavenProjectWorkspaceReader {

  //tlogEnableFull[MavenProjectWorkspaceReader]

  val workspaceRepository: WorkspaceRepository = new WorkspaceRepository("indesign-maven")

  var allProjects: Option[List[MavenProjectResource]] = None

  def resetAllProjects = this.synchronized {
    var t = new Throwable
    t.fillInStackTrace()
    var cause = t.getStackTrace()(2)
    //println("Reset from: " + cause.getFileName + ":" + cause.getLineNumber)
    this.allProjects = None
  }

  def getAllProjects: List[MavenProjectResource] = this.synchronized {
    allProjects match {

      case Some(ap) => ap
      case None =>

        // Find Maven
        //----------------------
        var foundMaven = MavenProjectHarvester.getResourcesOfType[MavenProjectResource]

        /*var foundMavenRes = foundMaven.collect {
          case mp if (mp.getMavenModel.isDefined) => mp

        }*/
        // Find MavenRegions
        //----------------------
        var foundMavenRegions = Harvest.collectResourcesOnHarvesters[Brain, MavenExternalBrainRegion, MavenExternalBrainRegion] {
          case mp =>
            // println("Match")
            mp

        }
       /* var foundMavenRegionsRes = foundMavenRegions.collect {
          case mp if (mp.getMavenModel.isDefined) => mp

        }*/

        //var res = foundMavenRes ::: foundMavenRegionsRes
        var res = foundMaven ::: foundMavenRegions
        this.allProjects = Some(res)

        //logWarn[MavenProjectWorkspaceReader]("found all: " + allProjects)
        println("found all: " + allProjects)
        res

    }

  }

  /**
   * Looking for artifact
   */
  def findArtifact(artifact: Artifact): File = {

    logFine[MavenProjectWorkspaceReader]("Looking in Other Maven Projects: " + artifact + "-> " + artifact.getFile)

    val allp = getAllProjects
    allp.find {
      mp =>
        mp.isArtifact(artifact)
    } match {
      case Some(mavenResource) if (artifact.getExtension == "pom" && mavenResource.getMavenModel.isDefined) =>
        logFine[MavenProjectWorkspaceReader]("Found: " + mavenResource.getMavenModel.get.getFile)
        mavenResource.getMavenModel.get.getFile
      case Some(mavenResource) if (artifact.getExtension == "jar" && mavenResource.getMavenModel.isDefined) =>
        var res = new File(mavenResource.getMavenModel.get.getBuild.getOutputDirectory)
        logFine[MavenProjectWorkspaceReader]("Found: " + res)
        res
      case other =>
        null
    }
    
     //null

  }

  def findVersions(artifact: Artifact): java.util.List[String] = {

    getAllProjects.collect {
      case mp if (mp.isArtifact(artifact)) => mp.getVersion
    }.toList

  }

  def getRepository(): WorkspaceRepository = workspaceRepository

}