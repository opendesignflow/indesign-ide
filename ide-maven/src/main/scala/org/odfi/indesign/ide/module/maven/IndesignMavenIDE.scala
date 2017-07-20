package org.odfi.indesign.ide.module.maven

import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.ide.core.IndesignIDEPlatform 
import org.odfi.indesign.ide.www.IDEGUI

object IndesignMavenIDE extends App {
  
  IndesignPlatorm use IndesignIDEPlatform 
  
  IndesignPlatorm use MavenModule
  
  IDEGUI.basePath= "/maven-ide"
  
  IndesignPlatorm.start
  
  
}