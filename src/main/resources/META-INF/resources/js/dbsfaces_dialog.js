dbs_dialog = function(pId) {
	var xDialog = $(pId);

//	dbsfaces.dialog.initialize(xDialog);

	$(pId).on("close", function(){
		var xDialogData = xDialog.data("data");
		dbsfaces.dialog.pvClose(xDialogData);
	});
	$(pId).on("open", function(){
		var xDialogData = xDialog.data("data");
		dbsfaces.dialog.pvOpen(xDialogData);
	});
	$(pId).on("stopTimeout", function(){
		var xDialogData = xDialog.data("data");
		dbsfaces.dialog.stopTimeout(xDialogData);
	});
	$(pId).on("startTimeout", function(){
		var xDialogData = xDialog.data("data");
		dbsfaces.dialog.startTimeout(xDialogData);
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
		var xDialogData = xDialog.data("data");
		//Fecha se possuior botão de fechar padrão
		if (xDialogData.dom.bthandle.length != 0){
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
			var xDialogData = xDialog.data("data");
			if (xDialogData.type == "msg"
		   	 && xDialogData.dom.btyes != null
		   	 && xDialogData.dom.btyes.length > 0){
				xDialogData.dom.btyes.click();
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
		/*fecha normalmente se não houver closeTimeout ou for modal*/
		var xDialogData = xDialog.data("data");
		if (xDialogData.closeTimeout == "0"){
			dbsfaces.dialog.show(xDialog);
		}else{
			/*Aguarda finalização do touch e mouse por 200ms para verificar se é um cancelamento do timeout*/
			xDialogData.time = new Date().getTime();
		}
	});
	
	/*Fecha o dialog*/
	$(pId + " > .-container > .-content > .-bthandle").on("mouseup touchend", function(e){
		if (xDialog.attr("disabled")){return;}
		var xDialogData = xDialog.data("data");
		if (xDialogData.closeTimeout == "0"){return;}
		var xTime = new Date().getTime();
		//Fecha normalmente
		if (xTime - xDialogData.time < 200){
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
	
	/*Ao final da animação do closeTimeout*/
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
		var xDialogData = dbsfaces.dialog.pvInitializeData(pDialog);
		dbsfaces.dialog.pvInitializeLayout(xDialogData);
		dbsfaces.dialog.pvInitializeCloseTimeout(xDialogData);
	},
		
	pvInitializeData: function(pDialog){
		var xData = {
			dom: {
				self: pDialog,
				parent: pDialog.parent().closest(".dbs_dialog"),
				container: pDialog.children(".-container"),
				content: null,
				icon: null,
				mask: null,
				sub_container: null,
				divscroll: null,
				sub_content: null,
				header: null,
				header_content: null,
				header_icon: null,
				header_caption: null,
				header_caption_label: null,
				footer: null,
				footer_content: null,
				footer_toolbar: null,
				bthandle: null,
				btyes: null
			},
			type: pDialog.attr("type"),
			closeTimeout: dbsfaces.util.getNotEmpty(pDialog.attr("ct"), "0"),
			time: null,
			padding: null
		}
		xData.dom.content = xData.dom.container.children(".-content");
		xData.dom.icon = xData.dom.container.children(".-icon");
		xData.dom.mask = xData.dom.container.children(".-mask");
		xData.dom.sub_container = xData.dom.content.children(".-sub_container");
		xData.dom.divscroll = xData.dom.sub_container.children("div");
		xData.dom.sub_content = xData.dom.divscroll.find("> div > .-sub_content");
		xData.dom.header = xData.dom.content.children(".-header");
		xData.dom.header_content = xData.dom.header.children(".-content");
		xData.dom.header_icon = xData.dom.header_content.children(".-icon");
		xData.dom.header_caption = xData.dom.header_content.children(".-caption");
		xData.dom.header_caption_label = xData.dom.header_caption.children(".-label");
		xData.dom.footer = xData.dom.content.children(".-footer");
		xData.dom.footer_content = xData.dom.footer.children(".-content");
		xData.dom.footer_toolbar = xData.dom.footer.children(".-toolbar");
		xData.dom.bthandle = xData.dom.content.children(".-bthandle");
		
		xData.padding = parseFloat(xData.dom.sub_content.css("padding"));

		pDialog.data("data", xData);

		var xBtYes = null;
		//Verifrica se há somente o botão de ok quando for mensagem.
		if (xData.type == "msg"
		 && xData.dom.footer_toolbar.length == 1){
			xBtYes = dbsfaces.util.getNotEmpty(xData.dom.footer_toolbar.children("[id$='btyes']"),null);
			if (xBtYes !=null
			 && xBtYes.css("display") != "none"){
				 xBtYes = null;			
			}
		}
		xData.dom.btyes = xBtYes;
		return xData;
	},
	
	pvInitializeLayout: function(pDialogData){
		dbsfaces.dialog.pvAjustLayout(pDialogData);

		pDialogData.dom.container.css("opacity", "");
		//Configura cor como transparencia a partir da cor definida pelo usuário
		var xColorClose;
		//Cor do header
		var xIsDark = tinycolor(pDialogData.dom.content.css("background-color")).isDark();
		if (xIsDark){
			if (pDialogData.dom.header_icon.length > 0){
				pDialogData.dom.header_icon.removeClass("-dark");
			}
			pDialogData.dom.header_content.addClass("-light")
										  .removeClass("-dark");
		}else{
			if (pDialogData.dom.header_icon.length > 0){
				pDialogData.dom.header_icon.addClass("-dark");
			}
			pDialogData.dom.header_content.addClass("-dark")
										  .removeClass("-light");
		}
		if (pDialogData.dom.header_content.length > 0){
			//Ajusta tamanho do icone do header
			var xHeight = pDialogData.dom.header_content[0].getBoundingClientRect().height / parseFloat(pDialogData.dom.header_content.css("font-size"));
			pDialogData.dom.header_icon.children().css("font-size", xHeight + "em");
		}
		
		//Cor da barra de timeout
		if (pDialogData.type != "mod"){
			if (xIsDark){
				xColorClose = "rgba(255,255,255,.1)";
			}else{
				xColorClose = "rgba(0,0,0,.1)";
			}
			pDialogData.dom.bthandle.css("border-color", xColorClose)
							  	    .css("background-color", xColorClose);
		}
		//Largura mínima em função da largura do header
		var xMinWidth = pDialogData.padding * 2;
		var xEle;
		xEle = pDialogData.dom.header_caption.children(".-icon");
		if (xEle.length != 0){
			xMinWidth += xEle[0].clientWidth;
		}
		xEle = pDialogData.dom.header_caption.children(".-label");
		if (xEle.length != 0){
			xMinWidth += xEle[0].clientWidth;
		}
		pDialogData.dom.sub_content.css("min-width", xMinWidth);
	},
	
	pvInitializeCloseTimeout: function(pDialogData){
		if (pDialogData.closeTimeout == "0"){return;}
		var xTime = 5;
		if (pDialogData.closeTimeout == "a"){
			xTime = dbsfaces.ui.getTimeFromTextLength(pDialogData.dom.sub_content.text()) / 1000;
		}
		dbsfaces.ui.cssTransition(pDialogData.dom.bthandle, "width " + xTime + "s linear, height " + xTime + "s linear");
	},

	
	stopTimeout: function(pDialogData){
		pDialogData.dom.bthandle.addClass("-stopped");
	},
	
	startTimeout: function(pDialogData){
		pDialogData.dom.bthandle.removeClass("-stopped");
	},

	/*Força o scroll já que ele não funciona naturalente no mobile*/
	scroll: function(pDialogData, pDx, pDy){
		var xDiv = pDialogData.dom.divscroll;
		xDiv.scrollLeft(xDiv.data("scrollx") + pDx);
		xDiv.scrollTop(xDiv.data("scrolly") + pDy);
	},
	
	/*Força o scroll já que ele não funciona naturalente no mobile*/
	//Salva posição atual do scroll
	scrollStart: function(pDialog){
		var xDialogData = pDialog.data("data");
		var xDiv = xDialogData.dom.divscroll;
		xDiv.data("scrollx", xDiv.scrollLeft());
		xDiv.data("scrolly", xDiv.scrollTop());
	},

	wipe: function(pDialog, pDirection){
		var xDialogData = pDialog.data("data");
		if (xDialogData.dom.bthandle.length == 0){
			return false;
		}

		if (xpDialogData.dom.type == "nav" //For nav
		 ||	(xDialogData.dom.type == "msg" && xDialogData.dom.btyes != null)){ //ou Msg on só há o botão ok
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
			dbsfaces.dialog.pvAjustLayout(pDialog.data("data"));
		}
	},

	show: function(pDialog){
		if ((typeof pDialog == "undefined") || pDialog.length == 0){return;}
		var xDialogData = pDialog.data("data");
		//Está fechado e vai abrir
		if (pDialog.hasClass("-closed")){
			dbsfaces.dialog.pvOpen(xDialogData);
		}else{
			dbsfaces.dialog.pvClose(xDialogData);
		}
	},
	
	setMsg: function(pDialog, pMsgType, pMsgText){
		var xDialogData = pDialog.data("data");
		var xIcon = xDialogData.dom.header_icon.children("div");
		var xCaption = xDialogData.dom.header_caption_label;
		var xMsgTypeData = dbsfaces.dialog.pvMsgTypeGetData(pMsgType);
		xDialogData.dom.sub_content.text(pMsgText);
		dbsfaces.dialog.pvInitializeCloseTimeout(xDialogData);
		
		xIcon.attr("class", "");
		xIcon.addClass(xMsgTypeData.iconClass);
		xCaption.text(xMsgTypeData.caption);
	},
	
	pvMsgTypeGetData: function(pMsgType){
		if (pMsgType == null || (typeof pMsgType == "undefined")){return null;}
		pMsgType = pMsgType.trim().toLowerCase();
		var xData = {
			caption: null,
			iconClass: null,
			question: null
		}
		if (pMsgType == "a"){
			xData.caption = "Sobre";
			xData.iconClass = "-i_information";
			xData.question = false;
 		}else if (pMsgType == "s"){
			xData.caption = "Sucesso";
			xData.iconClass = "-i_sucess";
			xData.question = false;
 		}else if (pMsgType == "i"){
			xData.caption = "Informação";
			xData.iconClass = "-i_information";
			xData.question = false;
 		}else if (pMsgType == "t"){
			xData.caption = "Importante";
			xData.iconClass = "-i_important";
			xData.question = true;
 		}else if (pMsgType == "w"){
			xData.caption = "Atenção";
			xData.iconClass = "-i_warning";
			xData.question = false;
 		}else if (pMsgType == "c"){
			xData.caption = "Confirmar";
			xData.iconClass = "-i_question_confirm";
			xData.question = true;
 		}else if (pMsgType == "g"){
			xData.caption = "Ignorar";
			xData.iconClass = "-i_question_ignore";
			xData.question = true;
 		}else if (pMsgType == "p"){
			xData.caption = "Proibido";
			xData.iconClass = "-i_forbidden";
			xData.question = false;
 		}else if (pMsgType == "e"){
			xData.caption = "Erro";
			xData.iconClass = "-i_error";
			xData.question = false;
 		}
		return xData;
	},
	

	pvOpen: function(pDialogData){
		dbsfaces.dialog.pvAjustLayout(pDialogData);
		pDialogData.dom.self.removeClass("-closed");
		if (!pDialogData.dom.self.attr("disabled")){
//			if (pDialog.data("parent").not(".-closed").length > 0){
				dbsfaces.ui.disableBackgroundInputs(pDialogData.dom.self);
				dbsfaces.dialog.pvFreeze(pDialogData, true);
				//Coloca o foco no primeiro campo de input dentro do dialog
				dbsfaces.ui.focusOnFirstInput(pDialogData.dom.self);
//			}
		}
	},
	
	pvClose: function(pDialogData){
		pDialogData.dom.self.addClass("-closed");
		//Retira foco do componente que possuir foco
		$(":focus").blur();
		dbsfaces.dialog.startTimeout(pDialogData);
		//Destrava tudo por não existe dialog pai aberto
		if (pDialogData.dom.parent.length == 0){
			dbsfaces.ui.enableForegroundInputs($("body"));
			dbsfaces.dialog.pvFreeze(pDialogData.dom.self, false);
			pDialogData.dom.self.attr('disabled', null);
		//Destrava somente dialog pai
		}else{
			pDialogData.dom.parent.attr('disabled', null);
			dbsfaces.ui.enableForegroundInputs(pDialogData.dom.parent);
		}
	},

	pvFreeze: function(pDialogData, pOn){
		if (pOn){
			$("html").addClass("dbs_dialog-freeze");
			//Previnir scroll em mobile se não for um filho deste dialog
			$(".dbs_dialog-freeze").on("touchstart touchmove", function(e){
				if ($.contains(pDialogData.dom.self[0], e.originalEvent.srcElement.classList)){
					return false;
				}
			});
		}else{
			$("html").removeClass("dbs_dialog-freeze");
			//reabilita scroll em mobile
			$(".dbs_dialog-freeze").off("touchstart touchmove");
		}
	},
	
	pvAjustLayout: function(pDialogData){
		//Força content-size para 's'
		if (dbsfaces.util.isMobile()
		&& (pDialogData.type == "mod" 
		 || pDialogData.type == "nav"
		 || (pDialogData.type == "msg" && pDialogData.dom.self.attr("p") != "c"))){
			pDialogData.dom.self.attr("cs","s");
		}
		if (pDialogData.dom.parent.length > 0
		 && pDialogData.dom.parent.attr("cs") != "s"){
			pDialogData.dom.self.attr("p","c");
		}
		//Configura o padding
		if (pDialogData.dom.header_content.length > 0){
			pDialogData.dom.sub_container.css("padding-top", pDialogData.dom.header_content[0].clientHeight);
		}
		if (pDialogData.dom.footer.length > 0){
			pDialogData.dom.sub_container.css("padding-bottom", pDialogData.dom.footer[0].clientHeight);
		}

	}

};

