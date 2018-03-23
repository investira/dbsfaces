dbs_tab = function(pId) {
	var xTabData = dbsfaces.tab.initialize($(pId));
	
	xTabData.dom.caption.on("mousedown touchstart", function(e){
		//Ainda carregando
		var xThis = $(this);
		if (xThis.children(".-ajax").length > 0){
			return;
		}

		//Selecionar item
		dbsfaces.tab.selectTabPage(xThis.attr("tabpageid"), xTabData);
		e.stopImmediatePropagation();
		return false;
	});
	
	xTabData.dom.container.on("change", function(e){
		//Inibir change recebido por componentes filhos
		return false;
	});
	
	xTabData.dom.caption.on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		var xContainer = $(this).children(".dbs_tabCaption");
		//Remove indicador do loading
		xContainer.removeClass("-ajax");
		//Atualiza elemento com novo conteúdo do tabpager
		xTabData.dom.tabPage = xTabData.dom.tabPages.find("> .-container > .dbs_tabPage");
	});

	xTabData.dom.captions.on(dbsfaces.EVENT.ON_TRANSITION_END, function(e) {
		if (!$(e.target).is($(this))){return;}
		if (xTabData.type == "acc"){
			var xTabPageRawId = xTabData.dom.input.val() + "_aba";
			var xTabPage = $(dbsfaces.util.jsid(xTabPageRawId));
			//Habilitar novo chamado após finalizada a transição
			xTabData.dom.captions.css("pointer-events", "");
			//Posiciona como primeiro item da lista
			xTabPage.parent().prepend(xTabPage);
		}
	});

	if (xTabData.type == "scr"){
		xTabData.dom.tabPages.touchwipe({
			wipeLeft : function() {
				var xTabPage = $(dbsfaces.util.jsid(xTabData.dom.input.val())).next();
				if (xTabPage.length > 0){
					dbsfaces.tab.selectTabPage(xTabPage[0].id, xTabData);
				}
//				return dbsfaces.dialog.wipe(xDialogData, "l");
			},
			wipeRight : function() {
				var xTabPage = $(dbsfaces.util.jsid(xTabData.dom.input.val())).prev();
				if (xTabPage.length > 0){
					dbsfaces.tab.selectTabPage(xTabPage[0].id, xTabData);
				}
//				return dbsfaces.dialog.wipe(xDialogData, "r");
			},
			wipeUp : function() {
//				return dbsfaces.dialog.wipe(xDialogData, "u");
			},
			wipeDown : function() {
//				return dbsfaces.dialog.wipe(xDialogData, "d");
			},
			onStart : function() {
//				dbsfaces.dialog.scrollStart(xDialogData);
			},
			onMove : function(dx, dy) {
//				dbsfaces.dialog.scroll(xDialogData, dx, dy);
			},
			min_move_x : 25,
			min_move_y : 25,
			preventDefaultEvents : true
		});
	}
	
	$(window).resize(function(e){
		dbsfaces.tab.resize($(pId).data("data"));
	});
	
	
}

dbsfaces.tab = {

	initialize: function(pTab){
		var xTabData = dbsfaces.tab.initializeData(pTab);
		dbsfaces.tab.initializeLayout(xTabData);
		if (xTabData.type == "tab" 
		 || xTabData.type == "scr"){
			if (xTabData.dom.tabPage.length > 0){
				if (xTabData.dom.input.val() == ""){
					dbsfaces.tab.showTabPage(xTabData.dom.tabPage[0].id, xTabData);
				}else{
					dbsfaces.tab.showTabPage(xTabData.dom.input.val(), xTabData);
				}
			}
		}
		xTabData.dom.container.removeClass("-hide");
		return xTabData;
	},
	
	initializeData: function(pTab){
		var xData = {
			dom : {
				self: pTab,
				container: null,
				captions: null,
				captions_container: null,
				caption: null,
				tabPages: null,
				tabPage: null,
				input: null
			},
			type: pTab.attr("type"),
			showTabPageOnClick: pTab.attr("soc"),
			color: pTab.css("color"),
			colorInverted: tinycolor(pTab.css("color").toString()).invertLightness().setAlpha(1).toString(),
			resizeTimeout: null
		}
		xData.dom.container = pTab.children(".-container");
		xData.dom.captions = xData.dom.container.children(".-captions");
		xData.dom.captions_container = xData.dom.captions.children(".-container");
		xData.dom.caption = xData.dom.captions_container.children(".-caption");
		xData.dom.tabPages = xData.dom.container.children(".-tabPages");
		xData.dom.tabPage = xData.dom.tabPages.find("> .-container > .dbs_tabPage");
		xData.dom.input = xData.dom.tabPages.find("> .-container > input");
		pTab.data("data", xData);
		return xData;
	},

	initializeLayout: function(pTabData){
		//Remove caption se não houver caption em nenhum tabpage
		if (pTabData.dom.captions_container.children().length == 0){
			pTabData.dom.captions.remove();
		}
	},
	
	showTabPage: function(pTabPageRawId, pTabData){
		var xTabPage = $(dbsfaces.util.jsid(pTabPageRawId));
		if (typeof pTabData == "undefined"){
			pTabData = xTabPage.closest(".dbs_tab").data("data");
		}
		//Impedir novo chamado enquanto não estiver concluida a transição
		if (pTabData.type == "acc"){
//			pTabData.dom.captions.css("pointer-events", "none");
		}
		var xDoUnSelect = pTabData.type == "acc" && xTabPage.hasClass("-selected");
		//Remove seleção anterior
		pTabData.dom.caption.removeClass("-selected");
		pTabData.dom.caption.children().removeClass("-selected");
		pTabData.dom.tabPage.removeClass("-selected");
		//Nova seleção
		var xCaption = pTabData.dom.caption.filter("[tabpageid='" + pTabPageRawId + "']");
		xCaption.siblings().css("min-height", "");
		if (xDoUnSelect){
			pTabData.dom.container.removeClass("-selected");
			xCaption.removeClass("-hide").removeClass("-selected");
			xCaption.children().removeClass("-hide").removeClass("-selected");
			xCaption.siblings().removeClass("-hide").removeClass("-selected");
			xCaption.siblings().children().removeClass("-hide").removeClass("-selected");

			//Salva página selecionada
			pTabData.dom.input.val(null);
		}else{
			pTabData.dom.container.addClass("-selected");
			xCaption.removeClass("-hide").addClass("-selected");
			xCaption.children().removeClass("-hide").addClass("-selected");
			
			xTabPage.removeClass("-hide").removeClass("-next").removeClass("-prev").addClass("-selected");
			xTabPage.prevAll().removeClass("-next").addClass("-prev");
			xTabPage.nextAll().removeClass("-prev").addClass("-next");
			xCaption.siblings().addClass("-hide");
			xCaption.siblings().children().addClass("-hide");
			xTabPage.siblings().addClass("-hide");
			
			pTabData.dom.captions.css("min-height", "");
			xCaption.css("min-height", "");
//			xCaption.siblings().css("min-height", "");
			pTabData.dom.input.val(pTabPageRawId);
//			setTimeout(function(e){
//				var xRect = dbsfaces.ui.getRect(xCaption.children());
//				var xFontSize = dbsfaces.number.parseFloat(xCaption.css("font-size"));
//				var xHeight = xRect.height / xFontSize;
//				xHeight += "em";
//				xCaption.css("min-height", xHeight);
//				pTabData.dom.captions.css("min-height", xHeight);
//			},300);
		}
		//Troca cor
//		xCaption.css("background-color", pTabData.color)
//				.css("color", pTabData.colorInverted);
		dbsfaces.tab.resize(pTabData);
		pTabData.dom.self.trigger("change", pTabPageRawId.substring(pTabPageRawId.lastIndexOf(":") + 1));
		xTabPage.trigger("select", !xDoUnSelect);
	},

	selectTabPage: function(pTabPageRawId, pTabData){
		if (!pTabData.showTabPageOnClick){
			return;
		}
		//Exibe página selecionada
		dbsfaces.tab.showTabPage(pTabPageRawId);
	},

	resize: function(pTabData){
	}

}

	