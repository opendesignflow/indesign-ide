package org.odfi.indesign.ide.agent.reload

import java.lang.instrument.Instrumentation
import java.io.File
import imported.com.idyria.osi.tea.files.FileWatcherAdvanced
import java.lang.instrument.ClassDefinition
import imported.com.idyria.osi.tea.io.TeaIOUtils 
import java.io.FileInputStream
import org.odfi.indesign.ide.agent.IndesignIDEAgent

class IndesignClassReloader(val instrumentation: Instrumentation) {

  val watcher = new FileWatcherAdvanced
  watcher.start

  var watchedFolders = List[File]()

  def onChange(file: File) = {
    
    //println("Detected change on target class: "+file)
    
    //println("TocToc")
    file.getCanonicalPath match {
      
      // ONly conside .class without $ to filter the inner classes definitions
      case path if (path.endsWith(".class") && path.contains("$")==false) =>
        println("Reloading: " + path)

        //-- Remove base folder
        watchedFolders.find { baseFolder => path.startsWith(baseFolder.getCanonicalPath) } match {

          // Remove base folder from path and convert to class name
          case Some(baseFolder) =>
            var className = path.stripPrefix(baseFolder.getCanonicalPath+File.separator).replace(File.separator,".").stripSuffix(".class")
            println("Reloading: "+className)
            
            //-- Look for class in loaded classes
            try {
            instrumentation.getAllLoadedClasses.find(c => className.endsWith(c.getName) && className==c.getCanonicalName) match {
              case Some(loadedClass) => 
                println("Can reload")
                instrumentation.redefineClasses(new ClassDefinition(loadedClass,TeaIOUtils.swallowStream(new FileInputStream(path))))
              case None => 
                println("Class not loaded")
            }
            } catch {
              case e : Throwable => 
                
                println(s"Error for: $className during name definition, ignoring...."+e.getLocalizedMessage)
                
                //-- Mark tainted
                IndesignIDEAgent.restartRequired
                
            }

          // Ignore
          case None =>
        }
      //instrumentation.redefineClasses(x$1)

      case other =>
    }
  }

  def watchFolder(f: File) = {
    watcher.watchDirectoryRecursive(this, f)(onChange)
    watchedFolders = watchedFolders :+ f
  }

}