package org.odfi.indesign.ide.core.ui.editor

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.indesign.ide.core.ui.lib.IndesignIDELibView
import org.odfi.indesign.ide.core.sources.SourceFile

trait EditorView extends IndesignIDELibView {

  this.addLibrary("indesign-ide") {

    case (_, targetNode) =>

      onNode(targetNode) {
        script(createAssetsResolverURI("indesign-ide/js/editor/indesign-editor.js")) {

        }
      }

  }
  
  def languageEditor(language:String,source:SourceFile) = {
    
  }

}