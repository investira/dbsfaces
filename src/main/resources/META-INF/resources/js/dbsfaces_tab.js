dbs_tab = function(pId) {
	
	$(pId + " > .-container > ul > li").click(function(e) {
		dbsfaces.tab.changeTab(pId, this);
   	});

//	$(pId + " > .-container > ul > li > .-iconclose").click(function(e) {
//		dbsfaces.tab.changeTab(pId, this);
//		e.stopImmediatePropagation();
//		return false;
//   	});
	
	$(pId + " > .-container > div > .-content > .dbs_tabPage").on("click.tab", function(e){
		e.stopPropagation();
	});

	$(pId + " > .-container > ul > li").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		$(this).children("a").css("opacity",1);
		$(this).children(".loading_container").remove();
	});

}

dbsfaces.tab = {
	showTab: function(pId, pTabPageRawId){
		if ($(pId).attr("showTabPageOnClick") == "false"){
			return;
		}

		var xTabPageId = dbsfaces.util.jsid("#" + pTabPageRawId);
		var xTabPage = $(xTabPageId);
		var xTabAba = $(".dbs_tab > .-container > ul > li[tabpage='" + pTabPageRawId + "']");
		
		/* título */
		$(pId + " > .-container > ul > li").removeClass("-selected");
		xTabAba.addClass("-selected");
		
		/* conteúdo */
		$(pId + " > .-container > div > .-content > .dbs_tabPage").removeClass("-selected");
		xTabPage.addClass("-selected");
	},
	
	changeTab: function(pId, pTabAba){
		if ($(pId).attr("showTabPageOnClick") == "false"){
			return;
		}
		//Ainda carregando
		if ($(pTabAba).children(".loading_container").length > 0){
			return;
		}

		var xTabPageId = $(pTabAba).attr("tabpage");
		
		//Salva id da aba selecionada
		var xInputId = $(pId).closest(".dbs_tab").get(0).id + ":input";

		$(dbsfaces.util.jsid("#" + xInputId)).val(xTabPageId);
		
		//Exibe Aba
		dbsfaces.tab.showTab(pId, xTabPageId);

		//Submit do valor da página selecionada
//		setTimeout(function(){
//			jsf.ajax.request($(dbsfaces.util.jsid("#" + xInputId)).get(0), null, {execute:xInputId, onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror})
//		}, 0);	

		
		//evento indicando será selecionado
		$(xTabPageId).trigger("click.tab");
	},

	clickTabPage: function(pTabPageId){
		$(".dbs_tab > .-container > ul > li[tabpage='" + pTabPageId + "']").click();
	},
	
	closeTab: function(pId, e){
	}
}

	