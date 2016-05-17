dbs_quickInfo = function(pId) {
	
	dbsfaces.quickInfo.initialize($(pId));
	
	$(pId + ".-oh > .-icon").on("mouseenter", function(e){
		dbsfaces.quickInfo.showQuickInfo($(pId));
	});
	$(pId + ".-oh > .-icon").on("mouseleave", function(e){
		dbsfaces.quickInfo.hideQuickInfo($(pId));
	});
	$(pId + " > .-icon").on("click", function(e){
		//Desconsidera se origem do click não for no icon
		if (e.target != this){
			return;
		}
		if ($(pId).hasClass("-selected")){
			dbsfaces.quickInfo.hideQuickInfo($(pId));
		}else{
			dbsfaces.quickInfo.showQuickInfo($(pId));
		}
	});
	
}


dbsfaces.quickInfo = {
	initialize: function(pComponent){
		dbsfaces.quickInfo.initializeData(pComponent);
		dbsfaces.tooltip.initialize(pComponent.data("content"));
	},
	
	initializeData: function(pComponent){
		var xContent = pComponent.find(".-icon > .-content");
		pComponent.data("content", xContent);
	},
		
	showQuickInfo: function(pComponent){
		//Exibe quickinfo
		if (!dbsfaces.tooltip.show(pComponent.data("content"))){return;}

		//Esconde tooltip caso exista para não poluir a exibição do quickinfo
		dbsfaces.tooltip.hide(pComponent);
		//Desabilita tooltip para não exibi-lo enquando o quickinfo estiver ativo
		dbsfaces.tooltip.disable(pComponent);

		//Replica no quickinfo a class que indica a localicação do tooptip 
		var xTooltip = pComponent.data("content").data("tooltip");
		pComponent.removeClass("-l1 -l2 -l3 -l4");
		if (xTooltip.hasClass("-l1")){
			pComponent.addClass("-l1");
		}else if (xTooltip.hasClass("-l2")){
			pComponent.addClass("-l2");
		}else if (xTooltip.hasClass("-l3")){
			pComponent.addClass("-l3");
		}else if (xTooltip.hasClass("-l4")){
			pComponent.addClass("-l4");
		} 

//		var xContainer = pComponent.data("content").find(".-tooltip > .-container");
//		xContainer.css("width", xContainer.outerWidth());
//		xContainer.css("height", xContainer.outerHeight());

		//Indica que item foi selecionado
		pComponent.addClass("-selected");

		//Trava scroll
		var xScrollParent = pComponent.scrollParent();
		var xSt = window.getComputedStyle(xScrollParent.get(0), null);
		var xOf = xSt.getPropertyValue("overflow");
		xScrollParent.attr("of",xOf).css("overflow","hidden"); //Salva overflow original e altera para hidden
	},

	hideQuickInfo: function(pComponent){
		pComponent.removeClass("-selected");
		dbsfaces.tooltip.hide(pComponent.data("content"));
		//Reabilita tooltip 
		dbsfaces.tooltip.enable(pComponent);
		
		//Restaura scroll original
		var xScrollParent = pComponent.closest("[of]");
		var xOf = xScrollParent.attr("of");
		xScrollParent.attr("of", "").css("overflow", xOf);
	}
}

