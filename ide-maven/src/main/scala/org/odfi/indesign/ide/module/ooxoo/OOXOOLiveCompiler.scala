package org.odfi.indesign.ide.module.ooxoo

import org.odfi.indesign.ide.module.maven.compiler.MavenConfiguredLiveCompiler
import org.apache.maven.project.MavenProject
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import com.idyria.osi.tea.compile.IDCompiler
import scala.collection.JavaConversions._
import org.odfi.indesign.core.artifactresolver.AetherResolver
import com.idyria.osi.ooxoo.model.ModelCompiler
import java.io.File
import com.idyria.osi.ooxoo.model.writers.FileWriters
import org.odfi.indesign.core.harvest.fs.FSGlobalWatch
import org.odfi.indesign.ide.module.scala.ScalaSourceFile

class OOXOOLiveCompiler(project: MavenProjectResource) extends MavenConfiguredLiveCompiler(project) {

  var compiler: Option[IDCompiler] = None

  var outputBaseFile = new File(new File(project.getMavenModel.get.getBuild.getOutputDirectory).getParentFile, "generated-sources").getCanonicalFile

  /*this.onStart {
    logInfo[OOXOOLiveCompiler]("Starting OOXOO Live Compiler")
    compiler match {
      case Some(compiler) =>

      case None =>

        var c = new IDCompiler
        c.setParentClassLoader(getClass.getClassLoader)

        withDefinedNoError(this.project.getMavenModel) {
          mavenProject =>

            mavenProject.getBuild.getPlugins.find(p => p.getArtifactId == "maven-ooxoo-plugin") match {
              case Some(plugin) =>

                var dependencies = plugin.getDependencies.map {
                  dep =>
                    var deps = AetherResolver.resolveArtifactAndDependenciesClasspath(dep.getGroupId, dep.getArtifactId, dep.getVersion, "", "runtime")
                    println("Plugin dep: " + dep + " -> " + deps)
                    deps
                  //println("Plugin dep: "+dep.get)
                }.flatten.distinct

                c.addClasspathURL(dependencies.toArray)

              case None => throw new IllegalArgumentException("Maven Project has no maven Model to offer")

            }

        }

        this.compiler = Some(c)
    }
  }*/

  this.onStart {
    
    logInfo[OOXOOLiveCompiler]("Starting monitoring of " +  new File(project.getMavenModel.get.getBuild.getSourceDirectory))
    FSGlobalWatch.watcher.watchDirectoryRecursive(this, new File(project.getMavenModel.get.getBuild.getSourceDirectory)) {
      case f if (f.getName.endsWith(".xmodel.scala") && f.exists()) =>

        // Reset compiler to make sure rebuilding the model object will return the newest results and not the older one
        ModelCompiler.resetCompiler
        var resource = new OOXOOModelFile(new ScalaSourceFile(f.toPath()))
        var resolvedResource = OOXOOModelHarvester.getResourceById[OOXOOModelFile](resource.getId) match {
          case Some(r) => r
          case None => resource
        }

        processFile(resolvedResource)
      case other => 
        logInfo[OOXOOLiveCompiler]("Got non handled event ")

    }
  }

  this.onShutdown {

  }

  def processFile(modelFile: OOXOOModelFile) = {

    logInfo[OOXOOLiveCompiler]("Processing File: " + modelFile)
    
    modelFile.removeOutputFolders

    var modelInfos = ModelCompiler.compile(modelFile.path.toFile.getCanonicalFile)
    modelInfos.producers.value().foreach {
      producerAnnotation =>

        var producer = producerAnnotation.value().newInstance()

        var outFolder = new File(outputBaseFile, producer.outputType)
        outFolder.mkdirs()

        logInfo[OOXOOLiveCompiler](s"Producing ${producer.outputType} to: " + outFolder.getCanonicalPath)

        var out = new FileWriters(outFolder)

        ModelCompiler.produce(modelInfos, producer, out)

        modelFile.addOutputFolder(outFolder)

    }

  }

  def doRunFullBuild = {
    logInfo[OOXOOLiveCompiler]("Doing full build")

    // Look for XMOdel
    OOXOOModelHarvester.getResourcesByTypeAndUpchainParent[OOXOOModelFile, MavenProjectResource](project).foreach {
      modelFile =>
        logInfo[OOXOOLiveCompiler]("Model found: " + modelFile)

        FSGlobalWatch.watcher.isMonitoredBy(this, modelFile) match {
          case true =>
            logInfo[OOXOOLiveCompiler]("Model monitored " + modelFile)

          case false =>
            
            processFile(modelFile)

        }

    }

  }

}