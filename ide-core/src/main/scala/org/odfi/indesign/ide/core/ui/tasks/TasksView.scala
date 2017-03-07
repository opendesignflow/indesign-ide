package org.odfi.indesign.ide.core.ui.tasks

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.indesign.core.heart.Heart
import org.odfi.indesign.core.heart.HeartTask
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.indesign.ide.core.ui.utils.ErrorsHelpView
import org.odfi.indesign.core.harvest.HarvestedResource

trait TasksView extends FWAppFrameworkView with ErrorsHelpView {

  this.addLibrary("indesign-ide") {
    case (_, target) =>

      onNode(target) {

        script(createAssetsResolverURI("/indesign-ide/js/ide-tasks.js")) {

        }

      }

  }

  // Utilities
  //--------------

  def resourceTaskButton(r: HarvestedResource, id: String)(startName: String, stopName: String)(content: HeartTask[Any] => Any) = {
    taskButton(r.getId + "." + id)(startName, stopName)(content)
  }

  /**
   * Button for fast task creation
   */
  def taskButton(id: String)(startName: String, stopName: String)(content: HeartTask[Any] => Any) = {

    // Check Task is not running
    //--------------------

    Heart.running(id) match {
      case Some(t) if (t.isRunning) =>

        //-- Create Button

        "ui button" :: button("") {

          "settings icon" :: i {

          }

          text(stopName)
          /*onClickReload {
            
            Heart.killTask(t)
          }*/

        }

      case other =>

        //-- Create Button
        "ui button" :: button("") {

          "settings icon" :: i {

          }

          text(startName)

          onClickReload {

            println(s"*** create and run task")
            //-- Create task
            var task = new HeartTask[Any] {

              def getId = id

              def doTask = {
                println(s"*** Running task")
                content(this)
              }

            }

            //-- Run
            Heart.pump(task)
          }

        }

        //-- last error
        other match {
          case Some(task) =>
            a("#") {
              text("Last Run")
              errorsStat(task)
            }
          case None =>
        }
    }

    //-- Create Wrapper
    /*var taskButton = new HeartTaskButton(hButton, task)

    // Now setup run
    switchToNode(hButton, {

      //-- Get an Action String
      var actionString = getActionString {
        taskButton.submitTask
      }

      +@("onclick" -> s"indesign.heart.launchTask(this,'${taskButton.submitTask}')")

    })

    taskButton*/
  }
}