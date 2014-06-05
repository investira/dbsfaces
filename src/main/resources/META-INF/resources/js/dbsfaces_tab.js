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
		var xTabPage = "#" + $(e).attr("tabpage");
		xTabPage = dbsfaces.util.jsid(xTabPage);

		/* título */
		$(pId + " > .-container > ul > li").not($(e)).removeClass("-selected");
		$(e).addClass("-selected");
		
		/* conteúdo */
		$(pId + " > .-container > div > .-content > .dbs_tabPage").not($(xTabPage)).removeClass("-selected");
		$(xTabPage).addClass("-selected");

		/* salva */
		$(pId + "-input").val($(e).attr("tabpage"));

		/* evento indicando será selecionado*/
		$(xTabPage).trigger("click.tab");
	},

	clickTabPage: function(pTabPageId){
//		var xTabPage = $(".dbs_tab > ul > li[tabpage='" + pTabPageId + "']");
//		var xTab = dbsfaces.util.jsid("#" + xTabPage.closest(".dbs_tab").get(0).id);
//		dbsfaces.tab.showTab(xTab, xTabPage);
		$(".dbs_tab > .-container > ul > li[tabpage='" + pTabPageId + "']").click();
	},
	
	closeTab: function(pId, e){
	}
}

	