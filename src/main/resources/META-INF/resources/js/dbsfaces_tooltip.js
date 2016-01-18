dbs_tooltip = function(pId) {
	var wTimer = +new Date();

	$(pId).mouseenter(function(e){
		dbsfaces.tooltip.showTooltip(pId);
	});

	$(pId).mouseleave(function(e){
		dbsfaces.tooltip.hideTooltip(pId);
	});
	
	$(pId + " input").focus(function(e){
		dbsfaces.tooltip.hideTooltip(pId);
	});

	$(pId + " input").click(function(e){
		dbsfaces.tooltip.hideTooltip(pId);
	});

}

dbsfaces.tooltip = {
	showTooltip: function(pId){
		var xTooltip = $(pId).find(".-tooltip > .-container");
		/* Se o foco estiver em algum compenente filho */


		//Tempo de exibição
		var xTime = dbsfaces.ui.getDelayFromTextLength(xTooltip.text());
		
		wTimer = setTimeout(function(){
			if ($(pId).length == 0){
				return;
			}
			var xLeft = $(pId).get(0).getBoundingClientRect().left - (xTooltip.outerWidth() / 2);
			var xTop = $(pId).get(0).getBoundingClientRect().top - xTooltip.outerHeight() - 8; //* é o espaço para o triangulo
			//Austa altura exibir dentro dos verticais da tela
			if (xTop + xTooltip.outerHeight() > $(document).height()){
				xTop = $(document).height() - xTooltip.outerHeight() - 1;
			}
			
	
			xTooltip.css("top", xTop);
			xTooltip.css("left", xLeft);

			xTooltip.show();
			//Ajusta para exibir dentro dos limites horizontais da tela
			if (xLeft + xTooltip.outerWidth() > $(document).width() ){
				xLeft = $(document).width() - xTooltip.outerWidth() - 1;
				xTooltip.css("left", xLeft);
			}
			wTimer = setTimeout(function(){
				xTooltip.fadeOut("slow");
			}, xTime);
		}, 1200); //2 Segundos -Tempo para exibir
	},
	
	hideTooltip: function(pId){
		if (typeof(wTimer) != "undefined"){
			clearTimeout(wTimer);
			var xTooltip = $(pId).find(".-tooltip > .-container");;

			xTooltip.hide();
		}
	}
}