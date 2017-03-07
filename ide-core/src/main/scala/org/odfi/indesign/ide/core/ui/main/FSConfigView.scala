package org.odfi.indesign.ide.core.ui.main

import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.ide.core.ui.utils.LocalGUIExtensionsView

class FSConfigView extends IDEBaseView with LocalGUIExtensionsView {

  this.placePage {

    ribbonHeaderDiv("blue", "Search Paths") {

      var sources = FileSystemHarvester.getResourcesOfType[HarvestedFile].filter { _.rooted }

      sources.size match {
        case 0 =>
          "ui warning message" :: "No File System search path for projects was defined, you should configure one to find projects"

        // Sources Table
        //------------
        case _ =>

          "ui table" :: table {
            thead("Location", "Actions")
            tbody {
              sources.foreach {
                source =>
                  tr {
                    td(source.path.toFile.getCanonicalPath) {

                    }

                    td("") {

                    }
                  }
              }
            }
          }

      }
      
      // Add Folder
      isLocalRequestWithDisplay match {
        case true => 
          "@reload" :: selectDirectoryButton("Add Directory to Sources") {
            folder => 
              //FileSystemHarvester.addPath(folder)
              FileSystemHarvester.config match {
                case Some(conf) => 
                  conf.setUniqueKeyFirstValue("file",folder.getCanonicalPath)
                  conf.resyncToFile
                case None => 
                  
              }
          }
        case false => 
      }

    }

  }

}