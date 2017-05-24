package org.odfi.indesign.ide.core.module.eclipse

import java.nio.file.Path

import scala.sys.process._

import com.idyria.osi.tea.os.OSDetector

import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.config.ConfigSupport
import java.io.File
import com.idyria.osi.tea.io.TeaIOUtils
import java.io.FileInputStream
import java.net.URL
import org.odfi.indesign.ide.core.project.ProjectsHarvester

object EclipseModule extends IndesignModule {

  this.onInit {
    Harvest.addHarvester(EclipseWorkspaceHarvester)
    
    EclipseProjectHarvester --> ProjectsHarvester
  }

}

object EclipseWorkspaceHarvester extends Harvester with ConfigSupport {

  this.addChildHarvester(EclipseProjectHarvester)

  override def doHarvest = {

    // Sources
    this.config match {
      case Some(config) =>
        config.getKeys("workspace", "file").foreach {
          source =>
            var folder = new HarvestedFile(new File(source.values(0).toString).getCanonicalFile.toPath())
            this.deliver(folder)
          /*if (folder.isDirectory && folder.hasSubFile(".metadata", "version.ini").isDefined) {
          
        }*/
        }
      case None =>
    }

    /*var sources = (EclipseModule.getDerivedResources[HarvestedFile] ::: this.getResourcesOfExactType[HarvestedFile])
    sources.foreach {
      case folder if (folder.isDirectory && folder.hasSubFile(".metadata", "version.ini").isDefined) =>
        this.deliver(folder)
      case other => 
    }*/

  }

  this.onDeliverFor[HarvestedFile] {
    case folder if (folder.isDirectory && folder.hasSubFile(".metadata", "version.ini").isDefined) =>

      // Got Workspace
      var ws = new EclipseWorkspaceFolder(folder.path)
      ws.deriveFrom(folder)
      this.gather(ws)

      logFine(s"****(Eclipse) Eclipse workspace " + folder)

      ws.onGathered {
        case h if (h == this) =>
          // Add to aether resolver if opened
          // The lock file must be found by lsof
          ws.hasSubFile(".metadata", ".lock") match {

            case Some(lockFile) =>

              //println(s"****(Eclipse) Eclipse workspace is open, check lock: " + lockFile)
              OSDetector.getOS match {
                case OSDetector.OS.LINUX =>
                  var p = Process(Seq("lsof", "-l", "+D", lockFile.getParentFile.getAbsolutePath))
                  p.lineStream_!.find(l => l.contains(lockFile.getAbsolutePath)) match {
                    case Some(line) =>
                      println(s"OK; using")
                    //AetherResolver.session.setWorkspaceReader(new EclipseWorkspaceReader(ws.path.toFile()))
                    case None =>
                  }
                case _ =>
              }
            case None =>
          }
      }

      true
  }

}

class EclipseWorkspaceFolder(p: Path) extends HarvestedFile(p) {

  val projectsLocation = new File(p.toFile(), ".metadata/.plugins/org.eclipse.core.resources/.projects")

  // List projects
  def listProjects = projectsLocation.listFiles().filter(folder => new File(folder, ".location").exists()).map(new File(_, ".location")).map {
    location =>

      //-- Get source (file is binary, so search for delimiters)
      var locationContent = TeaIOUtils.swallow(new FileInputStream(location))
      var uri = locationContent.toList.dropWhile { _ != 'U'.toByte }.takeWhile { b => b != 0x00 }.map(_.toChar).mkString

      //locationContent.
      //var locationContent = scala.io.Source.fromFile(location, "UTF-8").mkString
      var search = """URI//([\w :/_.-]+)""".r

      //-- Find URI
      search.findFirstMatchIn(uri) match {
        case Some(result) =>
          logFine(s"Found lcoation: " + result.group(1))
          Some(new File(new URL(result.group(1)).getFile))
        case None =>
          logFine(s"Location not  found for : " + location)
          None
      }

  }.filter(_ != None).map { folder => new EclipseProjectFolder(folder.get.toPath).deriveFrom(this) }

}