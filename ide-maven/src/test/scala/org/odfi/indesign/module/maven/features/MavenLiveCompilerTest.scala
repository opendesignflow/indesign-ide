package org.odfi.indesign.module.maven.features

import org.odfi.indesign.ide.module.maven.MavenModule
import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.ide.module.scala.ScalaModule
import org.scalatest.FunSuite
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import java.io.File
import org.odfi.indesign.ide.module.ooxoo.OOXOOLiveCompiler
import com.idyria.osi.tea.logging.TLog
import org.odfi.indesign.ide.module.ooxoo.OOXOOModule
import org.odfi.indesign.ide.module.maven.MavenProjectHarvester
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.brain.Brain
import com.idyria.osi.tea.files.FileWatcherAdvanced
import org.odfi.indesign.core.harvest.fs.FSGlobalWatch
import org.odfi.indesign.core.harvest.fs.HarvestedFile

class MavenLiveCompilerTest extends FunSuite {

  IndesignPlatorm use MavenModule
  IndesignPlatorm use ScalaModule
  IndesignPlatorm use OOXOOModule
  
  test("LiveCompiler Detection")  {
    
    Brain.moveToStart
    
    //FSGlobalWatch.watcher.start
    
    TLog.enableFull[OOXOOLiveCompiler]
    TLog.enableFull[FileWatcherAdvanced]
    
    
    // Get Project
    var topProject = new MavenProjectResource(new HarvestedFile(new File("src/test/testFS/maven-app-ooxoo/").toPath()))
    MavenProjectHarvester.gatherPermanent(topProject)
    
    Harvest.run
    Harvest.printHarvesters
    
    // Build Live compiler
    topProject.buildLiveCompilers
    
    assertResult(2)(topProject.liveCompilers.size)
    
    // Get ooxoo and build
    var ooxooCompiler = topProject.getLiveCompiler[OOXOOLiveCompiler]
    assertResult(true)(ooxooCompiler.isDefined)
    
    ooxooCompiler.get.moveToStart
   // ooxooCompiler.get.runFullBuild
    
    ooxooCompiler.get.consumePrintErrorsToStdErr
    
    Console.readLine()
    
    ooxooCompiler.get.consumePrintErrorsToStdErr
    
    
  }

}