package org.odfi.indesign.ide.module.maven

import java.io.File

object MavenExecuteCatchStdout extends App {
  
  var projectFolder = new File("src/test/testFS/maven-app-oneerror")
  
  var me = MavenModule.maven
  
  me.buildProject(new File(projectFolder, "pom.xml")) match {
    case ESome(project) =>
      println("Build ok")
      
      me.executeGoal(project, List("compile")) 
      
    case other =>    
  }
  
  
}