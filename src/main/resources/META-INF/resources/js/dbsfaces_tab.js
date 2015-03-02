dbs_tab = function(pId) {
	$(pId + " > .-container > ul > li").click(function(e) {
		dbsfaces.tab.showTab(pId, this);
   	});

	$(pId + " > .-container > ul > li > .-iconclose").click(function() {
		dbsfaces.tab.showTab(pId, this);
   	});
	
	$(pId + " > .-container > div > .-content > .dbs_tabPage").on("click.tab", function(e){
		e.stopPropagation();
	});
}

dbsfaces.tab = {
	showTab: function(pId, e){
		if ($(pId).attr("showTabPageOnClick") == "false"){
			return;
		}
		var xTabPageId = "#" + $(e).attr("tabpage");
		xTabPageId = dbsfaces.util.jsid(xTabPageId);
		dbsfaces.ui.ajaxShowLoading(xTabPageId);
		
		/* título */
		$(pId + " > .-container > ul > li").not($(e)).removeClass("-selected");
		$(e).addClass("-selected");
		
		/* conteúdo */
		$(pId + " > .-container > div > .-content > .dbs_tabPage").not($(xTabPageId)).removeClass("-selected");
		$(xTabPageId).addClass("-selected");

		/* salva */
		$(pId + "-input").val($(e).attr("tabpage"));
		
//		var xParentId = $(pId).closest(".dbs_tab").get(0).id; 
//		var xInputId = $(pId + "-input").get(0).id;
//		jsf.ajax.request($(pId + "-input").get(0), "click", {execute:xInputId, onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror})
		
//		/* evento indicando será selecionado*/
		$(xTabPageId).trigger("click.tab");
	},
	

	clickTabPage: function(pTabPageId){
		$(".dbs_tab > .-container > ul > li[tabpage='" + pTabPageId + "']").click();
	},
	
	closeTab: function(pId, e){
	}
}

	