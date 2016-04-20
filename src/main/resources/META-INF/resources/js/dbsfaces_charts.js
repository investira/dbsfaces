dbs_charts = function(pId) {
	var xCharts = $(pId);

	dbsfaces.charts.initialize(xCharts);

	//TODO resolver lostfocus no firefox
	$(pId + "> .-container" ).on("mouseleave", function(e){
		dbsfaces.charts.lostFocus(xCharts);
//		console.log("charts\t" + e.originalEvent.type);
	});

};

dbsfaces.charts = {
	initialize: function(pCharts){
		dbsfaces.charts.pvInitializeData(pCharts);
		dbsfaces.charts.pvCreateDefGuides(pCharts);
		dbsfaces.charts.pvUpdateGroupMember(pCharts);
		pCharts.children(".-container").css("opacity",1);
	},
	
	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		pCharts.data("children", pCharts.find("g.dbs_chart"));
		pCharts.data("grid", pCharts.find(".dbs_charts-grid").first());
		//Item selecionado temporariamente(Hover)
		pCharts.data("hover", null);
	},

	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvCreateDefGuides: function(pCharts){
		var xDefs = pCharts.find("svg > defs");
		var xMarker = xDefs.children(".-point");
		var xElement;
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, null, null, null,null, null, null);
			xMarker.svgAttr("id", pCharts.get(0).id + "_point");
			xMarker.svgAttr("class", "-point");
			xElement = dbsfaces.svg.ellipse(xMarker,  null, null, "1em", "1em", null, null, null);
			xElement.svgAttr("stroke", "currentColor");
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, ".2em", ".2em", null, null, "none");
			xElement.svgAttr("stroke", "currentColor");
		}
		var xMarker = xDefs.children(".-guide");
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, null, null, null,null, null, null);
			xMarker.svgAttr("id", pCharts.get(0).id + "_guide");
			xMarker.svgAttr("class", "-guide");
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, "20px", "20px", null, null, "none"); //"rx:inherit; ry:inherit;"
//			xElement.svgAttr("stroke", "currentColor");
//			xElement.svgAttr("stroke-width", "1px");
			xElement = dbsfaces.svg.rect(xMarker, "-3.5", "-8.5", "6px", "16px", "2", "2", null, null, "white");
			xElement.svgAttr("stroke", "currentColor");
//			xElement.svgAttr("stroke-width", "1px");
			xElement = dbsfaces.svg.rect(xMarker, "-2", "-5", "1px", "9px", null, null, null, null, "currentColor");
			xElement = dbsfaces.svg.rect(xMarker, "0", "-5", "1px", "9px", null, null, null, null, "currentColor");
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

	lostFocus: function(pCharts){
		var xHover = pCharts.data("hover");
		if (xHover != null
		 && xHover.svgHasClass("-selected")){
			return;
		}
		//Verifica se não há registro marca antes de desmarcar
		var xDoUnSelect = true;
		var xChartsChildren = pCharts.data("children");
		xChartsChildren.each(function(){
			//Verifica se não há registro marca antes de desmarcar
			xChart = $(this);
			if (xChart.data("selection").length > 0){
				xDoUnSelect = false;
				return;
			}
		});
		if (xDoUnSelect){
			dbsfaces.chartValue.unSelect(xHover);
		}
		xChartsChildren.each(function(){
			xChart = $(this);
			xChart.data("guideIndex", 0);
//			//Desmarcar qualquer guia 
//			dbsfaces.chart.lostFocus($(this));
		});
	},
	
	select: function(pChartValue, pSelect){
//		if (pChartValue == null){return;}
		var xCharts = pChartValue.data("parent").data("parent");
		var xHover = xCharts.data("hover");
		if (xHover != null){
			var xHoverCharts = xHover.data("parent").data("parent");
			//Ignora se for para selecionar item já selecionado
			if (xHoverCharts[0].id == xCharts[0].id){
				return;
			}
		}
		//Desmarca item selecionado anteriormente, caso exista
		dbsfaces.charts.pvSelectCharts(xCharts, false);
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
	}
	
};

