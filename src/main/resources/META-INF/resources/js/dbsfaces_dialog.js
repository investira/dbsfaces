dbs_dialog = function(pId) {
	var xDialog = $(pId);

	dbsfaces.dialog.initialize(xDialog);

	$(pId).on("close", function(){
		dbsfaces.dialog.pvClose(xDialog);
	});
	$(pId).on("open", function(){
		dbsfaces.dialog.pvOpen(xDialog);
	});
	$(pId).on("stopTimeout", function(){
		dbsfaces.dialog.stopTimeout(xDialog);
	});
	$(pId).on("startTimeout", function(){
		dbsfaces.dialog.startTimeout(xDialog);
	});

	$(window).resize(function(e){
		setTimeout(function(){
			dbsfaces.dialog.resized(xDialog);
		},0);
	});


	$(pId + ":not([disabled]) > .-container > .-icon").on("mousedown touchstart", function(e){
		dbsfaces.dialog.show(xDialog);
		return false;
	});

	$(pId + ":not([disabled]) > .-container > .-mask").on("mousedown touchstart", function(e){
		//Fecha se pussior botão de fechar padrão
		if (xDialog.data("bttimeout").length != 0){
			dbsfaces.dialog.show(xDialog);
		}
		return false;
	});
	
	/*Captura movimento touch para verificar se é para fechar o dialog*/
	if (!xDialog.data("c")) {
		$(pId + ":not([disabled]) > .-container > .-content").touchwipe({
		     wipeLeft: function() {return dbsfaces.dialog.wipe(xDialog, "l");},
		     wipeRight: function() {return dbsfaces.dialog.wipe(xDialog, "r");},
		     wipeUp: function() {return dbsfaces.dialog.wipe(xDialog, "u");},
		     wipeDown: function() {return dbsfaces.dialog.wipe(xDialog, "d");},
		     onStart: function() {dbsfaces.dialog.scrollStart(xDialog);},
		     onMove: function(dx, dy) {dbsfaces.dialog.scroll(xDialog, dx, dy);},
		     min_move_x: 25,
		     min_move_y: 25,
		     preventDefaultEvents: true
		});
	}


	/*Após animação de abrir ou fechar*/
	$(pId + ":not([disabled]) > .-container > .-content").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		//Foi fechado
		if ($(this).closest(".dbs_dialog").hasClass("-closed")){
			xDialog.trigger("closed");
			$(this).parent().removeClass("-opened").addClass("-closed");
		//Foi aberto
		}else{
			$(this).parent().removeClass("-closed").addClass("-opened");
			xDialog.trigger("opened");
		}
	});
	
	/*Message contralizada, fecha com com qualquer ação*/ 
//	$(pId + "[type='msg'][p='c']:not([disabled]) > .-container > .-content").on("mousedown touchstart", function(e){
//		dbsfaces.dialog.show(xDialog);
//	});

	
	$(pId + ":not([disabled]) > .-container > .-content > .-bttimeout").on("mousedown touchstart", function(e){
		/*fecha normalmente se não houver timeout ou for modal*/
		if (xDialog.attr("type") == "mod" 
		 || xDialog.data("timeout") == "0"){
			dbsfaces.dialog.show(xDialog);
		}else{
			/*Aguarda finalização do touch o mouse para verificar se é um cancelamento do timeout*/
			xDialog.data("time", new Date().getTime());
		}
	});
	
	/*Fecha o dialog*/
	$(pId + ":not([disabled]) > .-container > .-content > .-bttimeout").on("mouseup touchend", function(e){
		if (xDialog.data("timeout") == "0"){return;}
		var xTime = new Date().getTime();
		//Fecha normalmente
		if (xTime - xDialog.data("time") < 200){
			dbsfaces.dialog.show(xDialog);
		//Interrompe o fechamento
		}else{
			xDialog.trigger("stopTimeout");
		}
		return false;
	});

	/*dispara evento informando que botão back for pressionado*/
	$(pId + ":not([disabled]) > .-container > .-content > .-btback").on("mousedown touchstart", function(e){
		xDialog.trigger("back");
	});

	/*Animação do timeout*/
	$(pId + ":not([disabled]) > .-container > .-content > .-bttimeout").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		dbsfaces.dialog.show(xDialog);
		return false;
	});
	
	/*Exibe dialog já aberto*/
	if (xDialog.attr("open")) {
		dbsfaces.dialog.show(xDialog);
	}
};

dbsfaces.dialog = {

	initialize: function(pDialog){
		dbsfaces.dialog.pvInitializeData(pDialog);
		dbsfaces.dialog.pvInitializeLayout(pDialog);
		dbsfaces.dialog.pvInitializeCloseTimeout(pDialog);
	},

	pvInitializeData: function(pDialog){
		pDialog.data("container", pDialog.children(".-container"));;
		pDialog.data("content", pDialog.data("container").children(".-content"));
		pDialog.data("icon", pDialog.data("container").children(".-icon"));
		pDialog.data("mask", pDialog.data("container").children(".-mask"));
		pDialog.data("sub_container", pDialog.data("content").children(".-sub_container"));
		pDialog.data("divscroll", pDialog.data("sub_container").children("div"));
		pDialog.data("sub_content", pDialog.data("divscroll").find("> div > .-sub_content"));
		pDialog.data("header", pDialog.data("content").children(".-header"));
		pDialog.data("header_content", pDialog.data("header").children(".-content"));
		pDialog.data("footer", pDialog.data("content").children(".-footer"));
		pDialog.data("footer_content", pDialog.data("footer").children(".-content"));
		pDialog.data("bttimeout", pDialog.data("content").children(".-bttimeout"));
		pDialog.data("padding", parseFloat(pDialog.data("sub_content").css("padding")));
		pDialog.data("timeout", dbsfaces.util.getNotEmpty(pDialog.attr("timeout"),"0"));
	},
	
	pvInitializeLayout: function(pDialog){
		dbsfaces.dialog.pvAjustLayout(pDialog);

		pDialog.data("container").css("opacity", "");
		//Configura cor como transparencia a partir da cor definida pelo usuário
		if (pDialog.attr("type") == "mod"){
		}else{
			if (tinycolor(pDialog.data("content").css("background-color")).isDark()){
				xColorClose = "rgba(255,255,255,.1)";
			}else{
				xColorClose = "rgba(0,0,0,.1)";
			}
			pDialog.data("bttimeout").css("border-color", xColorClose)
							  	     .css("background-color", xColorClose);
		}
		//Largura mínima em função da largura do header
		var xMinWidth = pDialog.data("padding") * 2;
		var xEle;
		xEle = pDialog.data("header").find("> .-content > .-caption > .-icon");
		if (xEle.length != 0){
			xMinWidth += xEle[0].clientWidth;
		}
		xEle = pDialog.data("header").find("> .-content > .-caption > .-label");
		if (xEle.length != 0){
			xMinWidth += xEle[0].clientWidth;
		}
		pDialog.data("sub_content").css("min-width", xMinWidth);
	},
	
	pvInitializeCloseTimeout: function(pDialog){
		if (pDialog.data("timeout") == "0"){return;}
		if (pDialog.attr("timeout") == "a"){
			pDialog.data("timeout", dbsfaces.ui.getTimeFromTextLength(pDialog.data("sub_content").text()) / 1000);
		}
		var xTime = parseInt(pDialog.data("timeout"));
		dbsfaces.ui.cssTransition(pDialog.data("bttimeout"), "width " + xTime + "s linear, height " + xTime + "s linear");
	},

//	cancelCloseTimeout: function(pDialog){
//		dbsfaces.ui.cssTransition(pDialog.data("bttimeout"), "none");
//	},
	
	stopTimeout: function(pDialog){
		pDialog.data("bttimeout").addClass("-stopped");
	},
	
	startTimeout: function(pDialog){
		pDialog.data("bttimeout").removeClass("-stopped");
	},

	/*Força o scroll já que ele não funciona naturalente no mobile*/
	scroll: function(pDialog, pDx, pDy){
		var xDiv = pDialog.data("divscroll");
		xDiv.scrollLeft(xDiv.data("scrollx") + pDx);
		xDiv.scrollTop(xDiv.data("scrolly") + pDy);
	},
	
	/*Força o scroll já que ele não funciona naturalente no mobile*/
	//Salva posição atual do scroll
	scrollStart: function(pDialog){
		var xDiv = pDialog.data("divscroll");
		xDiv.data("scrollx", xDiv.scrollLeft());
		xDiv.data("scrolly", xDiv.scrollTop());
	},

	wipe: function(pDialog, pDirection){
		if (pDialog.data("bttimeout").length == 0){
			return false;
		}
		if ((pDialog.attr("p") == "t"
		  && pDirection == "u")
		 || (pDialog.attr("p") == "b"
		  && pDirection == "d")
		 || (pDialog.attr("p") == "l"
		  && pDirection == "l")
		 || (pDialog.attr("p") == "r"
		  && pDirection == "r")
		 || (pDialog.attr("p") == "c"
		  && pDialog.attr("type") == "msg")){
			dbsfaces.dialog.show(pDialog);
			return true;
		}
		return false;
	},

	resized: function(pDialog){
		if (!pDialog.hasClass("-closed")){
			dbsfaces.dialog.pvAjustLayout(pDialog);
		}
	},

	show: function(pDialog){
		//Está fechado e vai abrir
		if (pDialog.hasClass("-closed")){
			dbsfaces.dialog.pvOpen(pDialog);
		}else{
			dbsfaces.dialog.pvClose(pDialog);
		}
	},

	pvOpen: function(pDialog){
		dbsfaces.dialog.pvAjustLayout(pDialog);
		dbsfaces.ui.disableBackgroundInputs(pDialog);
		dbsfaces.dialog.pvFreeze(pDialog, true);
		pDialog.removeClass("-closed");
		//Coloca o foco no primeiro campo de input dentro do nav
		dbsfaces.ui.focusOnFirstInput(pDialog);
	},
	
	pvClose: function(pDialog){
		var xP = pDialog.attr("p");

		dbsfaces.ui.enableForegroundInputs($("body"));
		//Retira foco do componente que possuir foco
		$(":focus").blur();
		pDialog.addClass("-closed");
		dbsfaces.dialog.startTimeout(pDialog);
		dbsfaces.dialog.pvFreeze(pDialog, false);
	},
	
	pvFreeze: function(pDialog, pOn){
		if (pOn){
			$("html").addClass("dbs_dialog-freeze");
			//Previnir scroll em mobile se não for um filho deste dialog
			$(".dbs_dialog-freeze").on("touchstart touchmove", function(e){
				if ($.contains(pDialog[0], e.originalEvent.srcElement.classList)){
					return false;
				}
			});
		}else{
			$("html").removeClass("dbs_dialog-freeze");
			//reabilita scroll em mobile
			$(".dbs_dialog-freeze").off("touchstart touchmove");
		}
	},
	
//	pvAjustLayout: function(pDialog){
//		if (dbsfaces.util.isMobile()){
//			pDialog.attr("cs","s");
//		}
//		var xHeader = pDialog.data("header");
//		var xFooter = pDialog.data("footer");
//		var xSubContainer = pDialog.data("sub_container");
//
//				
//		if (xHeader.length > 0){
//			var xHeaderHeight = xHeader[0].clientHeight;
//			xSubContainer.css("padding-top", xHeaderHeight);
//		}
//		if (xFooter.length > 0){
//			var xFooterHeight = xFooter[0].clientHeight;
//			xSubContainer.css("padding-bottom", xFooterHeight);
//		}
//
//	}
	pvAjustLayout: function(pDialog){
		if (dbsfaces.util.isMobile()){
			pDialog.attr("cs","s");
		}
		var xHeaderContent = pDialog.data("header_content");
		var xFooter = pDialog.data("footer");
		var xSubContainer = pDialog.data("sub_container");
		
		if (xHeaderContent.length > 0){
			var xHeaderHeight = xHeaderContent[0].clientHeight;
			xSubContainer.css("padding-top", xHeaderHeight);
		}
		if (xFooter.length > 0){
			var xFooterHeight = xFooter[0].clientHeight;
			xSubContainer.css("padding-bottom", xFooterHeight);
		}

	}

//	pvAjustLayout: function(pDialog){
//		if (dbsfaces.util.isMobile()){
//			pDialog.attr("cs","s");
//		}
//		var xHeaderContent = pDialog.data("header_content");
//		var xFooterContent = pDialog.data("footer_content");
//		var xSubContainer = pDialog.data("sub_container");
//		
//		if (xHeaderContent.length > 0){
//			var xHeaderHeight = xHeaderContent[0].clientHeight;
//			xSubContainer.css("padding-top", xHeaderHeight);
//		}
//		if (xFooterContent.length > 0){
//			var xFooterHeight = xFooterContent[0].clientHeight;
//			xSubContainer.css("padding-bottom", xFooterHeight);
//		}
//
//	}
};

