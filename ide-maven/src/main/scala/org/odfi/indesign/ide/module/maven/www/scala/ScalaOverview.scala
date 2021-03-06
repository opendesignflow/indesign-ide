package org.odfi.indesign.ide.module.maven.www.scala

import org.odfi.indesign.ide.www.IDEBaseView
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.ide.module.scala.ScalaAppHarvester
import org.odfi.indesign.ide.module.scala.ScalaAppSourceFile

class ScalaOverview extends IDEBaseView {



  this.placePage {

    div {
      h2("Scala Overview") {

      }

      "ui celled table" :: table {

        thead {
          th("App File") {

          }
          th("App Name") {

          }
          th("Action") {

          }
        }

        tbody {

          Harvest.onHarvesters[ScalaAppHarvester] {
            case h =>
              h.onResources[ScalaAppSourceFile] {
                sf =>
                  tr {
                    td(sf.path.toFile().getCanonicalPath) {

                    }
                    td(sf.getDefinedObjects.head) {

                    }
                    td("") {

                      "ui button" :: button("Run") {
                        onClick {
                          sf.run
                        }
                      }
                    }
                  }
              }

          }
        }
      }

    }

  }
}