package org.odfi.indesign.ide.core.ui.editor

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.indesign.ide.core.ui.lib.IndesignIDELibView

import org.odfi.indesign.ide.core.sources.SourceCodeProvider
import org.odfi.indesign.ide.core.sources.outline.SourceOutlineProvider
import org.odfi.wsb.fwapp.module.semantic.SemanticView

trait EditorView extends IndesignIDELibView with SemanticView {

  this.addLibrary("indesign-ide") {

    case (_, targetNode) =>

      onNode(targetNode) {
        script(createAssetsResolverURI("indesign-ide/editor/indesign-editor.js")) {

        }
        
         script(createAssetsResolverURI("indesign-ide/editor/outline/indesign-outline.js")) {

        }

        stylesheet(createAssetsResolverURI("indesign-ide/editor/indesign-editor.css")) {

        }
      }

  }

  def editorAndOutline(source: SourceOutlineProvider)(cl: => Any) = {

    //-- Take code from

    "ui two column divided grid ide code-and-outline powergrid" :: div {

      "streched row" :: div {

        //-- Editor Column
        "column" :: div {
          "ui segment ide code" :: div {
            "ui button " :: button("Save") {

              withData("text", """$(".ide.editor.area").data("editor").getValue()""")
              onClickReload {

                //codeUpdated(request.get.getURLParameter("text").get)

                println("Button Clicked: " + request.get.urlParameters)
                
                source.setTextContent(request.get.getURLParameter("text").get)
                

              }
            }

            "ide editor results" :: div {

            }

            "ide editor area ace-editor" :: pre("") {
              data("language", "verilog")
              code(source.getTextContent) {

              }
            }

          }
        }

        //-- Outline Column
        "column" :: div {

          "ui sticky segment ide outline" :: div {
            
            source.toOutline match {
              case Some(outline) => 
                data("outline" -> outline.toJSONString)
              case None => 
                
            }
            
            source.outlineClass match {
              case Some(cl) =>
                classes(cl)
              case other => 
            }
            
            /*
            //-- Hierarchy
            //-- Create Hierarchy from
            var verilog = new VerilogSourceString(vcode)
            verilog.toHierarchy match {
              case Some(hierarchy) =>
                
                var mname = getTempBufferValueDefault[String]("module-name", "Test")
                "#hierarchy h2dl-hdl-diagram" :: div {
                  data("hier" -> hierarchy.toJSONString)
                }
              case None =>
                "ui warning message" :: "Hierarchy could not be build"
            }*/

          }

        }
      }
      //-- EOf Row
      
      //-- Customisations
      cl

    }
  }

  /*def languageEditor(language:String,source:SourceFile) = {
    
  }*/

}