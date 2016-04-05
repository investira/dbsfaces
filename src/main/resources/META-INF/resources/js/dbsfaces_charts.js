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
	
	
	
	$(pId + " > .-container").on("mouseleave", function(e){
		dbsfaces.chartValue.unSelect(xCharts.data("selected"));
//		console.log("charts\t" + e.originalEvent.type);

//		$(this).data("chart").each(function(){
//			dbsfaces.chart.stopDeltaDrag($(this));
//		});
//		dbsfaces.charts.unSelect($(this));
	});

//	$(pId).on("mouseleave mousedown", function(e){
//		console.log("charts\t" + e.originalEvent.type);
////		$(this).data("chart").each(function(){
////			dbsfaces.chart.stopDeltaDrag($(this));
////		});
//	});
};

dbsfaces.charts = {
	initialize: function(pCharts){
		dbsfaces.charts.pvInitializeData(pCharts);
		dbsfaces.charts.pvCreateDefGuide(pCharts);
		dbsfaces.charts.pvUpdateGroupMember(pCharts);
		pCharts.children(".-container").css("opacity",1);
	},
	
	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		pCharts.data("children", pCharts.find("g.dbs_chart"));
		pCharts.data("grid", pCharts.find(".dbs_charts-grid").first());
		//Item selecionado temporariamente(Hover)
		pCharts.data("selected", null);
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

	select: function(pChartValue, pSelect){
		if (pChartValue == null){return;}
		var xCharts = pChartValue.data("parent").data("parent");
		var xSelected = xCharts.data("selected");
		if (xSelected != null){
			var xSelectedCharts = xSelected.data("parent").data("parent");
			//Ignora se for para selecionar item já selecionado
			if (xSelectedCharts[0].id == xCharts[0].id){
				return;
			}
			//Desmarca item selecionado anteriormente, caso exista
			dbsfaces.charts.pvSelectCharts(xCharts, false);
		}
		//Marca item selecionado
		dbsfaces.charts.pvSelectCharts(xCharts, pSelect);
	},
	
	unSelect: function(pChartValue){
		if (pChartValue==null){return;}
		var xCharts = pChartValue.data("parent").data("parent");
//		xCharts.data("selected", null);
		dbsfaces.charts.pvSelectCharts(xCharts, false);
	},
	
	pvSelectCharts: function(pCharts, pSelect){
		if (pCharts == null){return;}
		var xChartsChildren = pCharts.data("children");
		var xSelect = pSelect;
		var xDim = true;
		//Procupra se algum gráfico tem algum item marcado 
		if (pSelect != null){
			xDim = false;
			xChartsChildren.each(function(){
				xChart = $(this);
				//Se algum guia estiver selecionado,
				if (xChart.data("cv1") != null
				 || xChart.data("cv2") != null){
					xDim = true;
					return;
				}
			});
		}
		if (xDim){
			pCharts.data("grid").svgAddClass("-dim");
		}else{
			pCharts.data("grid").svgRemoveClass("-dim");
		}
		xChartsChildren.each(function(){
			xChart = $(this);
//			console.log(xDim + "\t" + $(this).attr("class") + "\t" + xChart.svgHasClass("-dim"));
			if (xDim){
				if (!xChart.svgHasClass("-dim")){
					xChart.svgAddClass("-dim");
				}
			}else{
				if (xChart.svgHasClass("-dim")){
					xChart.svgRemoveClass("-dim");
				}
			}
		});
	},
	
};

