package org.odfi.indesign.ide.agent

import java.lang.management.ManagementFactory
import com.sun.tools.attach.VirtualMachine
import java.io.File


object TryAgentLoad extends App {

  //-- Package first!
  var pb = new ProcessBuilder
  //pb.inheritIO()
  pb.environment().put("JAVA_HOME", new File(System.getProperty("java.home")).getCanonicalFile.getParentFile.getCanonicalPath)
  println("Java home: "+System.getProperty("java.home"))
  pb.command("E:/Common/tools/apache-maven-3.3.9/bin/mvn.cmd","-Dskip=true","-DskipTests=true","package")
  var process = pb.start()
  process.waitFor()
  
  
  var nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
  var p = nameOfRunningVM.indexOf('@');
  var pid = nameOfRunningVM.substring(0, p);

  println(s"VM pid: "+pid)
  

    var vm = VirtualMachine.attach(pid);
    vm.loadAgent(new File("target/indesign-ide-agent-1.0.0-SNAPSHOT.jar").getCanonicalPath);
  
  
    
    vm.detach();
    
   // println(s"Waiting for kill...")
    Console.readLine()
    
    //-- Now Load Reloased class and call hello on it
    var r = new ReloadedClass
    while(true) {
      
      r.hello
       Console.readLine()
    }
    
    
  

}