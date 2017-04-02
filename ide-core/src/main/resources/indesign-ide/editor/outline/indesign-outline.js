$(function() {
	
	/**
	 * Outline are created as tabs
	 */
	$(".ide.outline").each(function(i,outlineDiv) {
		
		console.log("(Outline) Found Outline Div");
		var outline = jQuery.parseJSON("{"+$(outlineDiv).data("outline")+"}").Outline;
		console.log("(Outline) Outline: "+$(outlineDiv).data("outline"));
		
		// Make Headers and content
		//---------------
		var headerDiv = $('<div class="ui top attached tabular menu"></div>')
		$(outlineDiv).append(headerDiv);
		$(outline.View).each(function(i,view) {
			
			console.log("(Outline) Found View: "+view.ViewName);
			
			// Create Header
			//---------------
			var classes = "item";
			if (i==0) {
				classes = "item active";
			}
				
			headerDiv.append('<a class="'+classes+'" data-tab="'+view.ViewName+'">'+view.ViewName+'</a>');
			
			
			// Make Content
			//--------------------
			var contentDiv = $('<div class="ui bottom attached active tab segment" data-tab="'+view.ViewName+'"></div>');
			contentDiv.addClass(view.ViewClass);
			contentDiv.data("view",view)
			$(outlineDiv).append(contentDiv);
			
		});
	
		// Activate tabs
		$(headerDiv).find(".item").tab();
		
	});
	
	var outlineTextMakeOutlineSection = function(baseElement,section) {
		
		// Add LI and add UL for content
		var sectionLi = $("<li>"+section.Name+"</li>");
		$(baseElement).append(sectionLi)
		var sectionul = $("<ul></ul>");
		sectionLi.append(sectionul);
		$(section.OutlineElement).each(function (i,outlineElement) {
			
			console.log("Adding Outline Section element: ");
			var elementLi = $("<li>"+outlineElement.Name+"</li>");
			sectionul.append(elementLi);
			
			// add tooltip
			var lineHint = outlineElement.Hint[0];
			var lineTooltip = "Line: "+lineHint.Value;
			$(elementLi).attr("data-tooltip",lineTooltip);
			
		});
	
		
		// For each element, add 
		
	};
	
	/**
	 * Outline Text
	 */
	$(".outline-text").each(function(i,outlineText) {
		
		console.log("(Outline) Found outline text....");
		var view = $(outlineText).data("view");
		
	
		var ul = $("<ul></ul>")
		$(outlineText).append(ul);
		 
		$(view.OutlineSection).each(function(i,outlineSection) {
			
			
			outlineTextMakeOutlineSection(ul,outlineSection);
			
		});
		
		//$(outlineText).append($("</ul>"));
		
		
	});
	
});
