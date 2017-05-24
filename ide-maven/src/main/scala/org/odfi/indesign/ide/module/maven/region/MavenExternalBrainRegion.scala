package org.odfi.indesign.ide.module.maven.region

import java.io.File
import java.net.URL

import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.brain.ExternalBrainRegion
import org.odfi.indesign.core.brain.ExternalBrainRegionBuilder
import org.odfi.indesign.core.brain.artifact.ArtifactRegion
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.ide.module.maven.MavenProjectResource
import org.odfi.indesign.ide.module.maven.resolver._

import com.idyria.osi.tea.compile.ClassDomain
import com.idyria.osi.tea.logging.TLog
import org.odfi.indesign.ide.core.sources.ModuleSourceFile
import org.odfi.indesign.ide.module.maven.project
import scala.reflect.ClassTag

class MavenExternalBrainRegionBuilder extends ExternalBrainRegionBuilder {

  def accept(url: URL): Integer = {

    url.getProtocol match {
      case "file" =>

        // Look for target/classes
        new File(new File(url.getPath), "pom.xml") match {
          case pomFile if (pomFile.exists()) =>
            
            //-- Filter out building the maven region project itself
            var prj = project(pomFile.toURI().toURL())
            prj.getArtifactId match {
              case id if(id!=null && id == "indesign-ide-maven") =>
                0
              case other => 
                 // Return 2 to be above standard Folder Region Builder score
                2
            }

          case _ => 0
        }
      case _ => 0
    }

  }

  def build(url: URL): ExternalBrainRegion = {

    var base = new File(url.getPath).getCanonicalFile
    new MavenExternalBrainRegion(new HarvestedFile(base.toPath()))

  }
}

/**
 * Loads a Brain Region present in another externaly compiled module
 */
class MavenExternalBrainRegion(val basePath: HarvestedFile) extends MavenProjectResource(basePath) with ExternalBrainRegion with ArtifactRegion {

  //  TLog.setLevel(classOf[MavenExternalBrainRegion], TLog.Level.FULL)

  //-- Register Maven Region Builder
  //-- When Region is created, look if builder classdomain is tainted
  this.on("region.created") {
    println("(***) MERegion create: " + regionBuilder.get.getClass.getClassLoader)
  }

  //ExternalBrainRegion.addBuilder(new MavenExternalBrainRegionBuilder,true)

  override def getName = projectModel.artifactId match {
    case null => getId
    case v => v.toString
  }
  override def getId = projectModel.artifactId match {
    case null => basePath.path.toFile().getAbsolutePath
    case v => v.toString
  }

  override def isConfigLoaded = {
    super.isConfigLoaded
  }

  override def config = super.config

  override def getRegionPath = basePath.path.toFile.getAbsolutePath

  def getRegionArtifact: Artifact = new DefaultArtifact(projectModel.getGroupId, projectModel.getArtifactId, "jar", projectModel.getVersion)
  def getRegionDependencies = this.getDependencies.map { d => d.getArtifact}

  //-- Override tainted to make sure tainted is only if original classloader is tainted and also local one

  /**
   * Tainted if class domain is tainted or this classdomain tainted and classloader is also tainted
   */
  override def isTainted = {
    this.classdomain.get.tainted || (this.classdomain.get.tainted && this.getClass.getClassLoader.isInstanceOf[ClassDomain] && this.getClass.getClassLoader.asInstanceOf[ClassDomain].tainted)
  }

  //-- Detect dependent projects
  //TLog.setLevel(classOf[MavenProjectIndesignWorkspaceReader], TLog.Level.FULL)
  //MavenProjectIndesignWorkspaceReader.resetAllProjects
  //AetherResolver.session.setWorkspaceReader(MavenProjectIndesignWorkspaceReader)
  //TLog.setLevel(classOf[MavenExternalBrainRegion], TLog.Level.FULL)
  
  //-- Load actual Region
  /*println(s"CL: " + Thread.currentThread().getContextClassLoader)
  this.resetClassDomain
  println(s"CL: " + Thread.currentThread().getContextClassLoader)*/

  /**
   * Maven region setup;
   * Try to find other regions we dependend on, and set them as parent class loader
   */
  this.onSetup {

    println("(***) MERegion Setup: " + regionBuilder.get.getClass.getClassLoader)

    regionBuilder.get.getClass.getClassLoader
    this.changeParentClassDomain(regionBuilder.get.getClass.getClassLoader.asInstanceOf[ClassDomain])

    //-- Update Dependencies
    // this.forceUpdateDependencies

    //-- Look for another MavenRegion which we would have in depedendencies
    this.resolveRegionClassDomainHierarchy

    logFine[MavenExternalBrainRegion](s"($this) Maven Region Setup: Reseting CLD: " + this + " - " + this.classdomain)
    //this.resetClassDomain
    logFine[MavenExternalBrainRegion](s"($this) Now CLD: " + this.classdomain)
    logFine[MavenExternalBrainRegion](s"($this) Parent Container CLD : " + this.getParentClassDomain)
    logFine[MavenExternalBrainRegion](s"($this) Local CLD parent     : " + this.getClassDomainParent)

    //-- Add to FSHarvester if needed
    // println(s"***** Delivering base path $basePath to FS Harvester")
    Harvest.onHarvesters[FileSystemHarvester] {
      case fsh =>
        //println(s"***** ----> Doing")
        fsh.deliverDirect(basePath)
    }

  }

  /**
   * Called during load
   */
  def loadRegionClass(cl: String) = {

    logFine[MavenExternalBrainRegion]("Maven load region Class: " + this.classdomain.get.getURLs.length)
    /*try {
      forceUpdateDependencies
    } catch {
      case e: Throwable =>
    }*/
    logFine[MavenExternalBrainRegion]("--- After deps update: " + this.classdomain.get.getURLs.length)
    logFine[MavenExternalBrainRegion]("Create Region Class: " + this.classdomain.get)
    logFine[MavenExternalBrainRegion]("Create Region Class: " + this.classdomain.get.getURLs.toList)
    /*var region = Brain.createRegion(this.classDomain, cl)
    this.addSubRegion(region)*/
    this.classdomain match {
      case Some(cd) =>
        ESome(Brain.createRegion(this.classdomain.get, cl))
      case None =>
        ENone
    }

  }

  this.onShutdown {
    logFine[Brain]("Maven REgion on Shutdown: " + this + " - " + this.classdomain.get)
    this.taintClassDomain
    //this.classDomain = null

    //-- Remove From Harvester
    Harvest.onHarvesters[FileSystemHarvester] {
      case fsh =>
        fsh.cleanResource(basePath)

    }
  }

  this.onAdded {
    case h =>

      logFine[MavenExternalBrainRegion]("Added Region for deps update: " + this + " - " + this.classdomain.get)
      //MavenProjectIndesignWorkspaceReader.resetAllProjects
    /* try {
        forceUpdateDependencies
      } catch {
        case e: Throwable =>
      }*/
  }

  /* this.onGathered {
    case h =>
      
      logFine[Brain]("Gathered Region: " + this + " - " + this.classDomain)
      try {
        forceUpdateDependencies
      } catch {
        case e: Throwable =>
      }
  }*/

  this.onCleaned {
    case h =>
      logFine[Brain]("Maven REgion Cleaned: " + this + " - " + this.classdomain.get)
      this.taintClassDomain
      this.moveToShutdown
  }

  override def rebuildDependencies = {
    this.updateDependencies
  }

  // Region Discovery
  //-----------
  override def discoverRegions: List[String] = {

    this.getDerivedResources[ModuleSourceFile].map { msf => msf.getDiscoveredModules }.flatten.toList.distinct

    /*var regionFiles = new File(this.basePath.path.toFile(), "target/classes/META-INF/indesign/regions.available")
    regionFiles match {
      case rf if (rf.exists() == true) =>

        scala.io.Source.fromFile(regionFiles, "UTF-8").getLines().toList

      case _ => List[String]()
    }*/

  }
  
  override def discoverType[CT <: Any](implicit tag :ClassTag[CT]) =  super.discoverType

}