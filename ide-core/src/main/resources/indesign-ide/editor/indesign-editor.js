
/*requirejs(["../indesign-ide"], function(indesignIde) {
    //This function is called when scripts/helper/util.js is loaded.
    //If util.js calls define(), then this function is not fired until
    //util's dependencies have loaded, and the util argument will hold
    //the module value for "helper/util".
	
	
	
	
});
*/
indesign.ide.editor = {
		
		createEditors : function() {
			
			indesign.ide.editor.impl.createEditors();
			
		}
		
		
}

$(function() {
	
	console.log("Creating Editors...")
	indesign.ide.editor.createEditors();
	
});

