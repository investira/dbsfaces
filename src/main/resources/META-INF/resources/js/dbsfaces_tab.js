dbs_tab = function(pId) {
	var xTabData = dbsfaces.tab.initialize($(pId));
	
	xTabData.dom.caption.on("mousedown touchstart", function(e){
		dbsfaces.tab.selectTabPage($(this).attr("tabpageid"), xTabData);
	});

	xTabData.dom.caption.on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		$(this).children("a").css("opacity",1);
		$(this).children(".loading_container").remove();
	});
	
	$(window).resize(function(e){
		dbsfaces.tab.resize(xTabData);
	});
}

dbsfaces.tab = {

	initialize: function(pTab){
		var xTabData = dbsfaces.tab.initializeData(pTab);
		if (xTabData.dom.tabPage.length > 0){
			if (xTabData.dom.input.val() == ""){
				dbsfaces.tab.showTabPage(xTabData.dom.tabPage[0].id, xTabData);
			}else{
				dbsfaces.tab.showTabPage(xTabData.dom.input.val(), xTabData);
			}
		}
		return xTabData;
	},
	
	initializeData: function(pTab){
		var xData = {
			dom : {
				self: pTab,
				container: null,
				captions: null,
				caption: null,
				tabPages: null,
				tabPage: null,
				input: null
			},
			showTabPageOnClick: pTab.attr("soc"),
			color: pTab.css("color"),
			colorInverted: tinycolor(pTab.css("color").toString()).invertLightness().setAlpha(1).toString(),
			resizeTimeout: null
		}
		xData.dom.container = pTab.children(".-container");
		xData.dom.captions = xData.dom.container.children(".-captions");
		xData.dom.caption = xData.dom.captions.find("> .-container > .-caption");
		xData.dom.tabPages = xData.dom.container.children(".-tabPages");
		xData.dom.tabPage = xData.dom.tabPages.find("> .-content > .dbs_tabPage");
		xData.dom.input = xData.dom.tabPages.find("> .-content > input");
		pTab.data("data", xData);
		return xData;
	},

	showTabPage: function(pTabPageRawId, pTabData){
		var xTabPage = $(dbsfaces.util.jsid(pTabPageRawId));
		if (typeof pTabData == "undefined"){
			pTabData = xTabPage.closest(".dbs_tab").data("data");
		}
		//Remove seleção anterior
		pTabData.dom.caption.removeClass("-selected");
		pTabData.dom.tabPage.removeClass("-selected");
		//Nova seleção
		var xCaption = pTabData.dom.caption.filter("[tabpageid='" + pTabPageRawId + "']");
		xCaption.addClass("-selected");
		xTabPage.addClass("-selected");
		//Troca cor
//		xCaption.css("background-color", pTabData.color)
//				.css("color", pTabData.colorInverted);
		dbsfaces.tab.resize(pTabData);
	},

	selectTabPage: function(pTabPageRawId, pTabData){
		if (!pTabData.showTabPageOnClick){
			return;
		}
		//Salva página selecionada
		pTabData.dom.input.val(pTabPageRawId);
		//Exibe página selecionada
		dbsfaces.tab.showTabPage(pTabPageRawId);
	},

	resize: function(pTabData){
		//Timeout para dar tempo de saber a dimensão do componente pai
//		clearTimeout(pTabData.resizeTimeout);
//		pTabData.resizeTimeout = setTimeout(function(e){
			//Move para frente
//			dbsfaces.ui.moveToFront(pTabData);
//		},0);
	}
	

	
//resize: function(pTabData){
	//Timeout para dar tempo de saber a dimensão do componente pai
//	clearTimeout(pTabData.resizeTimeout);
//	pTabData.resizeTimeout = setTimeout(function(e){
		//Move para frente
//		dbsfaces.ui.moveToFront(pTabData);
//	},1);
//}
}

	