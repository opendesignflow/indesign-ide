package org.odfi.indesign.ide.core.project.jvm

import org.odfi.indesign.ide.core.project.Project
import scala.reflect.ClassTag
import org.odfi.indesign.ide.core.project.BuildableProject

trait JavaOutputProject extends BuildableProject {
  
  // Type Discover
  //------------------
  
  def discoverType[CT <: Any](implicit tag :ClassTag[CT]) = {
    
    List[Class[CT]]()
    
  }
  
}