package org.odfi.indesign.ide.module.maven

import java.io.File
import java.net.URL
import java.nio.file.Path

import org.eclipse.aether.artifact.Artifact
import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.core.module.lucene.LuceneIndexResource
import org.odfi.indesign.ide.core.project.BuildableProject

import com.idyria.osi.tea.compile.ClassDomainContainer
import com.idyria.osi.tea.compile.ClassDomainSupport
import com.idyria.osi.tea.compile.IDCompiler
import com.idyria.osi.tea.files.FileWatcherAdvanced
import org.odfi.indesign.ide.core.project.BuildableProjectFolder
import org.apache.maven.model.building.ModelBuildingResult
import org.apache.maven.model.Model
import org.apache.maven.project.ProjectBuildingResult
import org.apache.maven.project.MavenProject

import scala.collection.JavaConversions._
import org.apache.maven.model.Dependency
import org.eclipse.aether.artifact.DefaultArtifact
import org.odfi.indesign.ide.module.scala.ScalaSourceFile
import org.odfi.indesign.ide.module.maven.embedder.EmbeddedMaven
import org.odfi.indesign.ide.core.compiler.LiveCompilerError
import org.odfi.indesign.ide.module.scala.compiler.ScalaLiveCompiler
import org.odfi.indesign.ide.module.scala.compiler.ScalaMavenLiveCompiler
import org.odfi.indesign.ide.core.compiler.LiveCompiler
import org.odfi.indesign.ide.module.ooxoo.OOXOOLiveCompiler

class MavenProjectResource(p: Path) extends BuildableProjectFolder(p) with ClassDomainSupport with LuceneIndexResource with ClassDomainContainer {

  //-- Get Pom File 
  var pomFile = new File(p.toFile(), "pom.xml")

  //-- File Watcher for this project
  //var watcher = new FileWatcher

  // Configuration thing
  //----------------
  this.config

  //-- Maven Model
  //------------
  var maven = new EmbeddedMaven

  var projectModel = project(pomFile.toURI().toURL())

  var buildProjectModel: ErrorOption[MavenProject] = ENone

  def getMavenModel = buildProjectModel match {

    case ENone =>
      buildProjectModel = MavenModule.buildMavenProject(pomFile)
      buildProjectModel
    case other: EError =>
      addError(other.value)
      other
    case other =>
      other

  }

  def isModelBuild = buildProjectModel.isDefined

  //-- ID Stuff
  // override def getId = s"${projectModel.getGroupId}:${projectModel.artifactId}:${projectModel.getVersion}"
  override def getId = getMavenModel match {
    case ESome(project) => s"${project.getGroupId}:${project.getArtifactId}:${project.getVersion}"
    case other if (projectModel == null) => super.getId
    case other => s"${projectModel.getGroupId}:${projectModel.getArtifactId}:${projectModel.getVersion}"
  }
  override def getDisplayName = getMavenModel match {
    case ESome(project) if (project.getName != null && project.getName != "") => project.getName
    case EError(error) if (projectModel.name != null && projectModel.name != "") => projectModel.name
    case other => getId
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
  MavenProjectResource.watcher.onFileChange(this, pomFile) {
    file =>
      this.projectModel = project(pomFile.toURI().toURL())
      this.buildProjectModel = ENone
    //this.dependencies = None
  }

  //-- Indexing
  def getLuceneDirectory = new File(p.toFile, ".indesign-lucene-index")

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
        println(s"To compile: " + r)
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

  // Dependencies
  //---------------------
  var dependencies = List[Artifact]()
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

  def getDependencies = getMavenModel match {
    case ESome(project) =>
      // println(s"Getting deps: "+project.getDependencies.toList)
      project.getDependencies.map {
        mart =>
          new DefaultArtifact(mart.getGroupId, mart.getArtifactId, "jar", mart.getVersion)
      }.toList
    case other =>
      projectModel.getArtifacts

  }

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
  this.createNewClassDomain(classOf[Brain].getClassLoader)

  //-- Compiler
  var compiler: Option[IDCompiler] = None

  this.onAdded {
    case h if (h == MavenProjectHarvester) =>

    //updateDependencies

    //view.originalHarvester = this.originalHarvester
    //WWWViewHarvester.deliverDirect(view)

    // println(s"Maven Project resource added to harvster")
    //MavenModule.addSubRegion(this)

    //-- 

    case _ =>
  }

  this.onProcess {
    //println(s"Creating Compiler for MavenProject")

    // watcher.start

    compiler match {
      case None =>

      /*this.compiler = Some(new IDCompiler)
        this.classDomain.addURL(new File(this.path.toFile(),"target/classes").toURI().toURL() )
        this.compiler.get.addSourceOutputFolders((new File(this.path.toFile(),"src/main/scala"),new File(this.path.toFile(),"target/classes")))*/

      case _ =>
    }

  }

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
    this.taintClassDomain
    this.classdomain = None
  }

  def buildDependencies = this.classdomain match {

    case None =>

      //-- Invalidate compiler
      this.buildInvalidateCompiler

      //-- Build Dependencies
      var result = getMavenModel match {
        case ESome(project) =>
          var deps = project.getDependencies.toList.map { dep => new DefaultArtifact(dep.getGroupId, dep.getArtifactId, "jar", dep.getVersion) }
          (deps, deps.map { dep => AetherResolver.resolveArtifactsFile(dep) }.filter(_.isDefined).map { _.get.toURI().toURL() })
        case other => (List(), List())
      }

      dependencies = result._1
      dependenciesURLS = result._2

      //-- Rebuild ClassDomain
      //-----------
      this.recreateClassDomain

      //-- Add Build output
      this.classdomain.get.addURL(new File(this.path.toFile(), getMavenModel.get.getBuild.getOutputDirectory).toURI().toURL())

      //-- Update Dependencies
      getDependenciesURL.foreach(this.classdomain.get.addURL(_))

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

    case Some(_) =>
  }

  // Compiler Request
  //--------------------
  def buildInvalidateLiveCompilers = {

    this.liveCompilers.foreach {
      c => c.clean
    }
    this.liveCompilers = List[LiveCompiler]()
    
  }

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