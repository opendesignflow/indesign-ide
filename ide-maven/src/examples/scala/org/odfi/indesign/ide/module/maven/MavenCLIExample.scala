package org.odfi.indesign.ide.module.maven

import java.io.File
import org.codehaus.plexus.DefaultPlexusContainer
import org.apache.maven.model.building.ModelBuilder
import scala.collection.JavaConversions._
import org.apache.maven.execution.DefaultMavenExecutionRequest
import org.apache.maven.cli.MavenCli
import org.odfi.indesign.ide.module.maven.embedder.EmbeddedMaven
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.odfi.indesign.core.artifactresolver.AetherResolver

object MavenCLIExample extends App {

  // Project
  var projectFolder = new File("src/test/testFS/maven-app-extrasource")

  // var me2 = new EmbeddedMaven
  var me = MavenModule.maven

  /*me.inMavenRealm {
    container => 
      
    var projectBuilder = me.container.lookup(classOf[ProjectBuilder])
     var pbr = new DefaultProjectBuildingRequest
     pbr.setRepositorySession(AetherResolver.session)
     
    projectBuilder.build(new File(projectFolder, "pom.xml"),pbr)
    
  }*/
  me.buildProject(new File(projectFolder, "pom.xml")) match {
    case ESome(project) =>
      println("Build ok")

      project.getCompileSourceRoots.foreach {
        root =>

          println(s"Compile source from: " + root)
      }

      /* var em = new MavenEmbedded
      sys.props.put("maven.multiModuleProjectDirectory", project.getBasedir.getCanonicalPath)
      em.doMain(Array("compile"), project.getBasedir.getCanonicalPath, System.out, System.err)
      */

      project.getDependencies.foreach {
        dep =>
          println(s"DEP: " + dep.getArtifactId)
      }

      me.executeGoal(project, List("generate-sources")) match {
        case ESome(okres) =>
          println("Goal run ok")

          project.getCompileSourceRoots.foreach {
            root =>

              println(s"P Compile source from: " + root)
          }
          
          println(s"Res proj: "+okres.getProject)
          okres.getProject.getCompileSourceRoots.foreach {
            root =>

              println(s"RP Compile source from: " + root)
          }

          //okres.getBuildSummary(project).getProject.getCompileSourceRoots
        case EError(error) =>
          println("Goal run error")
          error.printStackTrace(System.out)
      }

    case EError(error) =>
      println("Error: " + error.getLocalizedMessage)

    case ENone =>
      println("NOthing")
  }

  println(s"Done Building")
  /* var result = MavenModule.buildMavenProject(new File(projectFolder, "pom.xml"))
  result.getProblems.size() match {
    case 0 =>

    case other =>

      println("Model is ok")
      var model = result.getProject
      println("ArtifactId: " + model.getArtifactId)

      model.getDependencies.toList.foreach {
        dep =>
          println("Found dep: " + dep.toString)
      }
  }*/
}