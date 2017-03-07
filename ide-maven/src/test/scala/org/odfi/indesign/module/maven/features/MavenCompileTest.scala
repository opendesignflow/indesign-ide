package org.odfi.indesign.module.maven.features

import org.scalatest.FunSuite
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import java.io.File
import com.idyria.osi.tea.logging.TLog
import org.odfi.indesign.ide.module.maven.project
import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.ide.module.maven.MavenModule
import org.odfi.indesign.ide.module.scala.ScalaModule
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.ide.module.scala.ScalaProjectHarvester
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.ide.core.sources.SourceFile
import org.odfi.indesign.ide.module.scala.ScalaSourceFile
import org.odfi.indesign.core.harvest.fs.HarvestedFile


class MavenCompileTest  extends FunSuite {
  
  
  IndesignPlatorm use MavenModule
  IndesignPlatorm use ScalaModule
  
  test("Compile Failing project") {
    
    Brain.moveToStart
    
    // Open Project
    //---------
    TLog.setLevel(classOf[MavenProjectResource], TLog.Level.FULL)
    TLog.setLevel(classOf[FileSystemHarvester], TLog.Level.FULL)
    
    var project = new MavenProjectResource(new HarvestedFile(new File("src/test/testFS/maven-app-oneerror/").toPath()))

    MavenProjectHarvester.gatherPermanent(project)
  
    Harvest.run
    Harvest.run
    Harvest.printHarvesters
    
    //-- Check resources
    var resources = ScalaProjectHarvester.getResourcesByTypeAndUpchainParent[ScalaSourceFile,MavenProjectResource](project)
    println(s"Resources: "+resources.size)
    
     ScalaProjectHarvester.getResources.foreach {
      r => 
        println(s"R: "+r+" -> "+r.parentResource)
    }
    
    
    // Create Compiler
    //----------
    project.buildStandard
    
    
  }
  
}