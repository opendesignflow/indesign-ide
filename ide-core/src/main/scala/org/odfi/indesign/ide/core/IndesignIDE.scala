package org.odfi.indesign.ide.core


import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.wsb.fwapp.FWappTreeBuilder
import org.odfi.indesign.core.config.Config
import org.odfi.indesign.core.config.ooxoo.OOXOOFSConfigImplementation
import java.io.File
import org.odfi.indesign.ide.core.module.agent.AgentModule
import org.odfi.indesign.ide.core.services.stash.StashTree
import org.odfi.indesign.ide.core.ui.main.WelcomeIDEView
import org.odfi.wsb.fwapp.FWappApp

object IndesignIDE extends App with FWappTreeBuilder {
  
  IndesignPlatorm.prepareDefault
  
  // Developement options
  //--------------
  IndesignPlatorm use AgentModule
  
  // Setup Indesign
  //---------------
  
  IndesignPlatorm use Config
  Config.setImplementation(new OOXOOFSConfigImplementation(new File("indesign-ide-config")))
  
  
  // Create FWapp Server
  //---------------
  var server = new FWappApp 
  server.listen(8400)
  
  IndesignPlatorm use server
  
  
  
  // App Map
  //-------------
  server ::  "/"  is {
    
    view (new WelcomeIDEView)
    
    
    "/stash" uses new StashTree
    
  }
  
  
  
  IndesignPlatorm.start
  
  
}