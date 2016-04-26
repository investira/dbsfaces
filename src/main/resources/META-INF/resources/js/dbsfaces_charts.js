dbs_charts = function(pId) {
	var xCharts = $(pId);

	dbsfaces.charts.initialize(xCharts);

	$(pId).mouseleave(function(e){
		//Verificar se componente que disparou esta fora deste chart. Artifício necessário pois o Firefox dispara este evento diversas vezes 
		var xE = $(e.originalEvent.toElement || e.relatedTarget).closest(".dbs_charts");
        if (xE == "undefined"
        || xE[0] != this ){
    		dbsfaces.charts.lostFocus(xCharts);
        }
 	});
};

dbsfaces.charts = {
	initialize: function(pCharts){
		dbsfaces.charts.pvInitializeData(pCharts);
		dbsfaces.charts.pvUpdateGroupMember(pCharts);
		dbsfaces.charts.pvInitializeGuides(pCharts);
		pCharts.children(".-container").css("opacity",1);
	},
	
	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		pCharts.data("children", pCharts.find(".dbs_chart"));
		pCharts.data("grid", pCharts.find(".dbs_charts-grid").first());
		//Item selecionado temporariamente(Hover)
		pCharts.data("hover", null);
	},

	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvInitializeGuides: function(pCharts){
		dbsfaces.charts.pvCreateDefGuides(pCharts);

		//Seta posição dos guides do delta
		var xChartsChildren = pCharts.data("children");
		xChartsChildren.each(function(){
			//Verifica se não há registro marca antes de desmarcar
			var xChart = $(this);
			if (typeof(xChart.attr("showdelta")) != 'undefined'){
				dbsfaces.chart.setGuideIndex(xChart, 1);
				dbsfaces.chartValue.select(xChart.data("guide1").data("cv"), null);
				dbsfaces.chart.setGuideIndex(xChart, 2);
				dbsfaces.chartValue.select(xChart.data("guide2").data("cv"), null);
				dbsfaces.chart.setGuideIndex(xChart, 0);
			}

//			dbsfaces.chart.setGuideIndex(pChart, pGuideIndex);
//			dbsfaces.chartValue.select(pChartValue, true);
		});
	},
	
	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvCreateDefGuides: function(pCharts){
		var xDefs = pCharts.find("svg > defs");
		var xMarker = xDefs.children(".-point");
		var xElement;
		//Cria quia individual
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, null, null, null, null);
			xMarker.svgAttr("id", pCharts.get(0).id + "_point");
			xMarker.svgAttr("class", "-point");
			xElement = dbsfaces.svg.ellipse(xMarker,  null, null, "1em", "1em", null, null, null);
			xElement.svgAttr("stroke", "currentColor");
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, ".2em", ".2em", null, null, "none");
			xElement.svgAttr("stroke", "currentColor");
		}
		//Cria cria de calculo do delta
		var xMarker = xDefs.children(".-guide");
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, null, null, null, null);
			xMarker.svgAttr("id", pCharts.get(0).id + "_guide");
			xMarker.svgAttr("class", "-guide");
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, "20px", "20px", null, null, "none"); //"rx:inherit; ry:inherit;"
			xElement = dbsfaces.svg.rect(xMarker, "-3.5", "-8.5", "6px", "16px", "2", "2", null, null, "white");
			xElement.svgAttr("stroke", "currentColor");
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
		if (xHover != null){
			xHover.svgRemoveClass("-hover");
		}

		//Verifica se não há registro marca antes de desmarcar

		var xDoUnSelect = true;
		var xChartsChildren = pCharts.data("children");
		xChartsChildren.each(function(){
			//Verifica se não há registro marca antes de desmarcar
			var xChart = $(this);
			if (xChart.data("selection").length > 0){
				xDoUnSelect = false;
				return;
			}
		});
		if (xDoUnSelect){
			dbsfaces.chartValue.unSelect(xHover);
		}
//		//Desmarcar qualquer guia 
		xChartsChildren.each(function(){
			dbsfaces.chart.lostFocus($(this));
		});
	},
	
	select: function(pChartValue, pSelect){
//		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");
		var xHover = xCharts.data("hover");
		if (xHover != null){
			var xHoverCharts = xHover.data("parent").data("parent");
			//Ignora se for para selecionar item já selecionado
			if (xHoverCharts[0].id == xCharts[0].id){
//				return;
			}
		}
		//Desmarca item selecionado anteriormente, caso exista
		dbsfaces.charts.pvSelectCharts(xCharts, xChart, pChartValue, false);
		//Marca item selecionado
		dbsfaces.charts.pvSelectCharts(xCharts, xChart, pChartValue, pSelect);
	},
	
	unSelect: function(pChartValue){
		if (pChartValue==null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");
//		xCharts.data("selected", null);
		dbsfaces.charts.pvSelectCharts(xCharts, xChart, pChartValue, false);
	},
	
	pvSelectCharts: function(pCharts, pChart, pChartValue, pSelect){
		if (pCharts == null){return;}
		var xDim = null;
		var xChartsChildren = pCharts.data("children");
		if (pSelect == null || pSelect){
			//Ativa o DIM em todos os chart que não estiverem já setados
			xChartsChildren.each(function(){
				var xChart = $(this);
				if (!xChart.svgHasClass("-dim")){
					xChart.svgAddClass("-dim");
					xDim = true; //Indica que houve alteração
				}
			});
		}else{
			if (pCharts.data("hover") == null){
				xDim = false;
				//Desativa o DIM somente se não houver algum item selecionado
				xChartsChildren.each(function(){
					var xChart = $(this);
					//Se for o próprio chart, verifica se o item a ser deselecionado é o mesmo que está selecionado na lista 
					if (pChart[0].id == xChart[0].id){
						if ((xChart.data("selection").length > 1)
						 || (xChart.data("selection").length > 0
						  && xChart.data("selection")[0].id != pChartValue[0].id)){
							xDim = null; //Cancela desativação do DIM
							return;
						}
					}else if(xChart.data("selection").length > 0){
						xDim = null;  //Cancela desativação do DIM
						return;
					}
				});
				//Desativa DIM de todos os chart
				if (xDim != null){
					xDim = null;
					xChartsChildren.each(function(){
						var xChart = $(this);
						if (xChart.svgHasClass("-dim")){
							xChart.svgRemoveClass("-dim");
							xDim = false;
						}
					});			
				}
			}
		}
		//DIM dos valores da linha no grid(Bar e Line)
		if (pChart.attr("type") == "line"
		 || pChart.attr("type") == "bar"){
			if (xDim != null){
				if (xDim){
					if (!pCharts.data("grid").svgHasClass("-dim")){
			 			pCharts.data("grid").svgAddClass("-dim");
					}
				}else{
					if (pCharts.data("grid").svgHasClass("-dim")){
						pCharts.data("grid").svgRemoveClass("-dim");
					}
				}
			}
		}

	}
	
};

