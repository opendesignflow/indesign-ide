package org.odfi.indesign.ide.core.sources.outline

import com.idyria.osi.ooxoo.model.ModelBuilder

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.Model
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import com.idyria.osi.ooxoo.lib.json.JSonUtilTrait

@producers(Array(
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object OutlineModel extends ModelBuilder {

  val namedAndDescriptions = "NamedAndDescriptionTrait" is {
    isTrait
    "Name" ofType ("string")
    "Description" ofType ("cdata")
  }

  "Outline" is {

    withTrait(classOf[STAXSyncTrait])
    withTrait(classOf[JSonUtilTrait])
    withTrait(namedAndDescriptions)

    "OutlineSection" multiple {
      withTrait(namedAndDescriptions)
    }

    "OutlineElement" multiple {

      withTrait(namedAndDescriptions)

      "Type" ofType ("string")

      "Hint" multiple {
        ofType("string")
        withTrait(namedAndDescriptions)
        "Value" ofType("string")

      }
    }
  }
}