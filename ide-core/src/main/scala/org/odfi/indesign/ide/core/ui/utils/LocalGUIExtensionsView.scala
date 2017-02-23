package org.odfi.indesign.ide.core.ui.utils

import org.odfi.indesign.ide.core.ui.IndesignFrameworkView
import java.io.File
import org.odfi.indesign.core.harvest.Harvest

import org.odfi.indesign.core.module.jfx.JFXRun
import org.odfi.indesign.core.brain.Brain
import javafx.stage.Stage
import javafx.stage.DirectoryChooser

trait LocalGUIExtensionsView extends IndesignFrameworkView {

  def selectDirectoryButton(text: String)(cl: File => Any) = {
    "ui button" :: button(text) {
      onClick {
        JFXRun.onJavaFX {
          var stage = new Stage()
          var dirChooser = new DirectoryChooser();
          dirChooser.setTitle("Select Compilation Output Folder for a region");

          dirChooser.showDialog(stage) match {
            case null =>
            case folder =>

              cl(folder)

          }
        }
      }
    }

  }

}