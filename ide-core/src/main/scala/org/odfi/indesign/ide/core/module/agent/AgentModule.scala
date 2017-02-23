package org.odfi.indesign.ide.core.module.agent

import org.odfi.indesign.core.artifactresolver.AetherResolver
import org.odfi.indesign.core.module.IndesignModule
import com.idyria.osi.tea.logging.TLog
import org.odfi.indesign.core.artifactresolver.ArtifactResolverModule
import scala.io.Source
import java.lang.management.ManagementFactory
import org.odfi.indesign.core.config.ConfigSupport
import java.io.File
import com.idyria.osi.tea.compile.ClassDomain
import org.odfi.indesign.ide.core.module.eclipse.EclipseModule
import com.idyria.osi.tea.os.OSDetector
import org.odfi.indesign.ide.agent.IndesignIDEAgent
import org.odfi.indesign.core.main.IndesignPlatorm

class AgentModule {

}
object AgentModule extends IndesignModule with ConfigSupport {

  // WARNING
  // Replacing main CL with a URL CL
  var agentMainCL = new ClassDomain(Thread.currentThread().getContextClassLoader)
  Thread.currentThread().setContextClassLoader(agentMainCL)

  /**
   * Load configuration parameters from command line
   */
  def apply(args: Array[String]) = {

    // Eclipse Workspace
    //-----------

    this
  }

  var projectVersion = ""

  def loadLocal = {
    //-- Use Aether to find agent
    AetherResolver.getArtifactPath("org.odfi.indesign.ide", "indesign-ide-agent", projectVersion) match {
      case Some(agentFile) =>

        logInfo[AgentModule]("Found Agent JAR at: " + agentFile)

        //-- Load it 
        var nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        var p = nameOfRunningVM.indexOf('@');
        var pid = nameOfRunningVM.substring(0, p);

        //println(s"VM pid: " + pid)

        //-- Get VirtualMachine
        try {

          //-- Analyse class path
          //-- Look for something that looks like tool.jar
          sys.props("java.class.path").contains("lib" + File.separator + "tools.jar") match {
            case true =>

            // NO tools.jar, search for JDK
            case false =>

              var exeExtension = OSDetector.isWindows() match { case true => ".exe"; case false => ""; }
              var javaHome = new File(sys.props("java.home"))

              var toolsLib = javaHome.getName.contains("jre") match {

                //-- Inside a JRE, look for ../bin/javaws
                case true if (new File(javaHome, ".." + File.separator + "bin" + File.separator + "javaw" + exeExtension).exists()) =>

                  //-- Look for tools
                  var jdkPath = new File(javaHome, ".." + File.separator)
                  var tools = new File(jdkPath, "lib" + File.separator + "tools.jar")
                  tools.exists() match {
                    case true =>
                      Some(tools)
                    case false =>
                      System.err.println(s"Cannot find  tools.jar in jdk: " + tools.getCanonicalPath)
                      None
                  }

                //-- Inside a JRE , look for ../jdk
                case true =>

                  //-- get JRE version and try to find a JDK
                  var jreVersion = new File(sys.props("java.home")).getName.replace("jre", "")
                  var jdkPath = new File(new File(sys.props("java.home")).getParentFile, "jdk" + jreVersion)
                  jdkPath.exists() match {
                    case true =>
                      Some(new File(jdkPath, List("lib", "tools.jar").mkString(File.separator)))

                    //System.err.println(s"Adding")
                    //agentMainCL.addURL(new File(jdkPath, List("lib", "tools.jar").mkString(File.separator)).toURI().toURL())
                    case false =>
                      System.err.println(s"Cannot find JDK matching JRE: " + jreVersion)
                      None
                  }

                //-- Inside JDK
                case false =>
                  Some(new File(javaHome, List("lib", "tools.jar").mkString(File.separator)))
                //agentMainCL.addURL(new File(new File(sys.props("java.home")), List("lib", "tools.jar").mkString(File.separator)).toURI().toURL())
              }

              // Check Tools was found
              //----------
              toolsLib match {
                case Some(toolsFile) =>
                  agentMainCL.addURL(toolsFile.toURI().toURL())
                case None =>
                  sys.error("Could not find tools.jar, java home is: " + javaHome.getCanonicalPath)
              }
          }

          /*println(s"Tool path: " + toolsPath)
          sys.props("java.home").contains("jdk")*/

          //-- Load Class
          var vmClass = Thread.currentThread().getContextClassLoader.loadClass("com.sun.tools.attach.VirtualMachine")

          //-- Get attach method
          var attachMethod = vmClass.getMethod("attach", classOf[String])

          //-- Attach
          var vm = attachMethod.invoke(null, pid)

          //-- Call loadAgent
          var loadAgentMethod = vm.getClass.getMethod("loadAgent", classOf[String])
          loadAgentMethod.invoke(vm, agentFile.getCanonicalPath)

          //-- Check Instances
          println("Start time of Agent: " + IndesignIDEAgent.startTime)

          //-- Restarting
          IndesignIDEAgent.on("restart") {
            println("Restart....")
            restartApplication
          }

          //vm.loadAgent();

        } catch {
          case e: ClassNotFoundException =>
            System.err.println(s"Cannot load Agent because com.sun.tools.attach.VirtualMachine was not fund, feature only supported on Oracle VM")
            throw e
          case e: Throwable =>
            System.err.println(s"Cannot load Agent because an error occured: " + e.getLocalizedMessage)
            throw e
        }

      case None =>

        logWarn[AgentModule]("Could not find Agent Jar")
    }

  }

  // Lifecycle
  //----------------

  /**
   * Add Agent
   */
  this.onLoad {

    TLog.setLevel(classOf[AgentModule], TLog.Level.FULL)

    //-- Requires
    this.requireModule(ArtifactResolverModule)
    this.requireModule(EclipseModule)

    //-- Get project version
    projectVersion = Source.fromURL(getClass.getClassLoader.getResource("org.odfi.indesign.ide.version.txt")).mkString

    logInfo[AgentModule]("Project version: " + projectVersion)

  }

  this.onInit {

  }

  // Restart Stuff
  //----------------
  def restartApplication = {
    println("Restarting application...")
    //IndesignPlatorm.stop
  }

}