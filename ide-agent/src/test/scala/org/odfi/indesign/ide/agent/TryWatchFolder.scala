package org.odfi.indesign.ide.agent

import java.io.File
import com.idyria.osi.tea.files.FileWatcherAdvanced

object TryWatchFolder extends App {
  
  var f = new File("target/classes").getCanonicalFile
  var tf = new File("target/test-classes").getCanonicalFile
  
  
  var watcher = new FileWatcherAdvanced
  
  watcher.watchDirectoryRecursive(this, f) {
    f => 
      println("Detected change on target classes: "+f)
  }
  watcher.watchDirectoryRecursive(this, tf) {
    f => 
      println("Detected change on target classes: "+f)
  }
  watcher.start
  
  println("Waiting")
  Console.readLine()
}