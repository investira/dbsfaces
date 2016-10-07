dbs_dialog = function(pId) {
	var xDialog = $(pId);

	$(window).resize(function(e){
		setTimeout(function(){
			dbsfaces.dialog.resized(xDialog);
		},0);
	});

	dbsfaces.dialog.initialize(xDialog);

	$(pId).on("close", function(){
		dbsfaces.dialog.pvClose(xDialog);
	});
	$(pId).on("open", function(){
		dbsfaces.dialog.pvOpen(xDialog);
	});
	
	
	$(pId + ":not([disabled]) > .-container > .-icon").on("mousedown touchstart", function(e){
//		console.log("icon mousedown touchstart");
		dbsfaces.dialog.show(xDialog);
		return false;
	});

	$(pId + ":not([disabled]) > .-container > .-mask").on("mousedown touchstart", function(e){
//		console.log("mask mousedown touchstart");
		dbsfaces.dialog.show(xDialog);
		return false;
	});
	
	/*Captura movimento touch para verificar se é para fechar o dialog*/
	if (!xDialog.data("c")) {
		$(pId + ":not([disabled]) > .-container > .-content").touchwipe({
		     wipeLeft: function() {return dbsfaces.dialog.whipe(xDialog, "l");},
		     wipeRight: function() {return dbsfaces.dialog.whipe(xDialog, "r");},
		     wipeUp: function() {return dbsfaces.dialog.whipe(xDialog, "u");},
		     wipeDown: function() {return dbsfaces.dialog.whipe(xDialog, "d");},
		     onStart: function() {dbsfaces.dialog.scrollStart(xDialog);},
		     onMove: function(dx, dy) {dbsfaces.dialog.scroll(xDialog, dx, dy);},
		     min_move_x: 25,
		     min_move_y: 25,
		     preventDefaultEvents: true
		});
	}


	/*Após animação de abrir ou fechar*/
	$(pId + ":not([disabled]) > .-container > .-content").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
//		console.log("end transition\t" + $(this).css("max-height"));
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
	
	$(pId + ":not([disabled]) > .-container > .-content > .-iconclose").on("mousedown touchstart", function(e){
		/*fecha normalmente se não houver timeout*/
		if (xDialog.data("timeout") == "0"){
			dbsfaces.dialog.show(xDialog);
		}else{
			/*Aguarda finalização do touch o mouse para verificar se é um cancelamento do timeout*/
			xDialog.data("time", new Date().getTime());
		}
	});
	
	/*Fecha o dialog*/
	$(pId + ":not([disabled]) > .-container > .-content > .-iconclose").on("mouseup touchend", function(e){
		if (xDialog.data("timeout") == "0"){return;}
		var xTime = new Date().getTime();
		//Fecha normalmente
		if (xTime - xDialog.data("time") < 200){
			dbsfaces.dialog.show(xDialog);
		//Interrompe o fechamento
		}else{
			dbsfaces.dialog.cancelCloseTimeout(xDialog);
		}
		return false;
	});

	/*Animação do timeout*/
	$(pId + ":not([disabled]) > .-container > .-content > .-iconclose").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		dbsfaces.dialog.show(xDialog);
	});
	
	/*Exibe dialog já aberto*/
	var xOpen = xDialog.attr('open');
	if (xOpen == "true") {
		dbsfaces.dialog.show(xDialog);
		return false;
	}
};

dbsfaces.dialog = {

	initialize: function(pDialog){
		dbsfaces.dialog.pvInitializeData(pDialog);
		dbsfaces.dialog.pvInitializeLayout(pDialog);
		dbsfaces.dialog.pvInitializeTimeout(pDialog);
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
		pDialog.data("iconclose", pDialog.data("content").children(".-iconclose"));
		pDialog.data("padding", parseFloat(pDialog.data("mask").css("padding-left")));
		pDialog.data("timeout", dbsfaces.util.getNotEmpty(pDialog.attr("timeout"),"0"));
	},
	
	pvInitializeLayout: function(pDialog){
		pDialog.data("container").css("opacity", "");
		//Configura cor como transparencia a partir da cor definida pelo usuário
		if (tinycolor(pDialog.data("content").css("background-color")).isDark()){
			xColorClose = "rgba(255,255,255,.1)";
		}else{
			xColorClose = "rgba(0,0,0,.1)";
		}
		pDialog.data("iconclose").css("border-color", xColorClose)
								 .css("background-color", xColorClose);
		dbsfaces.dialog.pvAjustLayout(pDialog);

	},
	
	pvInitializeTimeout: function(pDialog){
		if (pDialog.data("timeout") == "0"){return;}
		if (pDialog.attr("timeout") == "auto"){
			pDialog.data("timeout", dbsfaces.ui.getTimeFromTextLength(pDialog.data("sub_content").text()) / 1000);
		}
		var xTime = parseInt(pDialog.data("timeout"));
		console.log("ddd\t" + xTime);
		dbsfaces.ui.cssTransition(pDialog.data("iconclose"), "width " + xTime + "s linear, height " + xTime + "s linear");
	},

	cancelCloseTimeout: function(pDialog){
		dbsfaces.ui.cssTransition(pDialog.data("iconclose"), "none");
	},

//	pvInitializeTimeout: function(pDialog){
//		if (pDialog.attr("timeout") == "0" 
//		 || pDialog.data("timeout").length == 0){return;}
//		var xTime;
//		if (pDialog.attr("timeout") == "auto"){
//			console.log(pDialog.data("sub_content").length);
//		}else{
//			xTime = parseInt(pDialog.attr("timeout"));
//		}
//		dbsfaces.ui.cssTransition(pDialog.data("timeoutO"), "stroke-dashoffset " + xTime + "s linear");
//	},
	
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

	whipe: function(pDialog, pDirection){
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
		pDialog.data("sub_content").removeClass("-closed");
		pDialog.removeClass("-closed");
		//Coloca o foco no primeiro campo de input dentro do nav
		dbsfaces.ui.focusOnFirstInput(pDialog);
	},
	
	pvClose: function(pDialog){
		var xP = pDialog.attr("p");

		dbsfaces.ui.enableForegroundInputs($("body"));
		//Retira foco do componente que possuir foco
		$(":focus").blur();
		pDialog.data("sub_content").addClass("-closed");
		pDialog.addClass("-closed");
		dbsfaces.dialog.pvInitializeTimeout(pDialog);
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
	
	pvAjustLayout: function(pDialog){
		var xHeaderContent = pDialog.data("header_content");
		var xFooterContent = pDialog.data("footer_content");
		var xSubContainer = pDialog.data("sub_container");
		
		if (xHeaderContent.length > 0){
			var xHeaderHeight = xHeaderContent[0].clientHeight;
			xSubContainer.css("padding-top", xHeaderHeight);
		}
		if (xFooterContent.length > 0){
			var xFooterHeight = xFooterContent[0].clientHeight;
			xSubContainer.css("padding-bottom", xFooterHeight);
		}

	}

};

