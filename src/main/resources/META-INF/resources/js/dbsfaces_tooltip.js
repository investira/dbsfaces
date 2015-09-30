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
	showTooltip: function(e){
		var xTooltip = $(e).find(".-tooltip");
		/* Se o foco estiver em algum compenente filho */

		if (xTooltip.css("display") != "none"){
			return;
		}
		
		//Tempo de exibição
		var xTime = dbsfaces.ui.getDelayFromTextLength(xTooltip.text());
		
		wTimer = setTimeout(function(){
			var xLeft = $(e).offset().left;
			var xTop = $(e).offset().top + $(e).outerHeight();
			/* Se a posição do tooltip for superior ao tamanho do documento, 
			 * ajusta a posição para exibir próximo do componente a que se refere o tooltip
			 */
			if (xTop + xTooltip.outerHeight() > $(document).height()){
				xTop = $(e).offset().top - xTooltip.outerHeight() - 1;
			}
	
			xTooltip.css("top", xTop);
			xTooltip.css("left", xLeft);

			xTooltip.show();
			/* Ajusta para exibir dentro dos limites horizontais da tela
			 */
			if (xTooltip.get(0).getBoundingClientRect().left + xTooltip.outerWidth() > $(document).width() ){
				xLeft = $(document).width() - xTooltip.outerWidth() - 1;
				xTooltip.css("left", xLeft);
			}
			wTimer = setTimeout(function(){
				xTooltip.fadeOut("slow");
			}, xTime);
		}, 1200); //2 Segundos -Tempo para exibir
	},
	
	hideTooltip: function(e){
		if (typeof(wTimer) != "undefined"){
			clearTimeout(wTimer);
			var xTooltip = $(e).find(".-tooltip");

			xTooltip.hide();
		}
	}
}