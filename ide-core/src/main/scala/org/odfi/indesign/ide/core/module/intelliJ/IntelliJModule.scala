package org.odfi.indesign.ide.core.module.intelliJ

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.module.HarvesterModule
import java.io.File
import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.nio.file.Path

object IntelliJModule extends HarvesterModule {

  override def getDisplayName = "IntelliJ"
  
  this.onInit {
    addDerivedResource(new IntelliJOverview)
  }
  
  this.onStop {
    this.cleanDerivedResourcesOfType[IntelliJOverview]
  }

  override def doHarvest = {

    var programMenu = new File("""C:\ProgramData\Microsoft\Windows\Start Menu\Programs\JetBrains""")
    programMenu.exists() match {
      case true =>

        programMenu.listFiles().filter(file => file.getName.startsWith("""IntelliJ IDEA Community Edition""")).foreach {
          intelliJLink =>
            gather(new IntelliJStartLink(intelliJLink.toPath()))
        }

      case false =>
    }
  }

}

class IntelliJStartLink(p: Path) extends HarvestedFile(p) {

}