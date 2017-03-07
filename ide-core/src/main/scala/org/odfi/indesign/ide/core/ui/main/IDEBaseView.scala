package org.odfi.indesign.ide.core.ui.main

import java.awt.GraphicsEnvironment

import org.odfi.indesign.ide.core.IndesignIDE
import org.odfi.wsb.fwapp.framework.FWAppValueBindingView
import org.odfi.wsb.fwapp.module.datatables.DataTablesView
import org.odfi.wsb.fwapp.module.jquery.JQueryTreetable
import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.odfi.wsb.fwapp.views.ui.SemanticUIImplView
import org.w3c.dom.html.HTMLElement

import com.idyria.osi.vui.html.HTMLNode
import org.odfi.wsb.fwapp.FWappIntermediary
import org.odfi.indesign.ide.core.ui.IndesignFrameworkView
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.indesign.core.config.Config
import org.odfi.indesign.ide.core.ui.IDEGUI
import org.odfi.wsb.fwapp.framework.FWAppTempBufferView
import org.odfi.wsb.fwapp.framework.FWAppValueBufferView
import org.odfi.wsb.fwapp.Site
import org.odfi.indesign.core.brain.Brain
import org.odfi.indesign.ide.core.ui.contrib.IDEGUIMenuProvider
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource
import org.odfi.indesign.core.main.IndesignPlatorm

class IDEBaseView extends SemanticUIImplView with SemanticView with FWAppTempBufferView with FWAppValueBufferView with DataTablesView with JQueryTreetable with IndesignFrameworkView {

  // Common 
  override def getDisplayName = getClass.getName.replace("$", "")

  
  
  
  
  // Request Utils
  //-----------------
  def isLocalRequestWithDisplay = {
    this.request match {
      case Some(req) if (req.isLocalHost) =>
        !GraphicsEnvironment.isHeadless()
      case other => false
    }
  }

  // UI Utils
  //-----------------
  def placePage(cl: => HTMLNode[HTMLElement, _]) = {
    this.definePart("page-body") {
      cl
    }
  }

  this.viewContent {
    html {
      head {
        
        // IDE Libraries
        this.getAssetsResolver match {
          case Some(resolver) if(resolver.findAssetsSource("/indesign-ide").isEmpty) =>
            resolver.addAssetsSource("/indesign-ide", new ResourcesAssetSource).addFilesSource("indesign-ide")
          case other => 
        }
        
        placeLibraries

      }

      body {

        // Main
        //-------------------
        "header" :: div {
          h1("Indesign IDE") {

          }

          //-- Menu
          "ui horizontal menu" :: div {
            "header item" :: "Menu"
            "item" :: a("#") {

              image(createAssetsResolverURI("/indesign-ide/images/tractor-icon-32.png")) {
                waitReloadPage
                onClickReload {
                  Harvest.run
                }
              }
            }
            "item" :: a("#")(text("Dashboard"))

            "item" :: a("@/site/sources")(text("Sources"))
            "item" :: a("@/site/projects")(text("Projects"))

            // Plugins
            //-----------------
            "ui dropdown item" :: div {

              text("Plugins")

              "dropdown icon" :: i()

              IDEGUI.findIntermediaryForPath("/site/plugins/imported") match {
                case Some(pluginsBase) =>
                  "menu" :: div {

                    //-- overview Link
                    "item" :: a("@/site/plugins")(text("Overview"))

                    // Site Modules
                    /* Brain.getResources.foreach {
                      case site : Site if(site!=IDEGUI) => 
                      
                         "item" :: a(site.fullURLPath)(text(site.getDisplayName))
                         
                      case other =>
                        other.getDerivedResources[Site].foreach {
                          site => 
                            "item" :: a(site.fullURLPath)(text(site.getDisplayName))
                        }
                         
                    }*/

                    //-- Plugins
                    /* pluginsBase.intermediaries.foreach {
                      i => 
                        println("** PB: "+i)
                    }*/
                    pluginsBase.intermediaries.collect { case i: FWappIntermediary => i }.foreach {

                      //-- Site
                      case site: Site =>

                        
                        
                        //-- Get Extra Menu
                        site match {
                          case menuProvider : IDEGUIMenuProvider if(menuProvider.getMenuLinks.size>0) => 
                            
                            "ui dropdown item" :: div {
                              text(site.getDisplayName)
                              
                              "dropdown icon" :: i()

                              "menu" :: div {

                                "item" :: a(site.fullURLPath)(text(site.getDisplayName))
                                
                                menuProvider.getMenuLinks.foreach {
                                  case (name,link) =>

                                    "item" :: a(link)(text(name))

                                }

                              }

                            }
                            
                          case other => 
                            
                            "item" :: a("@/"+site.fullURLPath)(text(site.getDisplayName))
                        }
                        
                      //-- Standard In
                      case pathIntermediary =>

                      //println(s"Found IIIII")

                      /*var secondLevel = pathIntermediary.intermediaries.collect { case i: FWappIntermediary => i }
                        secondLevel.size match {
                          case 0 =>
                            // Menu Item
                            "item" :: a("")(text(pathIntermediary.basePath.replace("/", "")))
                          case other =>
                            "ui dropdown item" :: div {
                              a("")(text(pathIntermediary.getDisplayName))
                              "dropdown icon" :: i()

                              "menu" :: div {

                                secondLevel.foreach {
                                  secondLevelIntermediary =>

                                    "item" :: a(secondLevelIntermediary.fullURLPath)(text(secondLevelIntermediary.getDisplayName))

                                }

                              }

                            }

                        }*/

                    }
                  }

                case None =>
              }

            }
            //-- EOF PLugin

            "item" :: a("/site/agent")(text("Agent"))

            // Workspace selection
            "item" :: span {

              label("Config Realm: ") {

                select {
                  Config.listAvailableRealms.foreach {
                    realm =>
                      option(realm) {
                        attributeIf(realm == Config.currentRealm)("selected")
                        text(realm)
                      }
                  }

                  // reload
                  waitReloadPage
                  bindValue {
                    realm: String =>
                      println("Change realm to " + realm)

                      Config.currentRealm = realm
                      Harvest.run

                    //Thread.sleep(3000)
                  }

                }

                semanticIconClickPopup("add circle") {
                  semanticTopRight
                  form {
                    label("New Realm:") {
                      input {
                        bindValue {
                          realm: String =>
                            println("Bound realm value: " + realm)
                        }
                      }
                    }
                    "ui button" :: button("Ok") {
                      onClickReload {
                        //println("Adding realm: ")
                        request.get.getURLParameter("realm") match {
                          case Some(realm) =>
                            println("Adding realm: " + realm)
                            Config.addRealm(realm)

                          case None =>
                        }

                      }
                    }
                  }
                }
                /*"add circle icon green" :: i {
                  semanticPopupOnClick
                }*/

              }

            }
            // EOF workspace selection
            
            //-- Shutdown icon
            "ui item button icon" ::div {
              
              +@("data-tooltip" -> "Add users to your feed")
              
              "power icon" ::  i {
                
                onClick {
                  IndesignPlatorm.stop
                }
              }
              
            }
          }

        }

        // Page
        //-----------------
        div {
          id("page")
          placePart("page-body")
        }

      }
    }

  }

}