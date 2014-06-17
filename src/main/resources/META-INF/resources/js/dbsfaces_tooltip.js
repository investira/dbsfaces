dbs_tooltip = function(pId) {
	var wTimer;
	
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
//		if ($(e).has(document.activeElement).length > 0 ||
		if (xTooltip.css("display") != "none"){
			return;
		}
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
		
		wTimer = setTimeout(function(){
			$(xTooltip).fadeIn("fast");
			wTimer = setTimeout(function(){
				$(xTooltip).fadeOut("slow");
			}, xTime);
		}, 1800); //3 Segundos
	},
	
	hideTooltip: function(e){
		clearTimeout(wTimer);
		var xTooltip = $(e).find(".-tooltip");

		$(xTooltip).hide();
	}
}