package org.odfi.indesign.ide.core.ui.utils

import org.odfi.wsb.fwapp.views.LibraryView
import com.idyria.osi.tea.errors.ErrorSupport
import com.idyria.osi.vui.html.Td
import com.idyria.osi.tea.errors.TError

trait ErrorsHelpView extends LibraryView {

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
            "popup-activate" :: a("#") {
              text(s"${err.errors.size} errors")

            }
            "ui flowing popup top left transition hidden" :: div {
              "ui compact table" :: table {
                thead("File", "Line", "Message")
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
        "popup-activate" :: a("#") {
          text("Errors: " + err.errors.size)
        }
        errorPopupTable(err)

    }

  }

  def errorPopupTable(err: ErrorSupport) = {

    "ui flowing popup top left transition hidden" :: div {
      "ui compact table" :: table {
        thead("File", "Line", "Message")
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