package org.odfi.indesign.ide.core.module.debian.deb

import java.io.File
import java.nio.file.Path

import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.core.resources.TextSourceResource
import org.odfi.indesign.core.resources.FileTextSourceResource

/**
  * Represents a debian changes file
  *
  * Created by rleys on 4/13/17.
  */
class ChangesFile(f:File) extends FileTextSourceResource(f.toPath) {

  
  class ChangesFileFile(val line : String) {
    
    def getFileName = line.split(" ").last
    
  }
  
  def getDependendFiles = {
    
    getTextLines.dropWhile { _.startsWith("Files:")==false }.drop(1).takeWhile { _.startsWith(" ") }.map {
      fileLine => 
        
        new ChangesFileFile(fileLine.trim)
    }.toList
    
    
  }
  
  def getDistribution = {
    getTextLines.find { _.startsWith("Distribution:")==false } match {
      case Some(d) => d.trim().split(" ").last.trim
      case None => "undefined"
    }
  }
  
  def getArchitectures = {
    
    var r = getTextLines.find { _.startsWith("Architecture:")==false } match {
      case Some(d) => d.trim().split(" ").last.trim
      case None => "undefined"
    }
    r.split(" ").toList
  }
  
  // Integrity
  //-------------------
  
  def checkIntegrity = {
    
  }



}
