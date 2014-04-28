dbs_dialog = function(pId) {
    var xH = 0;
    var xW = 0;
    xH = $(pId + "> div > .-caption").outerHeight();
    xH = xH + $(pId + "> div > .-toolbar").outerHeight();
    $(pId + " > div ").css("padding-bottom", xH + "px");
    
    //Centraliza texto de confirmação dentro do dialog(quando existir)
    var xI = pId + " .dbs_dialog-confirmation > .-content > .-icon > div";
	var xC = pId + " .dbs_dialog-confirmation > .-content > .-content > div";
    dbsfaces.ui.centerVertical(xI);
    dbsfaces.ui.centerVertical(xC);

    $(xI).show();
    $(xC).show();
    //Centraliza tela e exibe
    xH = $(pId + " > div ").outerHeight() / 2;
    xW = $(pId + " > div ").outerWidth() / 2;
    $(pId + " > div ").css("left","50%")
    				  .css("top","50%")
    				  .css("margin-left","-" + xW + "px")
    				  .css("margin-top","-" + xH + "px")
    				  .css("opacity","1");


    dbsfaces.dialog.disableBackgroundInputs(pId);
    
    dbsfaces.dialog.focuOnFirstElement(pId);

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

    //Move dialog
    var xLastPosX;
    var xLastPosY;
    
	$(pId + "> div > .-caption").off("mousedown.dialog")
								.on("mousedown.dialog", function(e){
		if (e.which === 1) {
	    	xLastPosX = e.clientX;
	    	xLastPosY = e.clientY;
	    	$(pId + "> div > .-caption").off("mousemove.dialog")
	        							.on("mousemove.dialog", function(e){
	        	moveWindow(pId, e);
	     	});
			return false; //Inibe a propagação do evento
		}
 	});
	
	$("body").off("mouseout.dialog")
			 .on("mouseout.dialog", function(e){
		$(pId + "> div > .-caption").off("mousemove.dialog");
	});
	
	$(pId + "> div > .-caption").off("mouseup.dialog")
								.on("mouseup.dialog", function(e){
    	$(pId + "> div > .-caption").off("mousemove.dialog");
 	});
    
	moveWindow = function(pId, e){
    	var xDialogPos = $(pId + " > div").offset();
    	xDialogPos.left = xDialogPos.left + (e.clientX - xLastPosX);
    	xDialogPos.top = xDialogPos.top + (e.clientY - xLastPosY);
    	xLastPosX = e.clientX;
    	xLastPosY = e.clientY;
    	$(pId + " > div").offset(xDialogPos);
	}
    
}


dbsfaces.dialog = {
	disableBackgroundInputs: function(pId){
	    var xOP = $(pId).offsetParent();
		xOP.find("input").not('[type="hidden"]').not(pId + " input").attr('disabled', true);
		xOP.find("button").not(pId + " button").attr('disabled', true);
		xOP.find("select").not(pId + " select").attr('disabled', true);
		xOP.find("textarea").not(pId + " textarea").attr('disabled', true);

		dbsfaces.dialog.enableForegroundInputs($(pId));
	},
	
	enableForegroundInputs: function(pE){
		pE.find("input").not(".-disabled").attr('disabled', false);
		pE.find("button").not(".-disabled").attr('disabled', false);
		pE.find("select").not(".-disabled").attr('disabled', false);
		pE.find("textarea").not(".-disabled").attr('disabled', false);
	},
	
	focuOnFirstElement: function(pId){
		//Seta foco no primeiro campo de input
		var xContent = $(pId + " div > .-content");
		var xEle = xContent.find("input:first");
		//Se não achou input, procura por combobox.
		if (xEle.length == 0 ){
			xEle = xContent.find("select:first");
			//Se não achou input, procura por botão.
			if (xEle.length == 0 ){
				xEle = xContent.find("button:first");
			}
		}else{
			
		}
		if (xEle.length != 0 ){
			xEle.focus();
		}
	}


}
