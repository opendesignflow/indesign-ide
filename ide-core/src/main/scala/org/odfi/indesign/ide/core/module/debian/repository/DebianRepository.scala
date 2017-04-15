package org.odfi.indesign.ide.core.module.debian.repository

import java.io.File

import org.odfi.wsb.fwapp.Site
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPCodes
import com.idyria.osi.tea.io.TeaIOUtils
import java.io.ByteArrayInputStream
import org.odfi.indesign.ide.core.module.debian.deb.ChangesFile
import org.odfi.indesign.core.harvest.fs.HarvestedFile

/**
 * Created by rleys on 4/13/17.
 */
class DebianRepository(val repositoryPath: File) extends Site("/debian") {

  val incomingPath = HarvestedFile(new File(repositoryPath, "incoming").getCanonicalFile)
  val distsPath = HarvestedFile(new File(repositoryPath, "dists").getCanonicalFile)

  // Put is used for incoming files
  //------------------
  this.onDownMessage {

    case req if (req.operation == "PUT") =>

      //-- Res
      var resp = HTTPResponse()

      // Take File file
      req.parameters.find { _._1 == "File" } match {
        case Some((_, fileName)) =>

          // Save File
          var targetFile = new File(incomingPath, fileName)
          TeaIOUtils.writeToFile(targetFile, new ByteArrayInputStream(req.bytes))

        // Open and check

        case None =>

          resp.code = HTTPCodes.Bad_Request
          resp.setTextContent("Header File is missing")

      }

      /*
      println("Received PUT")
      println(s"Req for: " + req.path + " type " + req.operation)
      println(s"Req for: " + req.path + " type " + req.operation)
      println(s"Multipart: " + req.isMultipart)
      println(s"Req content: " + req.urlParameters)
      println(s"Req params: " + req.parameters)

      println(s"bytes: " + req.bytes.length)
			*/

      response(resp, req)

    case req =>

  }

  // POST is used to process
  //----------------------------
  this.onDownMessage {

    case req if (req.operation == "POST") =>

    case req                              =>

  }

  // Actual Actions
  //---------------------
  def incomingChanges(changes: File) = {

    //-- Create Changes File
    var changesFile = new ChangesFile(changes)

  }

  def incomingChanges(changes: ChangesFile) = {

    // Check Files
    //---------------------

    // Search for a file which is not in upload
    changes.getDependendFiles.find {
      file =>
        incomingPath.hasSubFile(file.getFileName).isEmpty
    } match {
      case Some(notFound) =>
        sys.error("Changes File requires " + notFound + " , but it is not in the incoming folder...make sure you upload all required files before triggering incoming process")
      case None =>

    }
    // Check Integrity
    //----------------

    // Publish
    //-----------------
    val targetDistFolder = distsPath.createSubFile(changes.getDistribution)
    changes.getArchitectures.foreach {
      case "source" => 
        targetDistFolder.createSubFile("source")
      case arch => 
        targetDistFolder.createSubFile("binary-"+arch)
    }
    val targetDistArchFolder = targetDistFolder.createSubFile("binary-"+changes.getArchitecture)

  }

}
