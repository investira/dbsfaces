dbs_charts = function(pId) {
	var xCharts = $(pId);
//	var xChart = $(pId).find("g.dbs_chart");
	//Salva chart's vinculados a este charts
//	xCharts.data("chart", xChart);
	dbsfaces.charts.initialize(xCharts);

//	$(pId).on("mousedown", function(e){
//		xCharts.each(function(){
//			dbsfaces.chart.hideDelta($(pId), $(this));
//		});
//	});
	
	
	
	$(pId).on("mouseleave mouseup", function(e){
		$(this).data("chart").each(function(){
//			dbsfaces.chart.stopDeltaDrag($(this));
		});
//		dbsfaces.charts.unSelect(xCharts);
	});

};

dbsfaces.charts = {
	initialize: function(pCharts){
		dbsfaces.charts.pvInitializeData(pCharts);
		dbsfaces.charts.pvCreateDefGuide(pCharts);
		dbsfaces.charts.pvUpdateGroupMember(pCharts);
	},
	
	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		var xChart = pCharts.find("g.dbs_chart");
		pCharts.data("chart", xChart);
		pCharts.data("grid", pCharts.find(".dbs_charts-grid").first());
	},

	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvCreateDefGuide: function(pCharts){
		var xDefs = pCharts.find("svg > defs");
		var xMarker = xDefs.children(".-guide");
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, null, null, null,null, null, null);
			xMarker.svgAttr("id", pCharts.get(0).id + "_guide");
			xMarker.svgAttr("class", "-guide");
			var xEllipse;
			xEllipse = dbsfaces.svg.ellipse(xMarker, "0", "0", "1em", "1em", null, null, null);
			xEllipse.attr("stroke-width", ".1em");
			xEllipse.attr("fill", "none");
			xEllipse = dbsfaces.svg.ellipse(xMarker, "0", "0", ".2em", ".2em", null, null, null);
			xEllipse.attr("stroke-width", ".1em");
			xEllipse.attr("fill", "none");
		}
	},
	
	pvUpdateGroupMember: function(pCharts){
		var xGroupId = pCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xMembers = $("div.dbs_charts[groupid='" + xGroupId + "']");
			xMembers.each(function(){
				$(this).data("groupMembers", xMembers);
			});
		}else{
			pCharts.data("groupMembers", null);
		}

	},

	select: function(pCharts){
		var xCharts = pCharts.data("groupMembers");
		if (xCharts == null){
			xCharts = pCharts;
		}
//		setTimeout(function(){
//			if (!xCharts.hasClass("-dim")){
//				xCharts.addClass("-dim");
//			}
//		},1000);

		xCharts.each(function(){
			$(this).data("grid").svgAddClass("-dim");
			xChart = $(this).data("chart");
			xChart.each(function(){
				$(this).svgAddClass("-dim");
			});
		});
	},
	
	unSelect: function(pCharts){
		var xCharts = pCharts.data("groupMembers");
		if (xCharts == null){
			xCharts = pCharts;
		}
//		setTimeout(function(){
//			if (xCharts.hasClass("-dim")){
//				xCharts.removeClass("-dim");
//			}
//		},1000);
		xCharts.each(function(){
			$(this).data("grid").svgRemoveClass("-dim");
			xChart = $(this).data("chart");
			xChart.each(function(){
				$(this).svgRemoveClass("-dim");
			});
		});
		//Desmaca seleção do gráficos filhos
		xCharts.each(function(){
			dbsfaces.chart.unSelect($(this).data("chart")); 
		});
	}
	
};

