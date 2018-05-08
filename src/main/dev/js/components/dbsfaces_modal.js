dbs_modal = function(pId) {
	
	dbsfaces.modal.initialize($(pId));
    
	var wModal = $(pId + " > .-modal");
    var wCaption = $(pId + " > .-modal > .-container > table > thead > tr > th");

    //Habilita os input caso não encontre mais modals
    //Asterisco necessário, pois o IE não está enviando o evento para o pId
    $(pId + " * ").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
    	if ($(pId).length == 0){
		   var xLastModal = $("body").find(".dbs_modal:last");
		   if (xLastModal.length == 0){
			   var xOP = $(e.currentTarget).offsetParent();
			   dbsfaces.ui.enableForegroundInputs($("body"));
		   }
		   e.stopImmediatePropagation();
	   }
	});

 	//Desabilita inputs atrás do modal
    dbsfaces.ui.disableBackgroundInputs($(pId));
    
    if ($(pId).hasClass("-confirmation")){
        //Exibe tela já que foi criada escondida para dar tempo de todos os componentes serem exibidos
	    	setTimeout(function(){
//	        	dbsfaces.modal.show(wModal);
	    	}, 0);
    }else{
//    		dbsfaces.modal.show(wModal);
    }

    

    //Seta foco no primeiro input disponível
    dbsfaces.ui.focusOnFirstInput($(pId + dbsfaces.modal.getBackgroundComponentSelector()));
    
    
    dbsfaces.modal.dragOff(wModal, wCaption);
	wCaption.on("mousedown.nnnmodal", function(e){
        var xModalPositionOld = {left:  Number.parseFloat(wModal.css("left")),
			 	   			   	 top:  Number.parseFloat(wModal.css("top")),
			 	   			   	 width: wModal.outerWidth(),
			 	   			   	 height: wModal.outerHeight()}; 
//        var xModalPositionOld = {left: wModal.offset().left,
//   			   	 top: wModal.offset().top,
//   			   	 width: wModal.outerWidth(),
//   			   	 height: wModal.outerHeight()}; 
	   	if (e.which === 1){
			dbsfaces.modal.dragOff(wModal, wCaption);
	   		wCaption.on("mousemove.nnnmodal",{pX:e.clientX, pY:e.clientY, pPosOld:xModalPositionOld}, function(e){
//				console.log(wModal.offset().left + ":" + wModal.offset().top + "\t" + 
//						    e.data.pPosOld.left + ":" + e.data.pPosOld.top + "\t" +
//						    e.data.pX + ":" + e.data.pY + "\t" +
//						    e.clientX + ":" + e.clientY  + "\t");
				var xLeft;
				var xTop;
		        //Desliga centralização
//		        if (wModal.css("transform") != "none"){
//			   		wModal.css("transform", "none");
//		   		}
		        //Seta posição
		   		wModal.css("margin", "");
//		   		wModal.css("left", dbsfaces.number.parseFloat(wModal.css("left")) + (e.clientX - e.data.pX) + "px");
//		   		wModal.css("top", dbsfaces.number.parseFloat(wModal.css("top")) + (e.clientY - e.data.pY) + "px");
		   		wModal.css("left", e.data.pPosOld.left + (e.clientX - e.data.pX) + "px");
		   		wModal.css("top", e.data.pPosOld.top + (e.clientY - e.data.pY) + "px");
		   		wModal.addClass("-moving");
		        return false;
			});
	   	}
	});
    
	//Desliga move. Por algum bug, dragstart é eventualmente disparado durante o move
	$(window).on("dragstart", function(e){
		dbsfaces.modal.dragOff(wModal, wCaption);
//		wCaption.off("mousemove.nnnmodal");
	});
	
	//Desliga move
	$(window).on("mouseup.nnnmodal", function(e){
		dbsfaces.modal.dragOff(wModal, wCaption);
//		wCaption.off("mousemove.nnnmodal");
	});
    
}


dbsfaces.modal = {
	initialize: function(pModel){
		dbsfaces.modal.initializeData(pModel);
	},
	
	initializeData: function(pModel){
		var xData = {
				dom : {
					self : pModel,
					modal : pModel.children(".-modal"),
				    caption : pModel.find(" > .-modal > .-container > table > thead > tr > th")
				}
		}
		pModel.data("data", xData);
	},

	show: function(pModal){
		//Fixa tamanho da tela
		pModal.css("width", pModal.outerWidth());
		pModal.css("height", pModal.outerHeight());
        //Centraliza tela e exibe
        var xH = pModal.outerHeight() / 2;
        var xW = pModal.outerWidth() / 2;
        pModal.css("left","50%")
        		   .css("top","50%")
        		   .css("margin-left","-" + xW + "px")
        		   .css("margin-top","-" + xH + "px")
        		   .css("opacity","1");

	},
	
	dragOff: function(pModal, pCaption){
		pModal.removeClass("-moving");
		pCaption.off("mousemove.nnnmodal");
	},

//	disableBackgroundInputs: function(pId){
////	    var xOP = $(pId).offsetParent();
//	    var xOP = $("body");
//		xOP.find("input").not('[type="hidden"]').not(pId + " input").attr('disabled', true);
//		xOP.find("button").not(pId + " button").attr('disabled', true);
//		xOP.find("select").not(pId + " select").attr('disabled', true);
//		xOP.find("a").not(pId + " a").attr('disabled', true);
//		xOP.find("textarea").not(pId + " textarea").attr('disabled', true);
//
//		dbsfaces.modal.enableForegroundInputs($(pId));
//	},
//	
//	enableForegroundInputs: function(pE){
//		pE.find("input").not(".-disabled").attr('disabled', false);
//		pE.find("a").not(".-disabled").attr('disabled', false);
//		pE.find("button").not(".-disabled").attr('disabled', false);
//		pE.find("select").not(".-disabled").attr('disabled', false);
//		pE.find("textarea").not(".-disabled").attr('disabled', false);
//	},
	
	//Retorna componente que contém o conteúdo principal do modal.
	//Este método também é chamado externamente
	getBackgroundComponent: function(pId){
		return $(dbsfaces.util.jsid(pId) + dbsfaces.modal.getBackgroundComponentSelector());
	},

	getBackgroundComponentSelector: function(){
		return " > .-modal > .-container > table > tbody > tr > td";
	}
}
