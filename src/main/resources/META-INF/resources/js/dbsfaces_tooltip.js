dbs_tooltip = function(pId) {
	$(pId).mouseenter(function(e){
		dbsfaces.tooltip.showTooltip(this);
	});

	$(pId).mouseleave(function(e){
		dbsfaces.tooltip.hideTooltip(this);
	});
}

dbsfaces.tooltip = {
	showTooltip: function(e){
		var xTooltip = $(e).find(".-tooltip");
		var xLeft = $(e).offset().left;
		var xTop = $(e).offset().top + $(e).outerHeight();
		/* Se a posição do tooltip for superior ao tamanho do documento, 
		 * ajusta a posição para exibir acima do componente a que se refere o tooltip
		 */
		if (xTop + $(xTooltip).outerHeight() > $(document).height()){
			xTop = $(e).offset().top - $(xTooltip).outerHeight() - 1;
		}
		
		xTooltip.css("top", xTop);
		xTooltip.css("left", xLeft);
		
		var xTime = $(xTooltip).text().length;
		xTime = (xTime / 2) * 200;
		$(xTooltip).show()
				   .delay(xTime)
				   .fadeOut("slow");
	},
	
	hideTooltip: function(e){
		var xTooltip = $(e).find(".-tooltip");
		$(xTooltip).hide();
	}
}