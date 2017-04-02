package org.odfi.indesign.ide.www.plugins

import org.odfi.indesign.ide.www.IDEBaseView
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.core.brain.ExternalBrainRegion
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.module.jfx.JFXRun
import javafx.stage.Stage
import javafx.stage.DirectoryChooser
import org.odfi.indesign.core.brain.BrainRegion
import org.odfi.indesign.core.harvest.Harvester
import org.odfi.indesign.core.brain.RegionClassName
import org.odfi.indesign.ide.core.ui.utils.ErrorsHelpView
import org.odfi.indesign.core.brain.ExternalBrainRegion

class PluginsListView extends IDEBaseView with ErrorsHelpView {

  this.placePage {
    div {

      ribbonHeaderDiv("blue", "Plugins List") {

        // Table of Brain External Regions which are plugins sortable
        //----------
        "ui celled  table treetable" :: table {
          id("region-table")
          thead {
            tr {
              "sorted descending" :: th("Region") {

              }

              th("Type") {

              }

              th("State") {

              }
              th("Error") {

              }

              th("Action") {

              }
            }
          }
          tbody {

            def brainRegionLine(region: BrainRegion) = {
              "leaf expanded" :: tr {
                // Id
                +@("data-tt-id" -> region.resourceHierarchyName())

                // Parent
                +@("data-tt-parent-id" -> region.resourceHierarchyName(withoutSelf = true))

                // name
                "collapsing" :: td("") {
                  importHTML(<i class="block layout icon"></i>)
                  importHTML(<i class="settings icon ui popup-activate"></i>)

                  "ui flowing popup top left transition hidden" :: div {
                    h4("Config") {

                    }
                    //configTable(region.config)
                  }

                  span {
                    textContent(region.getName)
                  }

                }

                //-- type
                classOf[ExternalBrainRegion].isInstance(region) match {
                  case true =>
                    td("") {
                      span(textContent("External: " + region.getClass.getSimpleName))
                      "text" :: p {
                        textContent(region.getClass.getClassLoader.toString)
                      }
                      "text" :: p {
                        textContent(region.asInstanceOf[ExternalBrainRegion].getId)
                      }
                      "text" :: p {
                        textContent(region.asInstanceOf[ExternalBrainRegion].getRegionPath)
                      }
                      /*"text" :: p {
                      textContent(s"Builder tainted: " + region.asInstanceOf[ExternalBrainRegion].regionBuilder.get.isTainted)
                    }*/

                    }
                  case false =>
                    td("Internal") {

                    }
                }

                //-- Lifecycle State
                region.currentState match {
                  case None =>
                    "negative" :: td("Not in Lifecylce") {

                    }
                  case Some(s) =>
                    td("") {
                      "icon checkmark" :: i {

                      }
                      span(textContent(s))

                    }
                }

                //-- Error State
                td("") {
                  region.hasErrors match {
                    case true =>
                      classes("negative")
                      span(textContent(region.errors.size.toString))
                      
                      "ui red button popup-activate" :: button(region.errors.size.toString) {
                      }
                      "ui flowing popup top left transition hidden" :: div {
                        region.errors.foreach {
                          error =>
                            "ui error message" :: div {
                              text(error.getLocalizedMessage+s"(${error.getStackTrace()(0).getFileName}:${error.getStackTrace()(0).getLineNumber})")
                            }
                        }
                      }
                    case false =>
                      classes("positive")
                      importHTML(<i class="icon checkmark"></i>)
                      span(textContent("None"))
                  }

                }

                //-- Actions
                td("") {

                  // Remove from config if in config
                  classOf[ExternalBrainRegion].isInstance(region) match {
                    case true =>

                      "ui button" :: button("Remove from Config") {

                      }
                      "ui button" :: button("Reload") {
                        //reload
                        onClick {
                          region.asInstanceOf[ExternalBrainRegion].reload
                        }
                      }
                      "ui button popup-activate" :: button("Loaded Regions") {

                      }
                      "ui flowing popup top left transition hidden" :: div {

                        //-- Make list to add 
                        var available = region.asInstanceOf[ExternalBrainRegion].discoverRegions
                        //var available = region.getDerivedResources[ModuleSourceFile].map { msf => msf.getDiscoveredModules }.flatten.toList.distinct
                        // println(s"Discovered: $available")

                        //var regionsClasses = r.derivedResources.map { rs => rs.getClass.getName.trim }.toList
                        var regionsClasses = region.asInstanceOf[ExternalBrainRegion].configKey.get.values.drop(1).map { v => v.toString }
                        var remaining = available.filter(a => !regionsClasses.contains(a))

                        remaining.size match {
                          case 0 =>
                            "ui info message" :: div {
                              textContent("No Regions available to add")
                            }
                          case other =>
                            //tempBufferSelect(s"${region.name}-regionLoad", remaining.map { name => (name, name.split("\\.").last) })

                            "ui info error" :: div {

                            }
                            "ui icon button" :: button("") {
                              +@("data-content" -> "Add Region to be loaded")
                              +@("reload" -> "true")
                              "add icon" :: i()

                              onClick {
                                /*var name = getTempBufferValue[String](s"${region.name}-regionLoad") match {
                                  case None =>
                                    remaining.head
                                  case Some(v) => v
                                }
                                region.asInstanceOf[ExternalBrainRegion].addRegionClass(name.toString)
                                Brain.config.get.resyncToFile*/
                              }
                            }
                        }

                        //-- Regions Tables
                        region.hasDerivedResourceOfType[BrainRegion] match {
                          case false =>
                            "ui info message" :: div {
                              textContent("No Regions were loaded so far")
                            }
                          case true =>
                            "ui table" :: table {
                              thead {
                                th("Class") {

                                }
                                th("Action") {

                                }
                              }
                              tbody {
                                region.onDerivedResources[BrainRegion] {
                                  case res =>
                                    tr {
                                      td(res.getClass.getName) {

                                      }
                                      td("") {

                                      }
                                    }

                                }
                              }
                            }
                        }

                      }

                    case false =>
                      Brain.config match {
                        case Some(config) =>
                          config.isInConfig("region", region.getClass.getName) match {

                            case true =>
                              "ui button" :: button(s"Remove from Config") {
                                onClick {

                                  ///-- Delete
                                  Brain.config.get.removeFromConfig("region", region.getClass.getName)
                                  Brain.config.get.resyncToFile

                                  //-- Stop
                                  //Brain.regions = Brain.regions.filter(_ != r)
                                  //r.kill
                                }
                              }
                            case false =>
                              span("Internal Non Configured: " + region.getClass.getName)

                          }
                        case None =>
                      }
                  }

                }

              }
            }
            // EOF Brain Region Line

            Brain.onResources[BrainRegion] {
              case r =>
                brainRegionLine(r)
                r.onDerivedResources[BrainRegion] {
                  case dr => brainRegionLine(dr)
                }
            }

          }
        }
        // EOF Brain Table
        
        // External Regions Button table
        Brain.getResourcesOfType[ExternalBrainRegion] match {
          case regions if (regions.size == 0) =>
            "ui info message" :: "No Plugins configured"
          case regions =>
            "ui success message" :: "Some Plugins are configured"

            "ui table" :: table {
              thead("Type", "Name", "Location", "Modules")

              regions.foreach {
                region =>
                  tr {
                    td(region.configKey.get.keyType.toString) {

                    }
                    td("") {

                      input {
                        bindBufferValue(region.configKey.get.name)
                      }

                    }
                    td(region.configKey.get.values(0).toString()) {

                    }
                    td("") {

                      region.getDerivedResources[RegionClassName] foreach {
                        regionClass =>
                          
                         // println("** GUI for external region available region: "+regionClass.className)
                         // println("REgion configuration has values:")
                         // region.configKey.get.values.foreach {
                          //  v => 
                          //    println("-> V: "+v)
                          //}
                          
                          div {
                            "ui button" :: button(regionClass.className) {
                              
                              //region.configKey
                              
                              region.configKey.get.values.find { value => value.toString() == regionClass.className } match {
                                case Some(found) => classes("green")
                                case None => classes("red")
                              }
                              onClickReload {
                                region.configKey.get.values.find { value => value.toString() == regionClass.className } match {
                                  case Some(found) => 
                                    region.configKey.get.values -= found
                                    // FIXME: Region class
                                  case None => 
                                    
                                    region.configKey.get.values.add.set(regionClass.className)
                                    region.loadRegionClass(regionClass.className)
                                }
                                Brain.config.get.resyncToFile
                                Harvest.run

                              }
                            }
                          }
                      }
                    }
                  }
              }
              tr {

              }

            }
            // EOF Table

        }

        // Add External Region
        //----------------
        isLocalRequestWithDisplay match {
          case true =>
            "ui button" :: button("Select External Folder Directory") {
              onClickReload {
                println(s"Action called")

                JFXRun.onJavaFX {
                  var stage = new Stage()
                  var dirChooser = new DirectoryChooser();
                  dirChooser.setTitle("Select Compilation Output Folder for a region");

                  dirChooser.showDialog(stage) match {
                    case null =>
                    case regionFolder if (Brain.config.get.isInConfig("external-region-folder", regionFolder.getCanonicalPath) == false) =>

                      var key = Brain.config.get.addKey(regionFolder.getName, "external-region-folder")

                      key.values.add.set(regionFolder.getCanonicalPath)
                      Brain.config.get.resyncToFile
                      

                    case other =>
                  }
                }

              }
            }
          case false =>
        }
        // End of select directory
   

      }

      // All Modules
      //----------------
      /* button("test") {  
        
      }*/
      ribbonHeaderDiv("blue", "Modules") {

        "ui table" :: table {
          thead("Name", "Errors")

          tbody {

            Brain.getResourcesOfType[BrainRegion].foreach {
              region =>
                tr {
                  td(region.getId) {

                  }

                  td("") {
                    region.hasErrors match {
                      case true =>
                        classes("positive")
                        "icon checkmark" :: i()
                      case false =>
                        classes("negative")
                    }
                  }
                }
            }

          }
        }

      }

    }

    // Harvest
    //---------------
    div {

      //-- harvest run
      div {
        "ui icon button" :: button("Run Harvest") {
          "icon settings" :: i {

          }
          //reRender
          onClick {
            Harvest.run
          }
        }

      }

      //-- Harvest Table 
      "ui celled table treetable" :: table {
        id("harvest-table")
        thead {
          tr {
            "sorted descending" :: th("Harvester") {

            }

            th("Resources Count") {

            }
            th("Info") {

            }
            th("Last Run") {

            }
            th("Error") {

            }
          }
        }
        tbody {

          def harvesterLine(hv: Harvester): Unit = {
            //-- Current
            tr {

              // Id
              +@("data-tt-id" -> hv.hierarchyName())

              // Parent
              +@("data-tt-parent-id" -> hv.hierarchyName(withoutSelf = true))

              td("") {

                importHTML(<i class="shipping icon"></i>)
                importHTML(<i class="settings icon ui popup-activate"></i>)
                span(textContent(hv.getDisplayName+"@"+hv.hashCode()))
                
                "ui flowing popup top left transition hidden" :: div {
                  h4("Config") {

                  }
                  //configTable(hv.config)
                }
                

              }

              // Resources count
              td("") {

                hv.getResources.size match {
                  case 0 =>
                    "ui info message" :: div {
                      textContent("Not Resources")
                    }
                  case _ =>
                    "ui button popup-activate" :: button(s"${hv.getResources.size.toString()} Resources Infos") {

                    }
                    "ui flowing popup top left transition hidden" :: div {
                      "ui celled table vui-datatables" :: table {
                        thead("Name","Error State") 
                        tbody {
                          hv.getResources.foreach {
                            ar =>
                              tr {
                                td(ar.getDisplayName) {

                                }
                                //-- Resource Error 
                                td("") {
                                  errorsStat(ar)
                                }
                                /*ar.getLastError match {
                                  case Some(e) =>
                                    "negative" :: td("") {
                                      "icon close" :: i {

                                      }
                                      span(textContent(e.getLocalizedMessage))
                                    }
                                  case None =>
                                    "positive" :: td("") {
                                      "icon checkmark" :: i {

                                      }
                                      span(textContent("None"))
                                    }
                                }*/

                              }
                          }
                        }
                      }
                    }

                }

              }

              td("") {

              }

              td(hv.lastRun.toString) {

              }

              //-- Error 
              hv.getLastError match {
                case Some(e) =>
                  "negative" :: td("") {
                    "icon close" :: i {

                    }
                    span(textContent(e.getLocalizedMessage))
                  }
                case None =>
                  "positive" :: td("") {
                    "icon checkmark" :: i {

                    }
                    span(textContent("None"))
                  }
              }

            }

            //-- Children
            hv.childHarvesters.foreach {
              child: Harvester => harvesterLine(child)
            }
          }
          Harvest.harvesters.foreach {
            r =>
              harvesterLine(r)

          }

        }
      } // EOF harvest table

    }
    // EOF Harvest

  }

}