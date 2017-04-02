package org.odfi.indesign.ide.core.ui.editor.ace

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
        script(createAssetsResolverURI("indesign-ide/editor/ace/indesign-ace.js")) {

        }
        stylesheet(createAssetsResolverURI("indesign-ide/editor/ace/indesign-ace.css")) {

        }
      }

  }

}