package org.odfi.indesign.ide.core.module.intelliJ

import org.odfi.indesign.ide.core.ui.main.IDEBaseView

class IntelliJOverview extends IDEBaseView {

  this.placePage {
    div {

      "ui info message" :: "This page will help you check the IntelliJ start links"

      "ui datatable table " :: table {
        thead("Name", "Location")
        tbody {

          IntelliJModule.getResourcesOfType[IntelliJStartLink].foreach {
            link =>
              tr {
                td(link.path.getFileName.toString()) {

                }
                td(link.path.toFile().getCanonicalPath) {

                }
              }
          }

        }

      }

    }
  }
}