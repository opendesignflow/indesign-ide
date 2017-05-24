package org.odfi.indesign.ide.module.ooxoo

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.ide.module.scala.ScalaSourceFile
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.io.File
import com.idyria.osi.tea.file.TeaFileUtils
import com.idyria.osi.tea.file.DirectoryUtilities

object OOXOOModelHarvester extends Harvester {
  
  
  this.onDeliverFor[ScalaSourceFile] {
    case f if (f.path.toFile().getName.endsWith(".xmodel.scala")) => 
      
      gather(new OOXOOModelFile(f))
      true
      
  }
  
}

class OOXOOModelFile(scalaFile: ScalaSourceFile) extends HarvestedFile(scalaFile.path) {
  deriveFrom(scalaFile)
  
  var outputFolders = List[File]()
  def addOutputFolder(f:File) = this.outputFolders = this.outputFolders :+ f
  
  /**
   * Reset ouput folders list, and delete their content too
   */
  def removeOutputFolders = {
    
    outputFolders.foreach {
      f => 
        DirectoryUtilities.deleteDirectory(f)
    }
    
    outputFolders = List[File]()
    
  }
  
}