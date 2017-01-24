package com.idyria.osi.tea.files

import java.io.File
import java.lang.ref.WeakReference
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey

import scala.collection.JavaConversions._

import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.tea.thread.ThreadLanguage

/**
 * @author zm4632
 */
class FileWatcherAdvanced extends ThreadLanguage with TLogSource {

  var baseDirectories = Map[WatchKey, File]()
  var changeListeners = Map[String, Map[WeakReference[Any], List[File => Any]]]()
  var directoryChangeListeners = Map[String, Map[WeakReference[Any], List[File => Any]]]()

  def start = {
    logFine[FileWatcherAdvanced](s"////////////// Startin watcher")
    watcherThread.start()
  }

  def stop = {

  }

  def isMonitoredBy(sourceListener: Any, f: File) = {
    //println("Checking monitored by of : "+f+ " -> "+ f.isDirectory() )
    f.isDirectory() match {
      case true =>
        this.directoryChangeListeners.get(f.getAbsolutePath) match {
          case None =>
            false
          case Some(listenersMap) =>

            listenersMap.keys.find {
              case ref if (ref.get() == null) => false
              case ref => ref.get == sourceListener
            }.isDefined
        }
      case false =>
        this.changeListeners.get(f.getAbsolutePath) match {
          case None =>
            false
          case Some(listenersMap) =>
            listenersMap.keys.find {
              case ref if (ref.get() == null) => false
              case ref => ref.get == sourceListener
            }.isDefined
        }
    }
  }

  /**
   * Watch on all subfiles and also keep track of added files
   */
  def watchDirectoryRecursive(sourceListener: Any, f: File)(cl: File => Any) : Unit = {

    //-- Watch on parent file for new addition of current file
    val parentFile = f.getParentFile
    this.onDirectoryChange(sourceListener, parentFile) {
      case f if (f.getName==f.getName) =>
        //- Recal recursive watch
        println("Base foler: "+f+" was removed and readded, rewatching it")
        watchDirectoryRecursive(sourceListener, f)(cl)
      case f => 
    }

    //-- Function to start watching new files
    def watchNewFiles(changed: File): Unit = {
      if (changed.isDirectory && !isMonitoredBy(sourceListener, changed)) {

        //-- Start Watching
        //println("Watching new directory: " + changed)
        this.onDirectoryChange(sourceListener, changed)(cl)

        //-- Call listener on existing content
        //-- Go throught hiearchy
        var subFiles = Files.walk(f.toPath())
        subFiles.forEach {
          f =>
            f.toFile.isDirectory() match {
              case true =>
                this.onDirectoryChange(sourceListener, f.toFile) {
                  changed =>
                    cl(changed)
                    watchNewFiles(changed)
                }
    
              case false =>
                cl(f.toFile)
            }
        }
        /*changed.listFiles().foreach {
          case f if (f.isFile) =>
            // println("Existing file content: "+f)
            cl(f)
          // Directory don't do anything
          case f =>

        }*/

      }
    }

    //-- Listen to base folder
    this.onDirectoryChange(sourceListener, f) {
      changed =>
        cl(changed)
        watchNewFiles(changed)

    }

    //-- Go throught hiearchy
    var subFiles = Files.walk(f.toPath())
    subFiles.forEach {
      f =>
        f.toFile.isDirectory() match {
          case true =>
            this.onDirectoryChange(sourceListener, f.toFile) {
              changed =>
                cl(changed)
                watchNewFiles(changed)
            }

          case false =>
          // this.onFileChange(sourceListener, f.toFile)(cl)
        }
    }

  }

  def onDirectoryChange(sourceListener: Any, f: File)(cl: File => Any) = {
    f match {
      
    
      
      case f if (!f.isDirectory()) =>
        throw new RuntimeException("Cannot Watch Directory Change on File")
        
      // If already watched
        // FIXME not really valid, should be if watched and by this very same sourceListener too)
      case d if (this.baseDirectories.find {case (key,file) => file.getAbsolutePath==f.getAbsolutePath}.isDefined) => 
      case d =>

        // to path
        var directoryPath = FileSystems.getDefault().getPath(d.getAbsolutePath)

        //watcher.

        // Register if necessary
        this.baseDirectories.find { case (key, file) => file.getCanonicalFile.getAbsolutePath == d.getCanonicalFile.getAbsolutePath } match {
          case Some(entry) =>
          case None =>
            logFine[FileWatcherAdvanced](s"///////////// Registering Folder " + directoryPath)
            var watchKey = directoryPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)
            this.baseDirectories = this.baseDirectories + (watchKey -> d.getCanonicalFile)
        }

        // Save listener
        //-----------------

        //-- Get Listerners map 
        var listenersMap = directoryChangeListeners.get(f.getAbsolutePath) match {
          case Some(listeners) => listeners
          case None => Map[WeakReference[Any], List[File => Any]]()
        }

        //-- Add Listeners list for a specific listener
        var sourceListenerPair = listenersMap.find { case (ref, list) => ref.get != null && ref.get == sourceListener } match {
          // A map exists for the source 
          case Some(m) => m
          case None => (new WeakReference(sourceListener), List[File => Any]())
        }

        //-- Add closure to  listeners list  in source pair
        sourceListenerPair = (sourceListenerPair._1, sourceListenerPair._2 :+ cl)

        //-- Update source listener Pair in main map 
        listenersMap = listenersMap + sourceListenerPair

        //-- Update Map in main listeners
        this.directoryChangeListeners = this.directoryChangeListeners.updated(f.getAbsolutePath, listenersMap)
    }
  }

  def onFileChange(sourceListener: Any, f: File)(cl: File => Any) = {

    f match {
      case f if (f.isDirectory()) => throw new RuntimeException("Cannot Watch File Change on Directory")
      case f =>

        // to path
        var sourcePath = FileSystems.getDefault().getPath(f.getCanonicalFile.getCanonicalPath)
        var directoryPath = FileSystems.getDefault().getPath(f.getParentFile.getCanonicalFile.getCanonicalPath)

        // Register if necessary
        this.baseDirectories.find { case (key, file) => file.getCanonicalPath == f.getParentFile.getCanonicalFile.getCanonicalPath } match {
          case Some(entry) =>
          case None =>
            logFine[FileWatcherAdvanced](s"///////////// Registering Folder " + directoryPath)
            var watchKey = directoryPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
            this.baseDirectories = this.baseDirectories + (watchKey -> f.getParentFile.getCanonicalFile)
        }

        // Save listener
        //-----------------
        //-- Make File path low case on windows
        /*var fileAbsolutePath = OSDetector.isWindows match {
          case true => f.getAbsolutePath.toLowerCase()
          case false => f.getAbsolutePath
        }*/

        //-- Get Listerners map 
        var listenersMap = changeListeners.get(f.getAbsolutePath) match {
          case Some(listeners) => listeners
          case None => Map[WeakReference[Any], List[File => Any]]()
        }

        //-- Add Listeners list for a specific listener
        var sourceListenerPair = listenersMap.find { case (ref, list) => ref.get != null && ref.get == sourceListener } match {
          // A map exists for the source 
          case Some(m) => m
          case None => (new WeakReference(sourceListener), List[File => Any]())
        }

        //-- Add closure to  listeners list  in source pair
        sourceListenerPair = (sourceListenerPair._1, sourceListenerPair._2 :+ cl)

        //-- Update source listener Pair in main map 
        listenersMap = listenersMap + sourceListenerPair

        //-- Update Map in main listeners

        this.changeListeners = this.changeListeners.updated(sourcePath.toFile().getCanonicalPath, listenersMap)

        /* // Save listener
        var listeners = changeListeners.get(f.getAbsolutePath) match {
          case Some(listeners) => listeners
          case None => List()
        }
        listeners = listeners :+ { () => cl }
        this.changeListeners = this.changeListeners.updated(f.getAbsolutePath, listeners)*/

        logFine[FileWatcherAdvanced](s"///////////// Recorded event for " + sourcePath.toFile().getCanonicalPath)
      // 
    }

  }

  // Watcher Thread
  //--------------------
  var watcher = FileSystems.getDefault().newWatchService();
  var watcherThread = createThread {

    var stop = false
    while (!stop) {

      // Get Key

      var key = watcher.take()

      try {
        /* key.pollEvents().foreach {
          e => e.
        }*/

        //println(s"///////////// Got Key ")
        var events = key.pollEvents()
        key.reset()
        //println(s"Key got events: "+events.size)
        events.filter { ev => ev.kind() != StandardWatchEventKinds.OVERFLOW } foreach {

          // Directory remove
          //-----------------
          case be: WatchEvent[_] if (be.kind() == StandardWatchEventKinds.ENTRY_DELETE && be.count() <= 1) =>

            var e = be.asInstanceOf[WatchEvent[Path]]

            // Get Path of base directory
            this.baseDirectories.get(key) match {
              case Some(directoryFile) =>

                // Get Path of file
                var fileAbsoluteFile = directoryFile.toPath().resolve(e.context()).toAbsolutePath().toFile()
                var filePath = fileAbsoluteFile.getAbsolutePath

                // Clean 
                // Look in base dir. If found, remove key and all listeners
                // FIXME If not it could be just a file
                baseDirectories.find { case (key, f) => f.getAbsolutePath == filePath } match {
                  case Some((key, file)) =>
                    //println("Delete for Dir : " + filePath)
                    baseDirectories = baseDirectories - key
                    this.directoryChangeListeners = this.directoryChangeListeners - file.getAbsolutePath
                  case None =>

                }
              case None =>
            }

          // Directory Add
          //----------------------
          case be: WatchEvent[_] if (be.kind() == StandardWatchEventKinds.ENTRY_CREATE && be.count() <= 1) =>
            var e = be.asInstanceOf[WatchEvent[Path]]

            // Get Path of directory
            var directoryFile = this.baseDirectories.get(key).get

            // println(s"Directory add in $directoryFile for ${e.context()}")

            // Get Path of file
            var fileAbsoluteFile = directoryFile.toPath().resolve(e.context()).toAbsolutePath().toFile()
            var filePath = fileAbsoluteFile.getAbsolutePath

            // Get and run listeners
            directoryChangeListeners.get(directoryFile.getAbsolutePath) match {
              case Some(listenersMap) =>

                // Go through all sourceListeners and their closures
                // Clear Listeners for a source listener if the reference is null
                listenersMap.foreach {
                  case (ref, listeners) if (ref.get == null) =>
                    var newListernersMap = listenersMap - ref
                    this.directoryChangeListeners = this.directoryChangeListeners.updated(directoryFile.getAbsolutePath, listenersMap)

                  case (ref, listeners) =>
                    listeners.foreach {
                      l =>
                        // println(s"Running event")
                        l(fileAbsoluteFile)
                    }
                }

              case None =>
            }
          /*directoryChangeListeners.get(directoryFile) match {
              case Some(listeners) =>
                listeners.foreach {
                  l =>
                    // println(s"Running event")
                    l(filePath)
                }
              case None =>
            }*/

          // File Modify
          //----------------
          case be: WatchEvent[_] if (be.kind() == StandardWatchEventKinds.ENTRY_MODIFY && be.count() <= 1) =>

            var e = be.asInstanceOf[WatchEvent[Path]]

            // Get Path of directory
            var directoryFile = this.baseDirectories.get(key).get

            // Get Path of file
            var filePath = directoryFile.toPath().resolve(e.context()).toAbsolutePath().toFile()
            var fileAbsoluteFile = filePath.getCanonicalFile
            //var filePath = directoryFile.toPath().resolve(e.context()).toAbsolutePath().toFile().getAbsolutePath

            //var filePath = 
            logFine[FileWatcherAdvanced](s"///////////// Got Modify event for " + filePath + s" ${fileAbsoluteFile.lastModified()}// " + e.context().toAbsolutePath().toFile().getAbsolutePath + "//" + directoryFile.toPath())
            logFine[FileWatcherAdvanced](s"///////////// Count: " + be.count())

            // Get and run listeners

            changeListeners.get(fileAbsoluteFile.getAbsolutePath) match {
              case Some(listenersMap) =>

                logFine[FileWatcherAdvanced](s"///////////// Listeners Map Present")
                listenersMap.foreach {
                  case (ref, listeners) if (ref.get == null) =>

                    logFine[FileWatcherAdvanced](s"///////////// Reference for set of listeners is void")
                    var newListernersMap = listenersMap - ref
                    this.directoryChangeListeners = this.directoryChangeListeners.updated(directoryFile.getAbsolutePath, listenersMap)

                  case (ref, listeners) =>

                    logFine[FileWatcherAdvanced](s"///////////// Delivering for reference listener: " + ref.get)
                    listeners.foreach {
                      l =>
                        // println(s"Running event")
                        l(fileAbsoluteFile)
                    }
                }
              /*listeners.foreach {
                  l =>
                    // println(s"Running event")
                    l()
                }*/
              case None =>
            }

          case e =>

          // logWarn(s"///////////// Got Unsupported Event ")
        }

      } finally {
        //-- invalid key
        //println(s"Reset Key -> "+key.isValid())
        key.reset()
      }

    }

  }
  watcherThread.setDaemon(true)

}