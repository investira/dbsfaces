dbs_componenttree = function(pId) {
	var wTimeout;
	var xClicked;
	$(pId + " > .-container li > .-label").click(function(e){
		var xSelectedKey = $(this).attr("key");
		dbsfaces.componenttree.select(e, this, xSelectedKey);
	});

	
	$(pId + " > .-container > .-caption li > .-label .-closable").click(function(e){
		//dbsclick Implemenado desta forma posid o click também é disparado no dbsclick
		//Se for duplo click exibe todos os filhos
		if (xClicked
		&& $(this).hasClass("-is_menuitem_plus")){
			clearTimeout(wTimeout);
			var xX = $(this).closest(".-label").parent();
			xX.find('.-label').each(function(){
				dbsfaces.componenttree.showNode($(this));
			});
			xClicked = false;
			e.stopImmediatePropagation();
			return false;
		//Se for click exibe somente o nó clicado
		}else{
			//click
			xClicked = true;
			var xThis = this;
			wTimeout = setTimeout(function(){
				xClicked = false;
				var xLabel = $(xThis).closest(".-label");
				dbsfaces.componenttree.showNode(xLabel);
				e.stopImmediatePropagation();
				return false;
			},300);
		}
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
			 }, 100, function() {
		});

		$(xCaptionContent)
		 	.animate({
			 height: 'toggle'
			 }, 100, function() {
			if ($(xCaptionContent).outerHeight()<2){
				$(xCaptionContent).css("overflow","visible");
			}	 
		});
	},

	select: function(e, pLabel, pSelectedKey){
		var xParentId = $(pLabel).closest(".dbs_componenttree").get(0).id; 
		var xInputId = dbsfaces.util.jsid("#" + xParentId + ":selection-input");
		var xButtonId = dbsfaces.util.jsid("#" + xParentId + ":selection-submit");
		$(xInputId).val(pSelectedKey);
		//Faz requuisição para dar submit das seleções e atualizar a linha
		jsf.ajax.request(e, 'update', {execute:xParentId, onevent:dbsfaces.onajax});
	},
	
	setExpandedIds: function(pLabel){
		var xSelectedKey = " " + $(pLabel).attr("key") + " ";
		var xParent = $(pLabel).closest(".dbs_componenttree").get(0);
		if (typeof(xParent) != 'undefined'){
			var xParentId = xParent.id; 
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
	
}