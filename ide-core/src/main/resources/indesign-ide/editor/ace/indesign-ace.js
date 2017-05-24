/*requirejs(["../editor/indesign-editor"], function(indesignEditor) {
    //This function is called when scripts/helper/util.js is loaded.
    //If util.js calls define(), then this function is not fired until
    //util's dependencies have loaded, and the util argument will hold
    //the module value for "helper/util".
	
	
	
	
});*/

indesign.ide.editor.impl = {
		
		
	createEditors : function() {
		
		$(".ace-editor").each(function(i,editorElement) {
			
			console.log("Found Editor Element");
			var editor = ace.edit(editorElement);
			//var editor = ace.edit("ace-editor");
			editor.setTheme("ace/theme/solarized_light");
		    editor.getSession().setMode("ace/mode/"+$(editorElement).data("language"));
		    /*editor.resize();*/
		    $(editorElement).height(editor.session.getLength()*15);
		    $(editorElement).data("editor",editor);
			
		});
		
	}
		
}




$(function() {
	
	
			
	
	
	
});