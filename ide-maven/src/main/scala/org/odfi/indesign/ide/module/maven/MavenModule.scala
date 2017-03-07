package org.odfi.indesign.ide.module.maven

import java.io.File

import org.apache.maven.Maven
import org.apache.maven.cli.MavenCli
import org.apache.maven.model.building.ModelBuilder
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.apache.maven.project.ProjectBuilder
import org.codehaus.plexus.DefaultPlexusContainer
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.brain.ExternalBrainRegion
import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.ide.module.maven.region.MavenExternalBrainRegionBuilder
import org.odfi.indesign.ide.module.maven.ui.MavenOverview
import org.odfi.indesign.ide.core.project.ProjectsHarvester
import org.odfi.indesign.core.artifactresolver.ArtifactResolverModule
import org.odfi.indesign.ide.module.maven.embedder.EmbeddedMaven
import org.apache.maven.project.MavenProject
import org.odfi.indesign.ide.core.project.ProjectModule
import org.odfi.indesign.core.harvest.fs.FileSystemHarvester
import org.odfi.indesign.ide.module.maven.ui.MavenGUI
import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.odfi.indesign.ide.module.maven.resolver.MavenProjectWorkspaceReader

object MavenModule extends IndesignModule {

  override def getDisplayName = "Maven"

  // Maven EmbedderInterface
  //---------------
  
  var maven =  new EmbeddedMaven
  
  /*var plexusContainer = new DefaultPlexusContainer();

  var modelBuilder = plexusContainer.lookup(classOf[ModelBuilder])
  //var clManager = plexusContainer.lookup(classOf[ClassRealmManager])
  var projectBuilder = plexusContainer.lookup(classOf[ProjectBuilder])
  
  def getMavenInstance = plexusContainer.lookup(classOf[Maven])

  var mavenCli = new MavenCli*/

  // Utils
  //----------------
  def buildMavenProject(pomFile: File) : ErrorOption[MavenProject]  = {

    

    maven.buildProject(pomFile)
    
    //var pbr = new DefaultProjectBuildingRequest
 
    //projectBuilder.build(pomFile,pbr)
    
   /* var buildRequest = new DefaultModelBuildingRequest
    buildRequest.setPomFile(pomFile)
    buildRequest.setLocationTracking(true)

    modelBuilder.build(buildRequest)*/
  }

  /**
   * Harvester of maven proejcts to start building
   */
  /*object ProjectsHarvester extends Harvester[HarvestedFile,MavenProjectResource] {
    
    
    def doHarvest = {
      
    }
    
    override def deliver(f: HarvestedFile) = {
      
      f match {
        case mf:MavenProjectResource => 
          gather(mf)
          println(s"Gathering Subregion")
          
          mf.onAdded {
            case h if (h==ProjectsHarvester.this) => 
              println("Added to harvester: "+h.hashCode())
              MavenModule.this.addSubRegion(new MavenProjectBuilder(mf))
            case _ => 
              println(s"Added to another Harvester")
          }
          
          true
        case _ => false
      }
      
    }
    
  }
  
  
  Harvest.registerAutoHarvesterObject(classOf[MavenProjectHarvester], ProjectsHarvester)*/

  //TLog.setLevel(classOf[MavenProjectIndesignWorkspaceReader], TLog.Level.FULL)

  this.onLoad {

    requireModule(ArtifactResolverModule)
    requireModule(ProjectModule)
    requireModule(MavenGUI)
  
    //-- Register Maven Region Builder
    //ExternalBrainRegion.addBuilder(new MavenExternalBrainRegionBuilder)

    println("Loading Maven : " + MavenModule.getClass.getClassLoader)
    // Harvest.registerAutoHarvesterClass(classOf[FileSystemHarvester], classOf[MavenProjectHarvester])
    //Harvest.registerAutoHarvesterClass(classOf[FileSystemHarvester], classOf[MavenProjectHarvester])

    FileSystemHarvester --> MavenProjectHarvester
    ProjectsHarvester --> MavenProjectHarvester

    // MavenProjectIndesignWorkspaceReader.resetAllProjects
    
    AetherResolver.session.setWorkspaceReader(MavenProjectWorkspaceReader)

    //println("Loading Maven WWW View---------: "+WWWViewHarvester.hashCode())

    // WWWViewHarvester.deliverDirect(new MavenOverview)

  }

  this.onStart {

    // Create Maven embbeded
    //------------

    // Register IDE UI
    //------------------
    var overview = new MavenOverview
    overview.deriveFrom(this)

    /*println("Start on Maven: " + Harvest.getHarvesters[WWWViewHarvester])
    Harvest.getHarvesters[WWWViewHarvester] match {
      case Some(h) =>
        h.last.deliverDirect(new MavenOverview)
      case _ =>
    }*/
  }

  this.onShutdown {
    println("Shutting down Maven module: " + Brain.getResources)
    MavenProjectHarvester.clean
    
    //-- Register Maven Region Builder
    //ExternalBrainRegion.addBuilder(new MavenExternalBrainRegionBuilder)

    // Find All Maven Regions in Brain and remove them
    //------------
    /*Brain.getResourcesOfLazyType[MavenExternalBrainRegion].drop(1).foreach {
      case r if (r.isTainted) =>
        println("Cleaning from brain: " + r.isTainted)
        Brain.cleanResource(r)
      case _ =>
    }*/

  }

  // Ressource Keep
  //-------------------
  this.onKept {
    case h =>
    /* println("Maven Module kept: " + Harvest.getHarvesters[WWWViewHarvester])
      Harvest.getHarvesters[WWWViewHarvester] match {
        case Some(h) =>
          h.last.deliverDirect(new MavenOverview)
        case _ =>
      }*/
  }

  def load = {
    //FileSystemHarvester.addChildHarvester(new POMFileHarvester)
    //println("Loading Maven")
    //Harvest.registerAutoHarvesterClass(classOf[FileSystemHarvester], classOf[MavenProjectHarvester])
  }
}

