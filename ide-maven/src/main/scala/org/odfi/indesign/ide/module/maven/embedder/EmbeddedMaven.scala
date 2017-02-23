package org.odfi.indesign.ide.module.maven.embedder

import org.codehaus.plexus.classworlds.ClassWorld
import org.codehaus.plexus.PlexusConstants
import org.codehaus.plexus.DefaultContainerConfiguration
import org.apache.maven.extension.internal.CoreExports
import java.util.Collection
import java.util.Collections
import org.codehaus.plexus.DefaultPlexusContainer
import com.google.inject.AbstractModule
import org.slf4j.ILoggerFactory
import org.slf4j.LoggerFactory
import com.idyria.osi.tea.compile.ClassDomainSupport
import org.codehaus.plexus.PlexusContainer
import java.io.File
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.project.MavenProject

import scala.collection.JavaConversions._
import com.idyria.osi.tea.errors.TError
import org.apache.maven.Maven
import org.apache.maven.execution.MavenExecutionRequest
import org.apache.maven.execution.DefaultMavenExecutionRequest
import org.apache.maven.execution.MavenExecutionRequestPopulator
import org.apache.maven.cli.MavenCli
import org.apache.maven.cli.configuration.SettingsXmlConfigurationProcessor
import org.apache.maven.cli.configuration.ConfigurationProcessor
import org.apache.maven.cli.internal.BootstrapCoreExtensionManager
import org.apache.maven.cli.internal.extension.model.io.xpp3.CoreExtensionsXpp3Reader
import org.apache.maven.extension.internal.CoreExtensionEntry
import org.apache.maven.execution.ExecutionListener
import org.apache.maven.execution.AbstractExecutionListener
import org.apache.maven.execution.ExecutionEvent
import java.net.URLClassLoader
import java.net.URI
import java.net.URL
import org.apache.maven.DefaultMaven




class EmbeddedMaven( val configHome : File = new File(new File( sys.props("user.home")), ".m2") ) extends ClassDomainSupport {

  // Classworld
  //---------------
  var classWorld = new ClassWorld("plexus.core", getClass.getClassLoader);
  
  var coreRealm = classWorld.getRealms().iterator().next();

  
  var containerConfig = new DefaultContainerConfiguration()
    .setClassWorld(classWorld)
    .setRealm(coreRealm)
    .setClassPathScanning(PlexusConstants.SCANNING_INDEX)
    .setAutoWiring(true)
    .setName("maven")
  
  //-- Exported artifacts
  val coreEntry = CoreExtensionEntry.discoverFrom( coreRealm )
  val exports = new CoreExports(coreRealm, coreEntry.getExportedArtifacts, coreEntry.getExportedPackages);

  val container = new DefaultPlexusContainer(containerConfig, new AbstractModule() {

    override def configure() =
      {
        bind(classOf[ILoggerFactory]).toInstance(LoggerFactory.getILoggerFactory());
        bind(classOf[CoreExports]).toInstance(exports);
        //bind(classOf[Maven]).to(classOf[DefaultMaven])
      }
  });

  container.setLookupRealm(coreRealm);
  
  /*container.discoverComponents(coreRealm).foreach {
    desc => 
      println("Found component: "+desc)
  }*/
  val maven = container.lookup(classOf[Maven])
  
  // Toolchains (not needed for this usage)
  //-------------
  //val toolchainConfigFile = new File(configHome,"toolchains.xml")
  
  // COnfig Processors
  //------------
  val configurationProcessors = container.lookupMap( classOf[ConfigurationProcessor] );
  
  
  // Request Populator
  //----------
  val executionRequestPopulator = container.lookup( classOf[MavenExecutionRequestPopulator] );


  // Extensinos Resolver
  //----------------
  val extensionsResolver = container.lookup( classOf[BootstrapCoreExtensionManager] );
  val extensionsConfigParser = new CoreExtensionsXpp3Reader();
  
  
  // Low Level calls
  //---------------
  def inMavenRealm[T](cl: PlexusContainer => T) = {
    this.withClassLoader(container.getContainerRealm) {
      cl(container)
    }
  }

  // High Level Utils
  //-------------------

  /**
   * 
   */
  def buildProject(pom: File) : ErrorOption[MavenProject]  = {
    inMavenRealm {
      container =>
        var builder = container.lookup(classOf[ProjectBuilder])

        var pbr = new DefaultProjectBuildingRequest
        pbr.setRepositorySession(AetherResolver.session)
      
        
        try {
          var res = builder.build(pom, pbr)
     
          var someRes = ESome(res.getProject)
          /*someRes.errors = res.getProblems.toList.map {
            problem => 
              var e = new TError(problem.getException)
              e.column = Some(problem.getColumnNumber)
              e.line = Some(problem.getLineNumber)
              e.file = Some(problem.getSource)
              e
          }*/
          //println("Returning some")
          someRes
        } catch {
          case e : Throwable => 
            EError(e)
            
        }
       
    }

  }
  
  
  def executeGoal(project:MavenProject,goals:List[String]) = {
    
    
    coreRealm.getURLs.foreach {
      url => 
        println(s"REalm: "+url)
    }
    
   
    
    
    //-- Execution request
    var req = new DefaultMavenExecutionRequest
    
  
    //-- Listener
    var list = new AbstractExecutionListener {
      
      override def projectFailed( event : ExecutionEvent ) = {
        println(s"***Failed project: "+event)
        println(s"*** Res: "+event.getSession.getResult)
        event.getSession.getResult.getExceptions.foreach {
          err => 
            println(s"Error: "+err.getLocalizedMessage)
        }
        
      }
      
    }
    req.setExecutionListener(list)
 
    
    //--
    /*val coreEntry = CoreExtensionEntry.discoverFrom( coreRealm )
    coreEntry.getExportedArtifacts.foreach {
      s => 
        println(s"EA: "+s)
    }*/
    
    //-- Poupulations
    executionRequestPopulator.populateDefaults(req)
    
   // extensionsResolver.loadCoreExtensions( req, Collections.emptySet(), Collections.emptyList() );
    
    //req.setUpdateSnapshots(true)
    req.setBaseDirectory(project.getBasedir)
    req.setPom(project.getFile)
    
    //req.setLocalRepository(AetherResolver.)
    
    req.setGoals(goals)
    
    maven.execute(req) match {
      case res if(res.getExceptions.size>0) => 
        EError(res.getExceptions.get(0))
        
      case res => 
        ESome(res)
    }
    
    
    
    //println("Result: "+res.getExceptions.size())
    
  }

}