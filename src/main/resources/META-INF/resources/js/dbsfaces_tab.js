dbs_tab = function(pId) {
    $(pId + " > .-container > .-tabs > .-tab").click(function(e) {
		dbsfaces.tab.changeTab(pId, this);
   	});

//	$(pId + " > .-container > .-tabs > .-tab > .-iconclose").click(function(e) {
//		dbsfaces.tab.changeTab(pId, this);
//		e.stopImmediatePropagation();
//		return false;
//   	});
	
	$(pId + " > .-container > div > .-content > .dbs_tabPage").on("click.tab", function(e){
		e.stopPropagation();
	});

	$(pId + " > .-container > .-tabs > .-tab").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		$(this).children("a").css("opacity",1);
		$(this).children(".loading_container").remove();
	});
}

dbsfaces.tab = {
//	initialize: function(pTab){
//		var xData = {
//			dom : {
//				self: pTab,
//				container: null,
//				tabs: null
//			},
//			showTabPageOnClick: false
//		}
//	},
	
	showTab: function(pId, pTabPageRawId){
		if ($(pId).attr("showTabPageOnClick") == "false"){
			return;
		}

		var xTabPageId = dbsfaces.util.jsid(pTabPageRawId);
		var xTabPage = $(xTabPageId);
		var xTabAba = $(".dbs_tab > .-container > .-tabs > .-tab[tabpage='" + pTabPageRawId + "']");
		
		/* título */
		$(pId + " > .-container > .-tabs > .-tab").removeClass("-selected").addClass("-th_i");
		xTabAba.addClass("-selected ").removeClass("-th_i");
		
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

		$(dbsfaces.util.jsid(xInputId)).val(xTabPageId);
		
		//Exibe Aba
		dbsfaces.tab.showTab(pId, xTabPageId);

		//Submit do valor da página selecionada
//		setTimeout(function(){
//			jsf.ajax.request($(dbsfaces.util.jsid("#" + xInputId)).get(0), null, {execute:xInputId, onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror})
//		}, 0);	

		
		//evento indicando será selecionado
		$(dbsfaces.util.jsid(xTabPageId)).trigger("click.tab");
	},

	clickTabPage: function(pTabPageId){
		$(".dbs_tab > .-container > .-tabs > .-tab[tabpage='" + pTabPageId + "']").click();
	},
	
	closeTab: function(pId, e){
	}
}

	