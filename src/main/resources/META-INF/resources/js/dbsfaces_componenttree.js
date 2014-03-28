dbs_componenttree = function(pId) {
	$(pId + " > .-container li > .-label").click(function(e){
//		e.stopImmediatePropagation();
		var xSelectedKey = $(this).attr("key");
		dbsfaces.componenttree.select(this, xSelectedKey);
//		return false;
	});
		
	$(pId + " > .-container > .-caption li > .-label .-closable").click(function(e){
		e.stopImmediatePropagation();
		var xLabel = $(this).closest(".-label");
		dbsfaces.componenttree.showNode(xLabel);
		return false;
	});	
	
	$(pId + " > .-container > .-extrainfo").focus(function(e){
//		console.log("FOCUS");
	});

	$(pId + " > .-container li > .-label").mouseenter(function(e){
		var xParentId = dbsfaces.util.jsid("#" + $(this).closest(".dbs_componenttree").get(0).id);
		var xSelectedKey =dbsfaces.util.jsid("#" + $(this).get(0).id);
		xSelectedKey = xSelectedKey.replace("-extrainfo","");
		$(xParentId + " > .-container li.-hover").removeClass("-hover");
		$(xSelectedKey + "-extrainfo").parent().addClass("-hover");
		$(xSelectedKey).parent().addClass("-hover");
	});	
	

	
}

dbsfaces.componenttree = {
	showNode: function(pLabel){
		var xExtraInfoId = dbsfaces.util.jsid("#" + $(pLabel).attr("id") + "-extrainfo");
		var xExtraInfoContent = $(xExtraInfoId).siblings(".-content").children("div");
		var xCaptionContent = $(pLabel).siblings(".-content").children("div");
		dbsfaces.componenttree.setExpandedIds(pLabel);
		$(pLabel).children("div").children(".-closable").toggleClass("dbs_iconsmall -is_menuitem_plus");
		$(pLabel).children("div").children(".-closable").toggleClass("dbs_iconsmall -is_menuitem_minus");
		
		$(xExtraInfoContent)
		 	.animate({
			 height: 'toggle'
			 }, 200, function() {
		});

		$(xCaptionContent)
		 	.animate({
			 height: 'toggle'
			 }, 200, function() {
			if ($(xCaptionContent).outerHeight()<2){
				$(xCaptionContent).css("overflow","visible");
			}	 
		});
	},

	select: function(pLabel, pSelectedKey){
		var xParentId = $(pLabel).closest(".dbs_componenttree").get(0).id; 
		var xInputId = dbsfaces.util.jsid("#" + xParentId + ":selection-input");
		var xButtonId = dbsfaces.util.jsid("#" + xParentId + ":selection-submit");
		$(xInputId).val(pSelectedKey);
		$(xButtonId).click();
	},
	
	setExpandedIds: function(pLabel){
		var xSelectedKey = " " + $(pLabel).attr("key") + " ";
		var xParentId = $(pLabel).closest(".dbs_componenttree").get(0).id; 
		var xInputId = dbsfaces.util.jsid("#" + xParentId + ":expandedIds");
		var xExpandedIds = " " + $(xInputId).val() + " ";
		if (xExpandedIds.indexOf(xSelectedKey) == -1){
			xExpandedIds = xExpandedIds + " " + $.trim(xSelectedKey); 
		}else{
			xExpandedIds = xExpandedIds.replaceAll(xSelectedKey,"");
		}
		xExpandedIds = " " + $.trim(xExpandedIds) + " ";
		$(xInputId).val(xExpandedIds);
	}
	
}