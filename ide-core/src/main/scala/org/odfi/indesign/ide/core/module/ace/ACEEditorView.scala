package org.odfi.indesign.ide.core.module.ace

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.indesign.ide.core.ui.editor.EditorView

trait ACEEditorView extends EditorView {

  this.addLibrary("ace") {

    case (Some(lib), node) =>

      script(createAssetsResolverURI("ace/src-min/ace.js")) {

      }

    case (_, targetNode) =>

      onNode(targetNode) {
        script(createAssetsResolverURI("indesign-ide/external/node/node_modules/ace-builds/src-min-noconflict/ace.js")) {

        }
        script(createAssetsResolverURI("indesign-ide/js/ace/indesign-ace.js")) {

        }
        stylesheet(createAssetsResolverURI("indesign-ide/js/ace/indesign-ace.css")) {

        }
      }

  }

}