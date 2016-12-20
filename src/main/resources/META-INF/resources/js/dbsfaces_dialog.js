dbs_dialog = function(pId) {
	var xDialog = $(pId);

//	dbsfaces.dialog.initialize(xDialog);

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


	$(pId + " > .-container > .-icon").on("mousedown touchstart", function(e){
		if (xDialog.attr("disabled")){return;}
		dbsfaces.dialog.show(xDialog);
		return false;
	});

	$(pId + " > .-container > .-mask").on("mousedown touchstart", function(e){
		if (xDialog.attr("disabled")){return;}
		//Fecha se possuior botão de fechar padrão
		if (xDialog.data("bthandle").length != 0){
			dbsfaces.dialog.show(xDialog);
		}
		return false;
	});
	
	/*Exibe dialog já aberto*/
	if (xDialog.attr("o")) {
		dbsfaces.dialog.show(xDialog);
	}
	
	/*Fecha dialog que originou o action*/
	if(xDialog.children().length == 0){
		var xList = $("body").data("dbs_dialogs");
		if (!(typeof xList === "undefined")){
			$(dbsfaces.util.jsid(xList.pop())).trigger("close");
		}
	};

};

dbs_dialogContent = function(pId) {
	var xDialog = $(pId);

	dbsfaces.dialog.initialize(xDialog);

	/*Captura movimento touch para verificar se é para fechar o dialog*/
	if (!xDialog.data("c")) {
		$(pId + " > .-container > .-content").touchwipe({
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
	$(pId + " > .-container > .-content").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		//Foi fechado
//		if ($(this).closest(".dbs_dialog").hasClass("-closed")){
		if (xDialog.hasClass("-closed")){
			xDialog.trigger("closed");
			$(this).parent().removeClass("-opened").addClass("-closed");
			//Envia confirmação da mensagem se houver somente o botão yes
			if (xDialog.attr("type") == "msg"
		   	 && xDialog.data("btyes") != null){
				xDialog.data("btyes").click();
			}
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

//	$(pId + " > .-container > .-content > .-footer > .-toolbar > .-btok").on("mousedown touchstart", function(e){
//		if (xDialog.attr("disabled")){return;}
//		dbsfaces.dialog.show(xDialog);
//	});

	
	$(pId + " > .-container > .-content > .-bthandle").on("mousedown touchstart", function(e){
		if (xDialog.attr("disabled")){return;}
		/*fecha normalmente se não houver timeout ou for modal*/
		if (xDialog.data("timeout") == "0"){
			dbsfaces.dialog.show(xDialog);
		}else{
			/*Aguarda finalização do touch e mouse por 200ms para verificar se é um cancelamento do timeout*/
			xDialog.data("time", new Date().getTime());
		}
	});
	
	/*Fecha o dialog*/
	$(pId + " > .-container > .-content > .-bthandle").on("mouseup touchend", function(e){
		if (xDialog.attr("disabled")){return;}
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


	/*Fecha dialog após retorno das chamadas ajax de botões com função de fechar */
	$(pId + " .-th_action.-closeDialog").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		var xButton = $(this);
		//Já foi fechado
		if (xDialog.hasClass("-closed")){
			return;
		}
		//Não fecha o dialog se houver mensagem a ser exibida e salva id deste dialog para posteriormente fecha-lo ao final das mensagens e quando for Yes.
 		if (xButton.data("hasmessage")){ 
			var xList = $("body").data("dbs_dialogs");
			if (typeof xList === "undefined"){
				xList = [];
			}
			var xI = 0;
			var xFound = false;
			while (xList[xI]) {
			    if (xList[xI] == xDialog[0].id){
			    	xFound = true;
			    	break;
			    }
			    i++;
			}
			if (!xFound){
				xList.push(xDialog[0].id);
			}
			$("body").data("dbs_dialogs", xList);
			return;
		} 
	
//		if (xButton.hasClass("-closeParent")){
//			$(dbsfaces.util.jsid(xDialog.data("content").attr("asid"))).closest(".dbs_dialog").trigger("close");
//		}
		dbsfaces.dialog.show(xDialog);
		e.stopImmediatePropagation();
		return false;
	});
	
	/*Animação do timeout*/
	$(pId + " > .-container > .-content > .-bthandle").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		dbsfaces.dialog.show(xDialog);
		return false;
	});
	
//	if (xDialog.data("closeCanceled")){
//		xDialog.data("closeCanceled", false);
//		dbsfaces.dialog.show(xDialog);
//	}

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
		pDialog.data("header_icon", pDialog.data("header_content").children(".-icon"));
		pDialog.data("footer", pDialog.data("content").children(".-footer"));
		pDialog.data("footer_content", pDialog.data("footer").children(".-content"));
		pDialog.data("footer_toolbar", pDialog.data("footer").children(".-toolbar"));
		pDialog.data("bthandle", pDialog.data("content").children(".-bthandle"));
		pDialog.data("padding", parseFloat(pDialog.data("sub_content").css("padding")));
		pDialog.data("timeout", dbsfaces.util.getNotEmpty(pDialog.attr("timeout"),"0"));
		pDialog.data("parent", pDialog.parent().closest(".dbs_dialog"));
//		pDialog.attr("previousOpen", null);
		var xBtYes = null;
		//Verifrica se há somente o botão de ok quando for mensagem.
		if (pDialog.attr("type") == "msg"
		 && pDialog.data("footer_toolbar").length == 1){
			xBtYes = dbsfaces.util.getNotEmpty(pDialog.data("footer_toolbar").children("[id$='btyes']"),null);
			if (xBtYes !=null
			 && xBtYes.css("display") != "none"){
				 xBtYes = null;			
			}
		}
		pDialog.data("btyes", xBtYes);
	},
	
	pvInitializeLayout: function(pDialog){
		dbsfaces.dialog.pvAjustLayout(pDialog);

		pDialog.data("container").css("opacity", "");
		//Configura cor como transparencia a partir da cor definida pelo usuário
		var xColorClose;
		//Cor do header
		var xIsDark = tinycolor(pDialog.data("content").css("background-color")).isDark();
		if (xIsDark){
			if (pDialog.data("header_icon").length > 0){
				pDialog.data("header_icon").removeClass("-dark");
			}
			pDialog.data("header_content").addClass("-light")
										  .removeClass("-dark");
		}else{
			if (pDialog.data("header_icon").length > 0){
				pDialog.data("header_icon").addClass("-dark");
			}
			pDialog.data("header_content").addClass("-dark")
										  .removeClass("-light");
		}
		if (pDialog.data("header_content").length > 0){
			//Ajusta tamanho do icone do header
			var xHeight = pDialog.data("header_content")[0].getBoundingClientRect().height / parseFloat(pDialog.data("header_content").css("font-size"));
			pDialog.data("header_icon").children().css("font-size", xHeight + "em");
		}
		
		//Cor da barra de timeout
		if (pDialog.attr("type") != "mod"){
			if (xIsDark){
				xColorClose = "rgba(255,255,255,.1)";
			}else{
				xColorClose = "rgba(0,0,0,.1)";
			}
			pDialog.data("bthandle").css("border-color", xColorClose)
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
//		var xTime = parseFloat(pDialog.data("timeout"));
		var xTime = parseFloat(5);
		dbsfaces.ui.cssTransition(pDialog.data("bthandle"), "width " + xTime + "s linear, height " + xTime + "s linear");
	},

	
	stopTimeout: function(pDialog){
		pDialog.data("bthandle").addClass("-stopped");
	},
	
	startTimeout: function(pDialog){
		pDialog.data("bthandle").removeClass("-stopped");
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
		if (pDialog.data("bthandle").length == 0){
			return false;
		}

		if (pDialog.attr("type") == "nav" //For nav
		 ||	(pDialog.attr("type") == "msg" && pDialog.data("btyes") != null)){ //ou Msg on só há o botão ok
			if ((pDialog.attr("p") == "t"
			  && pDirection == "u")
			 || (pDialog.attr("p") == "b"
			  && pDirection == "d")
			 || (pDialog.attr("p") == "l"
			  && pDirection == "l")
			 || (pDialog.attr("p") == "r"
			  && pDirection == "r")
			 || (pDialog.attr("p") == "c")){
				dbsfaces.dialog.show(pDialog);
				return true;
			}
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
		pDialog.removeClass("-closed");
		if (!pDialog.attr("disabled")){
//			if (pDialog.data("parent").not(".-closed").length > 0){
				dbsfaces.ui.disableBackgroundInputs(pDialog);
				dbsfaces.dialog.pvFreeze(pDialog, true);
				//Coloca o foco no primeiro campo de input dentro do dialog
				dbsfaces.ui.focusOnFirstInput(pDialog);
//			}
		}
	},
	
	pvClose: function(pDialog){
		pDialog.addClass("-closed");
		//Retira foco do componente que possuir foco
		$(":focus").blur();
		dbsfaces.dialog.startTimeout(pDialog);
		//Destrava tudo por não existe dialog pai aberto
		if (pDialog.data("parent").length == 0){
			dbsfaces.ui.enableForegroundInputs($("body"));
			dbsfaces.dialog.pvFreeze(pDialog, false);
			pDialog.attr('disabled', null);
		//Destrava somente dialog pai
		}else{
			pDialog.data("parent").attr('disabled', null);
			dbsfaces.ui.enableForegroundInputs(pDialog.data("parent"));
		}
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
		//Força content-size para 's'
		if (dbsfaces.util.isMobile()
		&& (pDialog.attr("type") == "mod" 
		 || pDialog.attr("type") == "nav"
		 || (pDialog.attr("type") == "msg" && pDialog.attr("p") != "c"))){
			pDialog.attr("cs","s");
		}
		if (pDialog.data("parent").length > 0
		 && pDialog.data("parent").attr("cs") != "s"){
			pDialog.attr("p","c");
		}
		//Configura o padding
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

};

