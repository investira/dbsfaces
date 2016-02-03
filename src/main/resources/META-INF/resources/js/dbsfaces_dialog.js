dbs_dialog = function(pId) {
    var wDialog = $(pId + " > .-dialog");
    var wCaption = $(pId + " > .-dialog > .-container > table > thead > tr > th");

    //Habilita os input caso não encontre mais dialogs
    //Asterisco necessário, pois o IE não está enviando o evento para o pId
    $(pId + " * ").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
    	if ($(pId).length == 0){
		   var xLastDialog = $("body").find(".dbs_dialog:last");
		   if (xLastDialog.length == 0){
			   var xOP = $(e.currentTarget).offsetParent();
			   dbsfaces.dialog.enableForegroundInputs($("body"));
		   }
		   e.stopImmediatePropagation();
	   }
	});

 	//Desabilita inputs atrás do dialog
    dbsfaces.dialog.disableBackgroundInputs(pId);
    
    //Exibe tela já que foi criada escondida para dar tempo de todos os componentes serem exibidos
	//Fixa tamanho da tela
	wDialog.css("width", wDialog.outerWidth());
	wDialog.css("height", wDialog.outerHeight());
    //Centraliza tela e exibe
    var xH = wDialog.outerHeight() / 2;
    var xW = wDialog.outerWidth() / 2;
    wDialog.css("left","50%")
    		   .css("top","50%")
    		   .css("margin-left","-" + xW + "px")
    		   .css("margin-top","-" + xH + "px")
    		   .css("opacity","1");

    //Seta foco no primeiro input disponível
    dbsfaces.ui.focusOnFirstInput($(pId + dbsfaces.dialog.getBackgroundComponentSelector()));
    
    
	wCaption.off("mousedown.nnndialog")
	   		.on("mousedown.nnndialog", function(e){
        var xDialogPositionOld = {left: wDialog.offset().left,
			 	   			   	  top: wDialog.offset().top,
			 	   			   	  width: wDialog.outerWidth(),
			 	   			   	  height: wDialog.outerHeight()}; 
	   	if (e.which === 1){
	   		wCaption.off("mousemove.nnndialog")
					.on("mousemove.nnndialog",{pX:e.clientX, pY:e.clientY, pPosOld:xDialogPositionOld}, function(e){
//				console.log(wDialog.offset().left + ":" + wDialog.offset().top + "\t" + 
//						    e.data.pPosOld.left + ":" + e.data.pPosOld.top + "\t" +
//						    e.data.pX + ":" + e.data.pY + "\t" +
//						    e.clientX + ":" + e.clientY  + "\t");
				var xLeft;
				var xTop;
		        //Desliga centralização
//		        if (wDialog.css("transform") != "none"){
//			   		wDialog.css("transform", "none");
//		   		}
		        //Seta posição
		   		wDialog.css("margin", "");
		   		wDialog.css("left", e.data.pPosOld.left + (e.clientX - e.data.pX) + "px");
		   		wDialog.css("top", e.data.pPosOld.top + (e.clientY - e.data.pY) + "px");

		        return false;
			});
	   	}
	});
    
	//Desliga move. Por algum bug, dragstart é eventualmente disparado durante o move
	$(window).on("dragstart", function(e){
		wCaption.off("mousemove.nnndialog");
	});
	
	//Desliga move
	$(window).on("mouseup.nnndialog", function(e){
		wCaption.off("mousemove.nnndialog");
	});
    
}


dbsfaces.dialog = {
	disableBackgroundInputs: function(pId){
//	    var xOP = $(pId).offsetParent();
	    var xOP = $("body");
		xOP.find("input").not('[type="hidden"]').not(pId + " input").attr('disabled', true);
		xOP.find("button").not(pId + " button").attr('disabled', true);
		xOP.find("select").not(pId + " select").attr('disabled', true);
		xOP.find("a").not(pId + " a").attr('disabled', true);
		xOP.find("textarea").not(pId + " textarea").attr('disabled', true);

		dbsfaces.dialog.enableForegroundInputs($(pId));
	},
	
	enableForegroundInputs: function(pE){
		pE.find("input").not(".-disabled").attr('disabled', false);
		pE.find("a").not(".-disabled").attr('disabled', false);
		pE.find("button").not(".-disabled").attr('disabled', false);
		pE.find("select").not(".-disabled").attr('disabled', false);
		pE.find("textarea").not(".-disabled").attr('disabled', false);
	},
	
	//Retorna componente que contém o conteúdo principal do dialog.
	//Este método também é chamado externamente
	getBackgroundComponent: function(pId){
		return $(dbsfaces.util.jsid("#" + pId) + dbsfaces.dialog.getBackgroundComponentSelector());
	},

	getBackgroundComponentSelector: function(){
		return " > .-dialog > .-container > table > tbody > tr > td";
	}
}
