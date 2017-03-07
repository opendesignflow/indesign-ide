package org.odfi.indesign.ide.core.ui.utils

import org.odfi.wsb.fwapp.views.LibraryView
import com.idyria.osi.tea.errors.ErrorSupport
import com.idyria.osi.vui.html.Td
import com.idyria.osi.tea.errors.TError
import org.odfi.wsb.fwapp.module.datatables.DataTablesView

trait ErrorsHelpView extends LibraryView with DataTablesView {

  this.addLibrary("indesign-ide") {
    case (_, node) =>
      onNode(node) {

        script(createAssetsResolverURI("/indesign-ide/js/ide-errors.js")) {

        }

      }

    case other =>
  }

  def errorsStat(err: ErrorSupport) = {

    currentNode match {

      // Table Case
      //--------------------
      case nodeistd: Td[_, _] =>

        err.hasErrors match {
          case false =>
            classes("positive")
            "icon checkmark" :: i {

            }
            text("No Errors")
          case true =>

            classes("negative")
            "icon remove circle" :: i {

            }
            "popup-activate" :: a("#" + "open-errors-" + err.hashCode()) {
              id("open-errors-" + err.hashCode())
              text(s"${err.errors.size} errors")

            }
            "ui flowing popup top left transition hidden" :: div {
              "ui compact table" :: table {
                thead("File", "Line", "Type", "Message")
                tbody {
                  err.errors.foreach {
                    case error: TError =>
                      tr {
                        td("") {
                          error.file match {
                            case Some(f) => text(f)
                            case None => text("-")
                          }

                        }
                        td("") {

                          error.line match {
                            case Some(f) => text(f.toString)
                            case None => text("-")
                          }

                        }
                        td("") {

                          "popup-activate" :: a("#" + "open-stack-" + error.hashCode()) {
                            id("open-stack-" + error.hashCode())
                            text(s"${error.getClass.getSimpleName}")

                          }
                          "ui flowing popup top left transition hidden" :: div {
                            text("TRR")
                          }

                        }
                        td(error.getLocalizedMessage) {

                        }

                      }
                    case error =>
                      trvalues("-", "-", error.getLocalizedMessage)
                  }
                }
              }
              /*region.errors.foreach {
                error =>
                  "ui error message" :: div {
                    text(error.getLocalizedMessage + s"(${error.getStackTrace()(0).getFileName}:${error.getStackTrace()(0).getLineNumber})")
                  }
              }*/
            }

        }

      // Otherwise, errors
      //--------------
      case other =>
        "popup-activate" :: button("Errors: " + err.errors.size) {
          //text()
        }
        errorPopupTable(err)

    }

  }

  def errorPopupTable(err: ErrorSupport) = {

    "ui flowing popup top left transition hidden" :: div {
      "ui compact table" :: table {
        thead("File", "Line", "Message", "Stack")
        tbody {
          err.errors.foreach {
            case error: TError =>
              tr {
                td("") {
                  error.file match {
                    case Some(f) => text(f)
                    case None => text("-")
                  }

                }
                td("") {

                  error.line match {
                    case Some(f) => text(f.toString)
                    case None => text("-")
                  }

                }
                td(error.getLocalizedMessage) {

                }

                td("") {
                  "popup-activate" :: button(s"${error.getClass.getSimpleName}") {
                    //id("open-stack-" + error.hashCode())
                   // text(s"${error.getClass.getSimpleName}")

                  }
                  "ui flowing popup top left transition hidden" :: div {
                    text {error.getStackTraceString}
                  }
                }

              }
            case error =>
              trvalues("-", "-", error.getLocalizedMessage)
          }
        }
      }
      /*region.errors.foreach {
                error =>
                  "ui error message" :: div {
                    text(error.getLocalizedMessage + s"(${error.getStackTrace()(0).getFileName}:${error.getStackTrace()(0).getLineNumber})")
                  }
              }*/
    }

  }

}