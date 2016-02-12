dbs_quickInfo = function(pId) {
	$(pId + ".-oh > .-icon").on("mouseenter", function(e){
		dbsfaces.quickInfo.showQuickInfo(pId);
	});
	$(pId + ".-oh > .-icon").on("mouseleave", function(e){
		dbsfaces.quickInfo.hideQuickInfo(pId);
	});
	$(pId + " > .-icon").on("click", function(e){
		//Desconsidera se origem do click não for no icon
		if (e.target != this){
			return;
		}
		if ($(pId).hasClass("-selected")){
			dbsfaces.quickInfo.hideQuickInfo(pId);
		}else{
			dbsfaces.quickInfo.showQuickInfo(pId);
		}
	});
	
}


dbsfaces.quickInfo = {
		
	showQuickInfo: function(pId){
//		var xTooltip = $(pId + " > .-icon > .-content > .-tooltip");
		//Exibe tooltip
		if (!dbsfaces.tooltip.show(pId + " > .-icon > .-content", "qi", $(pId).attr("dl"))){return;}
		//Esconde tooltip caso exista para não poluir a exibição do quickinfo
		dbsfaces.tooltip.hideTooltip(pId);
		//Desabilita tooltip para não exibi-lo enquando o quickinfo estiver ativo
		dbsfaces.tooltip.disableTooltip(pId);
		
		//Replica no quickinfo a class que indica a localicação do tooptip 
		var xTooltip = $(pId + " > .-icon > .-content > .-tooltip.-qi");
		$(pId).removeClass("-l1 -l2 -l3 -l4");
		if (xTooltip.hasClass("-l1")){
			$(pId).addClass("-l1");
		}else if (xTooltip.hasClass("-l2")){
			$(pId).addClass("-l2");
		}else if (xTooltip.hasClass("-l3")){
			$(pId).addClass("-l3");
		}else if (xTooltip.hasClass("-l4")){
			$(pId).addClass("-l4");
		} 
		var xContainer = $(pId + " > .-icon > .-content > .-tooltip > .-container");
		xContainer.css("width", xContainer.outerWidth());
		xContainer.css("height", xContainer.outerHeight());
		//Infica que item foi selecionado
		$(pId).addClass("-selected");

		//Trava scroll
		var xScrollParent = $(pId).scrollParent();
		var xSt = window.getComputedStyle(xScrollParent.get(0), null);
		var xOf = xSt.getPropertyValue("overflow");
		xScrollParent.attr("of",xOf).css("overflow","hidden"); //Salva overflow original e altera para hidden
	},

	hideQuickInfo: function(pId){
		dbsfaces.tooltip.hide(pId + " > .-icon > .-content", "qi");
		//Reabilita tooltip 
		dbsfaces.tooltip.enableTooltip(pId);
		$(pId).removeClass("-selected");
		//Restaura scroll original
		var xScrollParent = $(pId).closest("[of]");
		var xOf = xScrollParent.attr("of");
		xScrollParent.attr("of", "").css("overflow", xOf);
	}
}

