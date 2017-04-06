package org.odfi.indesign.ide.module.maven

import java.io.File
import java.net.URL
import java.nio.file.Files

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

import org.apache.maven.project.MavenProject
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.indesign.core.harvest.fs.FSGlobalWatch
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.core.module.lucene.LuceneIndexResource
import org.odfi.indesign.ide.core.compiler.LiveCompiler
import org.odfi.indesign.ide.core.compiler.LiveCompilerError
import org.odfi.indesign.ide.core.project.BuildableProjectFolder
import org.odfi.indesign.ide.core.project.jvm.JavaOutputProject
import org.odfi.indesign.ide.module.maven.embedder.EmbeddedMaven
import org.odfi.indesign.ide.module.ooxoo.OOXOOLiveCompiler
import org.odfi.indesign.ide.module.scala.ScalaSourceFile
import org.odfi.indesign.ide.module.scala.compiler.ScalaMavenLiveCompiler

import com.idyria.osi.tea.compile.ClassDomainContainer
import com.idyria.osi.tea.compile.ClassDomainSupport
import com.idyria.osi.tea.compile.IDCompiler
import com.idyria.osi.tea.files.FileWatcherAdvanced
import org.odfi.indesign.ide.module.maven.resolver.MavenProjectWorkspaceReader
import org.eclipse.aether.graph.Dependency
import org.odfi.indesign.core.harvest.fs.FileSystemIgnoreProvider

class MavenProjectResource(p: HarvestedFile) extends BuildableProjectFolder(p.path)
    with ClassDomainSupport
    with LuceneIndexResource
    with ClassDomainContainer
    with JavaOutputProject
    with FileSystemIgnoreProvider {

  //-- Get Pom File 
  var pomFile = new File(p.path.toFile(), "pom.xml")

  this.onProcess {

    println("Maven project Processing: " + this.getProjectId)

    this.resetErrors

    //-- Derive when added
    deriveFrom(p)

    //-- Build Dependencies
    buildDependencies
    //recreateClassDomain

    //-- Reset Workspace reader to consider new project
    MavenProjectWorkspaceReader.resetAllProjects

  }

  def fileIgnore(f: File) = {
    f match {
      case target if (target.getName == "target") =>
        //println("Blocking: "+f)
        true
      case isPom if (isPom.getCanonicalPath == pomFile.getCanonicalPath) =>
        //println("Blocking: "+f)
        true
      case other =>

        false
    }
  }

  //-- File Watcher for this project
  //var watcher = new FileWatcher

  // Class Domain Create
  //--------------

  var beautyTime = 5000
  var lastTime = 0L
  this.onRebuildClassDomain {

    println("Rebuilding CD: " + this.originalHarvester)
    this.originalHarvester match {
      case Some(ph) =>

        var cd = this.classdomain.get

        //-- Add output
        this.getMavenModel match {
          case ESome(model) =>

            //-- Add Output to Class loader
            println(s"Adding output folder to new cd: " + cd)

            //-- Print Deps
            //buildDependencies
            /*this.getDependenciesURL.foreach {
              url =>
                println(s"Deps: " + url)
            }*/

            //buildDependencies

            //println(s"Project output is: "+projectOutput)
            cd.addURL(new File(model.getBuild.getOutputDirectory).getCanonicalFile.toURI().toURL())

            getDependenciesURL.foreach {
              url =>
                cd.addURL(url)
            }

            FSGlobalWatch.watcher.isMonitoredBy(this, new File(model.getBuild.getOutputDirectory)) match {
              case true =>
              case false =>

                println(s"Starting Watch on $this  ${this.hashCode()} -> ${model.getBuild.getOutputDirectory}")
                //var e = new Throwable
                //e.printStackTrace(System.out)
                FSGlobalWatch.watcher.watchDirectoryRecursive(this, new File(model.getBuild.getOutputDirectory)) {
                  f =>
                    println(s"######### Detected compilation on $this (${this.isTainted}) ${this.hashCode()}, reloading class, origin file is $f ###########")
                    if (lastTime < (System.currentTimeMillis() - beautyTime)) {
                      lastTime = System.currentTimeMillis()
                      Thread.sleep(beautyTime / 4)

                      //println(s"Cleaning")
                      // This is running on the old classloader
                      this.classdomain match {
                        case Some(cd) =>
                          this.getDerivedResources[HarvestedResource].foreach {
                            case r if (r.getClass.getClassLoader == this.classdomain.get) =>
                              println(s"need to clean: " + r)
                              this.cleanDerivedResource(r)
                            //  r.clean
                            case r =>
                            //println(s"no need to clean: "+r+ " -> res has cl: "+r.getClass.getClassLoader+",current CL "+cd)
                          }
                        case None =>
                      }

                      //-- Rebuild classdomain
                      println("Replacing old CD: "+this.classdomain.get)
                      recreateClassDomain
                      MavenProjectHarvester.findDownStreamProjects(this).foreach {
                        p => 
                          println(s"Replacing CD on: "+p)
                          p.getDerivedResources[HarvestedResource].foreach {
                            case r if (r.getClass.getClassLoader == p.classdomain.get) =>
                              println(s"need to clean: " + r)
                              p.cleanDerivedResource(r)
                            //  r.clean
                            case r =>
                            //println(s"no need to clean: "+r+ " -> res has cl: "+r.getClass.getClassLoader+",current CL "+cd)
                          }
                          p.recreateClassDomain
                      }

                      //this.reload
                      //this.harvest
                      // Harvest.run
                      Harvest.run
                    }
                }

                this.onClean {
                  println(s"**** Remove compilation watcher")
                  FSGlobalWatch.watcher.cleanFor(this)
                }

            }

          case other =>
        }

      case None =>
    }
  }

  // Configuration thing
  //----------------
  //this.config

  //-- Maven Model
  //------------
  var maven = new EmbeddedMaven

  var projectModel = project(pomFile.toURI().toURL())

  var buildProjectModel: ErrorOption[MavenProject] = ENone
  var buildInProgress = false
  def getMavenModel = buildInProgress match {
    case false =>
      buildProjectModel match {

        case ENone =>

          //-- Maven Model is rebuild
          buildInProgress = true
          try {
          MavenModule.buildMavenProject(pomFile) match {
            case ESome(p) =>
              buildProjectModel = ESome(p)

              //-- Rebuild Class Domain
              buildInvalidateDependencies

            case other: EError =>
              buildProjectModel = other
              addError(other.value)
              //other.value.printStackTrace()
              other
            case other =>
              buildProjectModel = other
          }
          
          } finally {
             buildInProgress = false
          }

          buildProjectModel

        case other: EError =>
          //addError(other.value)
          //other.value.printStackTrace()
          other
        case other =>
          other
      }
    case true => 
      ENone
  }

  def isModelBuild = buildProjectModel.isDefined

  //-- ID Stuff
  override def getId = s"Maven:${p.path.toFile()}"

  def getProjectId = getMavenModel match {
    case ESome(project) => s"${project.getGroupId}:${project.getArtifactId}:${project.getVersion}"
    case other if (projectModel == null) => super.getId
    case other => s"${projectModel.getGroupId}:${projectModel.getArtifactId}:${projectModel.getVersion}"
  }
  override def getDisplayName = getMavenModel match {
    case ESome(project) if (project.getName != null && project.getName != "") => project.getName
    case EError(error) if (projectModel.name != null && projectModel.name != "") => projectModel.name
    case other => getProjectId
  }

  def getArtifactId = getMavenModel match {
    case ESome(project) => project.getArtifactId
    case other => projectModel.getArtifactId
  }

  def getGroupId = getMavenModel match {
    case ESome(project) => project.getGroupId
    case other => projectModel.getGroupId
  }

  def getVersion = getMavenModel match {
    case ESome(project) => project.getVersion
    case other => projectModel.getVersion
  }

  /**
   * On File Change, update model and invalidate dependencies
   */
  FSGlobalWatch.watcher.onFileChange(this, pomFile) {
    file =>
      this.projectModel = project(pomFile.toURI().toURL())
      this.buildProjectModel = ENone
    //this.dependencies = None
  }
  // MavenProjectResource.watcher.

  //-- Indexing
  def getLuceneDirectory = new File(p.path.toFile, ".indesign-lucene-index")

  //-- WWW VIew
  //var view = new MavenWWWView(this)

  // Build
  //--------------

  /**
   * Maps to generate-source per default
   */
  def buildPrepare = {
    println(s"*** In build prepare: " + getMavenModel)
    withDefined(getMavenModel) {
      projet =>

        println(s"*** Getting prepare goal")
        var prepareGoal = withOptionDefinedElse(this.config, "generate-sources") {
          case config if (config.getKey("build.prepare.goal", "string").isDefined) =>
            config.getKey("build.prepare.goal", "string").get.values(0).toString
          case other =>
            "generate-sources"

        }

        println(s"*** Running prepare goal")
        maven.executeGoal(projet, List(prepareGoal)) match {
          case ESome(res) =>
            this.buildProjectModel = ESome(res.getProject)
          case other =>
        }

    }

  }

  def buildFully = {
    //buildCompiler
    buildPrepare
    withDefined(getMavenModel) {
      projet =>
        maven.executeGoal(projet, List("compile")) match {
          case ESome(res) =>
            this.buildProjectModel = ESome(res.getProject)
          case EError(err) =>
            addError(err)
            throw err
          case other =>
        }
    }
  }

  def buildStandard = {
    buildCompiler

    //-- look for sources
    var toCompile = this.getDerivedResources[HarvestedFile].collect {
      case r if (r.hasDerivedResourceOfType[ScalaSourceFile]) =>
        // println(s"To compile: " + r)
        //r.getDerivedResources[ScalaSourceFile].head
        r.path.toFile()

    }

    withClassLoader(classdomain.get) {

      compiler.get.compileFiles(toCompile.toSeq) match {
        case Some(errors) =>
          addError(errors)
        case None =>
      }

    }

  }

  // Discoveries
  //-----------------
  override def discoverType[CT <: Any](implicit tag: ClassTag[CT]) = {

    this.getMavenModel match {
      case ESome(model) =>

        //println(s"Model Build -> "+this.classdomain)
        this.classdomain match {

          case Some(cd) =>
            println(s"Discovering using cd: "+cd)
            var folder = new File(model.getBuild.getOutputDirectory).getCanonicalFile

            var stream = Files.walk(folder.toPath())
            var foundTypes = List[Class[CT]]()
            stream.forEach {
              f =>

                f.toFile.getName.endsWith(".class") match {
                  case true =>
                    var className = f.toString().replace(folder.getCanonicalPath + File.separator, "").replace(File.separator.toString, ".").replace(".class", "")
                    // println(s"Discover ${tag} Lookin at: " + className)

                    try {

                      var cl = cd.loadClassFromFile(className, f.toFile)
                      // var cl = cd.loadClass(className)

                      tag.runtimeClass.isAssignableFrom(cl) match {
                        case true =>
                          foundTypes = foundTypes :+ cl.asInstanceOf[Class[CT]]
                        case false =>
                      }
                    } catch {
                      case e: Throwable =>
                        println(s"Fail : " + e.getLocalizedMessage)
                      //e.printStackTrace()

                    }
                  case false =>

                }
              /*f.getFileName.toString().endsWith("$.class") match {
            // Don't Check objects
            case true => 
          }*/

              // var relativeF = f.resolve(outputPath.toPath())
              //println("Lookin at: "+relativeF.toString())
              //if (f.getFileName.toString().endsWith("$.class")) {

            }

            foundTypes

          //-- Don't try without CD
          case None =>
            List[Class[CT]]()

        }

      //-- Don't try without Project Model
      case other =>
        List[Class[CT]]()
    }
  }

  // Dependencies
  //---------------------
  var dependencies = List[Dependency]()
  var dependenciesURLS = List[URL]()

  /**
   * Update Dependencies, meaning resresolve everything and add to list
   */
  def updateDependencies: Unit = {

    // Update Aether Resolver with Resolution URLS
    /*keepErrorsOn(this) {
      projectModel.repositories.repositories.foreach {
        r =>
          AetherResolver.config.addDefaultRemoteRepository(r.id, r.url.data.toURL)
      }
      projectModel.pluginRepositories.pluginRepositories.foreach {
        r =>
          AetherResolver.config.addDefaultRemoteRepository(r.id, r.url.data.toURL)
      }

      // Map List of dependencies
      var newDeps = projectModel.dependencies.dependencies.filter(d => d.scope == null || d.scope.toString() == "compile").map {
        d =>
          try {
            //println(s"Dep needed ${d.artifactId}:${d.groupId}:${d.version}")
            AetherResolver.resolveArtifactAndDependencies(d.groupId, d.artifactId, d.version)
          } catch {
            case re: Throwable =>
              println(re.getLocalizedMessage)
              List[Artifact]()
          }
      }.flatten.toList.distinct*/

    // println(s"Dep Res ${res.toList}")

    //dependencies = (dependencies ::: newDeps).distinct
    //dependenciesURLS = dependencies.map { d => AetherResolver.resolveArtifactsFile(d) }.filter(_.isDefined).map { _.get.toURI().toURL() }

    //dependenciesURLS = None

    //}

  }

  def getDependencies = dependencies

  def getDependenciesURL = dependenciesURLS

  def isArtifact(art: Artifact) = {
    /*var res = (getArtifactId.equals(art.getArtifactId)) &&
    (getGroupId.equals(art.getGroupId)) &&
    (getVersion.equals(art.getVersion))
    
    println(s"Is ${getGroupId}:${getArtifactId}:${getVersion} against: ${art.getGroupId}:${art.getArtifactId}:${art.getVersion} -> $res" )
    res*/

    (getArtifactId == art.getArtifactId) &&
      (getGroupId == art.getGroupId) &&
      (getVersion == art.getVersion)
  }

  // Compiler Stuff
  //-----------------

  //-- Classdomain
  // var classDomain = new ClassDomain(Thread.currentThread().getContextClassLoader)
  //var classDomain = new ClassDomain(classOf[Brain].getClassLoader)
  //this.createNewClassDomain(classOf[Brain].getClassLoader)

  //-- Compiler
  var compiler: Option[IDCompiler] = None

  /*def resetClassDomain: Unit = {

    recreateClassDomain

    // Clear
    //var pCl = classDomain.getParent
    //classDomain.tainted = true
    //classDomain = null
    this.compiler = null
    System.gc

    // Recreate compiler
    //this.classDomain = new ClassDomain(pCl)
    this.withClassLoader(this.classdomain.get) {
      this.compiler = Some(new IDCompiler)

      // this.classDomain.addURL(new File(this.path.toFile(), "target/classes").toURI().toURL())
      this.compiler.get.addSourceOutputFolders((new File(this.path.toFile(), "src/main/scala"), new File(this.path.toFile(), "target/classes")))

      // Add dependencies
      //var urlDeps = this.getDependencies.map(_.getFile.toURI().toURL()).toArray
      var du = getDependenciesURL
      this.compiler.get.addClasspathURL(du.toArray)

    }

  }*/

  def buildInvalidateDependencies = {

    //-- Tain Class Domain
    this.taintClassDomain

  }

  def buildDependencies = {

    //-- Invalidate compiler
    this.buildInvalidateCompiler

    //-- Build Dependencies
    getMavenModel match {
      case ESome(project) =>

        maven.resolveDependencies(project) match {
          case ESome(deps) =>

            
            
           /* var depsKey = config.get.getKey("dependencies", "files") match {
              case Some(key) => key
              case None => config.get.addKey("depdendencies", "files")
            }
            
            depsKey.values.clear()*/

            deps.foreach {
              dep =>
                println("Collected: " + dep.getArtifact.getFile)
               // depsKey.values.add.set(dep.getArtifact.getFile.getCanonicalPath)
            }
            
            //-- Save dependencies
            dependencies = deps
            dependenciesURLS = deps.map {
              dep => dep.getArtifact.getFile.toURI().toURL()
            }

            //-- Update classdomain
            recreateClassDomain

          case EError(err) =>
            println("Resolution error:")
            err.printStackTrace()
            throw err
          case other =>
            println("Resolution other: " + other)
        }

      /*var deps = project.getDependencies.toList.map { dep => new DefaultArtifact(dep.getGroupId, dep.getArtifactId, "jar", dep.getVersion) }

        project.getArtifacts.toList.foreach {
          dep =>
            println("Detected dependency: "+dep+" -> ")
        }*/
      /*project.getDependencies.toList.foreach {
          dep =>
            println("Detected dependency: "+dep+" -> ")
        }*/

      //(deps, deps.map { dep => AetherResolver.resolveArtifactsFile(dep) }.filter(_.isDefined).map { _.get.toURI().toURL() })
      case other =>
      //(List(), List())
    }

    println("Done deps")
    /*dependencies = result._1
    dependenciesURLS = result._2

    dependenciesURLS.foreach {
      url =>
        println("Created dependency: " + url)
    }*/

    //-- Rebuild ClassDomain
    //-----------
    //this.recreateClassDomain

    //-- Add Build output
    //this.classdomain.get.addURL(new File(this.path.toFile(), getMavenModel.get.getBuild.getOutputDirectory).toURI().toURL())

    //-- Update Dependencies
    //getDependenciesURL.foreach(this.classdomain.get.addURL(_))

    /*dependencies = None
    dependenciesURLS = None
    getDependencies*/

    //updateDependencies
    //this.@->("rebuild")

    /*var du = getDependenciesURL

    //-- Update
    this.compiler match {
      case Some(comp) => comp.addClasspathURL(du.toArray)
      case None =>
    }

    du.foreach(this.classdomain.get.addURL(_))*/

  }

  // Compiler Request
  //--------------------
  def buildLiveCompilers = {

    this.liveCompilers.size match {
      case 0 =>
        getMavenModel match {
          case ESome(model) =>

            model.getBuild.getPlugins.foreach {

              // Scala Plugin
              case plugin if (plugin.getArtifactId == "scala-maven-plugin") =>
                var c = new ScalaMavenLiveCompiler(this)
                this.addLiveCompiler(c)

              // Ooxoo Plugin
              case plugin if (plugin.getArtifactId == "maven-ooxoo-plugin") =>
                var c = new OOXOOLiveCompiler(this)
                this.addLiveCompiler(c)

              case other =>

            }

          case EError(err) =>
            addError(new LiveCompilerError(err))

          case ENone =>
            addError(new LiveCompilerError(new IllegalArgumentException("POM Model could not be found")))

        }
      case other =>

    }

  }

  def buildInvalidateCompiler = this.compiler match {
    case Some(c) =>
      this.compiler = None
      System.gc()
    case None =>
  }

  def buildCompiler = this.compiler match {
    case Some(c) =>

    case None =>

      //-- Create
      this.compiler = Some(new IDCompiler)

      // this.classDomain.addURL(new File(this.path.toFile(), "target/classes").toURI().toURL())
      this.getMavenModel match {
        case ESome(project) =>

          this.compiler.get.addSourceOutputFolders(new File(project.getBuild.getSourceDirectory), new File(project.getBuild.getOutputDirectory))

          // this.compiler.get.addSourceOutputFolders((new File(this.path.toFile(), "src/main/scala"), new File(this.path.toFile(), "target/classes")))

          // Add dependencies
          //var urlDeps = this.getDependencies.map(_.getFile.toURI().toURL()).toArray
          this.compiler.get.addClasspathURL(getDependenciesURL.toArray)

        case other =>

      }

  }

  /**
   * Compile a file
   */
  def compile(h: HarvestedFile) = {

    // resetClassDomain
    this.compiler match {
      case Some(compiler) =>

        withClassLoader(classdomain.get) {
          compiler.compileFile(h.path.toFile()) match {
            case Some(errors) =>
              throw errors
            case None =>
          }

        }

      case None =>
        sys.error("Cannot compile file " + h + ", compiler was not set, maybe resource was not processed")
    }

  }

  // Builder Task
  //-----------------------
  /*class BuilderTask extends DefaultHeartTask {

    
    
    def doTask = {

    }

  }*/

}

object MavenProjectResource {
  var watcher = new FileWatcherAdvanced
  watcher.start
}