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
	
	$(pId + " > .-container > .-data > .-container > .-content > .-label > .-content").on("mousedown", function(e){
		dbsfaces.charts.activateChart(xCharts, $(this));
	});
};

dbsfaces.charts = {
	initialize: function(pCharts){
		dbsfaces.charts.pvInitializeData(pCharts);
		dbsfaces.charts.pvInitializeGroupMember(pCharts);
		dbsfaces.charts.pvInitializeGuides(pCharts);
		dbsfaces.charts.pvInitializeChartActivate(pCharts);
		//Exibe gráfico
		pCharts.children(".-container").css("opacity",1);
	},

	pvInitializeData: function(pCharts){
		//Salva chart's vinculados a este charts
		pCharts.data("children", pCharts.find(".dbs_chart"));
		pCharts.data("grid", pCharts.find(".dbs_charts-grid").first());
		pCharts.data("fontsize", Number(dbsfaces.number.getOnlyNumber(pCharts.css("font-size"))));
		//Item selecionado temporariamente(Hover)
		pCharts.data("hover", null);
	},

	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvInitializeGuides: function(pCharts){
		dbsfaces.charts.pvCreateDefGuides(pCharts);
//		dbsfaces.charts.lostFocus(pCharts);

//		//Seta posição dos guides do delta
		var xChartsChildren = pCharts.data("children");
		xChartsChildren.each(function(){
			//Verifica se não há registro marca antes de desmarcar
			var xChart = $(this);
			if (typeof(xChart.attr("showdelta")) != 'undefined'){
				if (xChart.attr("type") == "line"){
					dbsfaces.chart.setGuideIndex(xChart, 1);
					dbsfaces.chartValue.select(xChart.data("guide1").data("cv"), null);
					dbsfaces.chart.setGuideIndex(xChart, 2);
					dbsfaces.chartValue.select(xChart.data("guide2").data("cv"), null);
					dbsfaces.chart.setGuideIndex(xChart, 0);
				}
			}

//			dbsfaces.chart.setGuideIndex(pChart, pGuideIndex);
//			dbsfaces.chartValue.select(pChartValue, true);
		});
		dbsfaces.charts.lostFocus(pCharts);
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
			//Circulo externo
			xElement = dbsfaces.svg.ellipse(xMarker,  null, null, "1em", "1em", null, null, "none");
			xElement.svgAttr("stroke", "currentColor");
			//Circulo interno
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, ".2em", ".2em", null, null, "white");
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

	pvInitializeChartActivate: function(pCharts){
		var xContainer = pCharts.children(".-container");
		var xContainerData = xContainer.children(".-data");
		var xContent = xContainerData.find(".-container > .-content");
		var xContentLabel = xContent.children(".-label");
		//Verifica se existe labels definidas
		if (xContentLabel.length != 0
		 && xContentLabel.children().length > 0){
			//Ativa todos os labels
			xContentLabel.children(".-content").svgAddClass("-activated");
			var xHeight = xContentLabel.css("font-size");
			//Ajusta altura conforme tamanho do fonte definido no css. Artíficio pois height do css não funciona em todos os browsers
			xContentLabel.find("rect").attr("height", xHeight);
			//Reposiciona gráfico
			dbsfaces.ui.cssTransform(xContent.children(".-value"), "translateY(" + xHeight + ")");
			//Ativa o primeiro gráfico que possuir delta
			dbsfaces.charts.activateChart(pCharts, xContentLabel.children(".-content:first"));
		}else{
			//Ativa o primeiro gráfico que possuir delta
			var xFirstChart = pCharts.data("children").filter("[showdelta]:first");
			dbsfaces.charts.pvActivateChartOne(xChart, null, true);
			dbsfaces.charts.pvActivateDelta(pCharts, xFirstChart, true);
		}
		//Artifício para corrigir altura do firefox
		var xHeight = xContent[0].getBoundingClientRect().height;
		xContainerData.css("height", xHeight);
		
	},

	pvInitializeGroupMember: function(pCharts){
		var xGroupId = pCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xMembers = $("div.dbs_charts[groupid='" + xGroupId + "']");
			xMembers.each(function(){
				$(this).data("groupmembers", xMembers);
			});
		}else{
			pCharts.data("groupmembers", null);
		}
	},
	
	pvInitializeDimensions: function(pCharts){
		var xGroupId = pCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xMembers = $("div.dbs_charts[groupid='" + xGroupId + "']");
			xMembers.each(function(){
				$(this).data("groupmembers", xMembers);
			});
		}else{
			pCharts.data("groupmembers", null);
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
//			if (typeof(xChart.attr("showdelta")) != "undefined"
//			 || xChart.data("selection").length > 0){
//				xDoUnSelect = false;
//				return;
//			}
			if (xChart.data("selection").length > 0){
				xDoUnSelect = false;
				return;
			}
		});
		if (xDoUnSelect){
			dbsfaces.chartValue.lostFocus(xHover);
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
				return;
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

	},
	
	activateChart: function(pCharts, pLabel){
		var xSiblings = pLabel.siblings();
		var xSiblingsActivated = xSiblings.filter(".-activated");
		var xChart= $("#" + dbsfaces.util.jsid(pLabel.attr("chartid")));
		//Move label para primeiro plano
		dbsfaces.ui.moveToFront(pLabel);
		//Desmarca todos
		xSiblingsActivated.each(function(){
			var xSibilingLabel = $(this);
			var xSibilingChart = $("#" + dbsfaces.util.jsid(xSibilingLabel.attr("chartid")));
			dbsfaces.charts.pvActivateDelta(pCharts, xSibilingChart, false);
			dbsfaces.charts.pvActivateChartOne(xSibilingChart, xSibilingLabel, false);
		});
		//Ativa delta
		dbsfaces.charts.pvActivateDelta(pCharts, xChart, true);
		//Marca selecionado
		dbsfaces.charts.pvActivateChartOne(xChart, pLabel, true);
		dbsfaces.ui.moveToFront(xChart);
	},

	pvActivateChartOne: function(pChart, pLabel, pActivate){
		if (pActivate){
			pChart.svgAddClass("-activated");
			if (pLabel != null){
				pLabel.svgAddClass("-activated");
			}
		}else{
			pChart.svgRemoveClass("-activated");
			if (pLabel != null){
				pLabel.svgRemoveClass("-activated");
			}
		}
	},

	pvActivateDelta: function(pCharts, pChart, pActivate){
		var xChartsChildren = pCharts.data("children");
		xChartsChildren.each(function(){
			if ($(this).data("deltagroup") != null){
				$(this).data("deltagroup").svgRemoveClass("-activated");
			}
		});
		if (pActivate){
			if (pChart != null
			 && pChart.data("deltagroup") != null
			 && pChart.data("children").length > 1){
				pChart.data("deltagroup").svgAddClass("-activated");
			}
		}
	}
		
};

