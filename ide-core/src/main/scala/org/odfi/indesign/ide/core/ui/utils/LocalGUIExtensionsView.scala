package org.odfi.indesign.ide.core.ui.utils

import org.odfi.indesign.ide.core.ui.IndesignFrameworkView
import java.io.File
import org.odfi.indesign.core.harvest.Harvest

import org.odfi.indesign.core.module.jfx.JFXRun
import org.odfi.indesign.core.brain.Brain
import javafx.stage.Stage
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import org.odfi.indesign.ide.core.ui.lib.IndesignIDELibView

trait LocalGUIExtensionsView extends IndesignIDELibView {

  def selectSaveFileButton(text: String)(cl: File => Any) = {
    "ui button" :: button(text) {
      onClick {
        JFXRun.onJavaFXBlock {

          var stage = new Stage()
          var fileChooser = new FileChooser();
          fileChooser.setTitle("Select File...");
          //fileChooser.setSelectedExtensionFilter(new ExtensionFilter(extension._1,extension._2))

          //stage.setAlwaysOnTop(true)
          stage.show()

          try {
            fileChooser.showSaveDialog(stage) match {
              case null =>
              case folder =>

                cl(folder)

            }
          } finally {
            stage.close()
          }

          println(s"Finished JFX Block")
        }
      }
    }

  }

  def selectFileOfTypeButton(text: String, extension: (String, String))(cl: File => Any) = {
    "ui button" :: button(text) {
      onClick {
        JFXRun.onJavaFXBlock {

          var stage = new Stage()
          var fileChooser = new FileChooser();
          fileChooser.setTitle("Select File...");
          fileChooser.setSelectedExtensionFilter(new ExtensionFilter(extension._1, extension._2))

          //stage.setAlwaysOnTop(true)
          stage.show()

          try {
            fileChooser.showOpenDialog(stage) match {
              case null =>
              case folder =>

                cl(folder)

            }
          } finally {
            stage.close()
          }
          stage.close()
          println(s"Finished JFX Block")
        }
      }
    }

  }

  def selectDirectoryButton(text: String)(cl: File => Any) = {
    "ui button" :: button(text) {
      onClick {
        JFXRun.onJavaFXBlock {

          var stage = new Stage()
          var dirChooser = new DirectoryChooser();
          dirChooser.setTitle("Select Compilation Output Folder for a region");

          //stage.setAlwaysOnTop(true)
          stage.show()
          //stage.setFocused(true)

          try {
            dirChooser.showDialog(stage) match {
              case null =>
              case folder =>

                cl(folder)

            }
          } finally {
            stage.close()
          }
        }
      }
    }

  }

}