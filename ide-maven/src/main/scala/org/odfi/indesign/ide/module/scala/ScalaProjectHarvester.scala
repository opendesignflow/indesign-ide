package org.odfi.indesign.ide.module.scala

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import org.odfi.indesign.core.module.lucene.LuceneResourceProvider

object ScalaProjectHarvester extends Harvester with LuceneResourceProvider {

  this.addChildHarvester(new ScalaAppHarvester)
  this.addChildHarvester(new ScalaIndesignModuleHarvester)
 

  this.onDeliverFor[HarvestedFile] {

    case r =>

    r.path.toString.endsWith(".scala") match {
      case true =>

        gather(new ScalaSourceFile(r.path).deriveFrom(r))
        true
      case _ =>
        false
    }
  }

}