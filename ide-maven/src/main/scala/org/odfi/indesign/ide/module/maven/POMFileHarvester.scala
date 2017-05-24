package org.odfi.indesign.ide.module.maven

import java.nio.file.Path

import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.harvest.fs.HarvestedFile

class POMFileHarvester extends Harvester {


  /**
   * Reacts on pom.xml file
   */
  this.onDeliverFor[HarvestedFile] {

    case r =>

      //if (r.
      r.path.endsWith("pom.xml") match {
        case true =>
          logFine(s"Delivered POM FILE: " + r.path.toUri())
          gather(new POMFileResource(r.path).deriveFrom(r))
          true
        case false =>
          false
      }

  }
}

class POMFileResource(p: Path) extends HarvestedFile(p) {

}