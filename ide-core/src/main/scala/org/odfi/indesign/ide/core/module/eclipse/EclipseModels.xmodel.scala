package org.odfi.indesign.ide.core.module.eclipse

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait


@producers(Array(
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object EclipseModels extends ModelBuilder{
  
  // Project Description
  //------------------
  "projectDescription" is {
    withTrait(classOf[STAXSyncTrait])
    
    "name" ofType "string"
    "comment" ofType "string"
    
    "projects" is {
      "project" multiple "string"
    }
    
    // Build
    //-----------
    "buildSpec" is {
      "buildCommand" multiple {
        "name" ofType "string"
        "arguments" is {
          "argument" multiple "string"
        }
      }
    }
    
    // Nature
    //-------------
    "natures" is {
      "nature" multiple "string"
    }
    
  }
  
}