dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
	
	dbsfaces.chartValue.initialize(xChartValue);
	
	//Seleciona nova posição do delta e do item selecionado
	if (xChartValue.data("parent").attr("type") != "line"){

		$(pId).on("mouseenter", function(e){
//			console.log("chartvalue\t" + e.originalEvent.type + "\t" + $(e.target).attr("class"));
			dbsfaces.chartValue.select(xChartValue, null);
//			e.preventDefault();
			e.stopImmediatePropagation();
			return false;
		});
	
		$(pId).on("mousedown touchstart", function(e){
//			console.log("chartvalueX\t" + e.originalEvent.type);
			//Artifício para desconsiderar mousedown em mobile
			if (e.originalEvent.type != "mousedown"
			 || !dbsfaces.util.isMobile()){
				dbsfaces.chartValue.select(xChartValue, true);
			}
//			e.preventDefault();
			e.stopImmediatePropagation();
			return false;
		});
	}else{
		$(pId).on("mouseenter", function(e){
			var xChart = $(this).data("parent");
			if (!xChart.svgHasClass("-activated")){
				return;
			}
			dbsfaces.chart.setGuideIndex(xChart, 0);
			dbsfaces.chartValue.select(xChartValue, null);
			e.stopImmediatePropagation();
			return false;
		});
	}

};



dbsfaces.chartValue = {
	initialize: function(pChartValue){
		//Indicar que nunca foi utilizado para cálculo do delta
		dbsfaces.chartValue.pvInitializeData(pChartValue);
		xChart = pChartValue.data("parent");
		///Inicializa conforme tipo de gráfico
		if (xChart.attr("type") == "pie"){
			dbsfaces.chartValue.pvInitializePie(pChartValue);
		}else if (xChart.attr("type") == "line"
			   || xChart.attr("type") == "bar"){
			dbsfaces.chartValue.pvInitializeBoxAndLine(pChartValue);
		}	
	},

	pvInitializeData: function(pChartValue){
		var xChart = pChartValue.closest(".dbs_chart");
		pChartValue.data("parent", xChart);
		var xChartValuePoint = pChartValue.children(".-point");
		pChartValue.data("pointgroup", xChartValuePoint);
		var xChartValueInfo = pChartValue.children(".-info");
		pChartValue.data("infogroup", xChartValueInfo);
		xValue = xChartValueInfo.children(".-value").text();
		if (xValue != 0){
			//Salva valores dentro do próprio componente para facilar o uso no momento do encode do delta
			//Posição X
			pChartValue.data("dx", Number(xChartValuePoint.attr("cx")));
			//Posição Y
			pChartValue.data("dy", Number(xChartValuePoint.attr("cy")));
			//Cor 
			pChartValue.data("df", xChartValuePoint.css("color"));
			//Valor nominal
			pChartValue.data("dv", pChartValue.attr("value"));
			//Percentual
			pChartValue.data("dp", pChartValue.attr("perc"));
			//Label
			pChartValue.data("dl", pChartValue.attr("label"));
			//Valor para exibição
			pChartValue.data("dd", xValue);
			//Tooltip
			var xChartValueTooltip = $(dbsfaces.util.jsid(pChartValue.get(0).id) + "_tooltip");
			if (xChartValueTooltip.length > 0){
				pChartValue.data("tooltip", xChartValueTooltip);
			}else{
				pChartValue.data("tooltip", null);
			}
		}else{
			pChartValue.data("dx", null);
		}
	},

	pvInitializeBoxAndLine: function(pChartValue){
		var xChart = pChartValue.data("parent");
		var xChartValueInfo = pChartValue.data("infogroup");
		var xChartValueLabel = xChartValueInfo.children(".-label");
		if (xChartValueLabel.length==0){ return;}
		var xX = Number(xChartValueLabel.attr("x"));
		var xY = Number(xChartValueLabel.attr("y"))
		//Ajusta X se houver deltalist a ser exibido utilizando a altura do corrente do gráfico
		if (typeof(xChart.attr("showdeltalist")) != "undefined"){
			xY += parseFloat(xChart.css("font-size")) * 2;
			xChart.data("deltalistheight", xY);
		}	
		var xTransform = "translate(" + xX + "," + xY + ")";
		xChartValueLabel.svgAttr("transform", xTransform);
	},

	pvInitializePie: function(pChartValue){
		var xChartValueInfo = pChartValue.data("infogroup");
		var xChartValueValue = xChartValueInfo.children(".-value");
		var xChartValueBox = xChartValueInfo.children(".-box");
		var xTransform = "translateY(-0.70em) ";
		var xWidth = xChartValueValue.get(0).getComputedTextLength() + 5;
		//Ajusta largura do box do label
		if (xChartValueValue.length != 0){
			xChartValueBox.attr("width", xWidth);
			if (xChartValueValue.attr("style").indexOf("text-anchor:end") != -1){
				xTransform += "translateX(-" + xWidth + "px)";
			}
			dbsfaces.ui.cssTransform(xChartValueBox, xTransform);
		}
	},
	
	pvIsEqual: function(pChartValue, pChartValueNew){
		if (pChartValue != null && pChartValueNew != null){
			//Ignora se for para seleciona o mesmo item
			if (pChartValue[0].id == pChartValueNew[0].id){
				return true;
			}
		}
		return false;
	},
	
	lostFocus: function(pChartValue){
		if (pChartValue == null){return;}
		var xHoverChart = pChartValue.data("parent");
		var xHoverCharts = xHoverChart.data("parent");
		var xHover = xHoverCharts.data("hover");
		//dbsfaces.chartValue.pvHoverChartValue(xHoverCharts, xHoverChart, xHover, false); COmentado em 2016/08/16 - Já efetuado no unSelect
		dbsfaces.chart.unSelect(xHover);
	},

	select: function(pChartValue, pSelect, pIsGroupMemberSelect){
		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
//		console.log("guide number\t" + xChart.data("guide"));
		var xCharts = xChart.data("parent");
		var xHover = null;
		var xHoverChart;
		var xHoverCharts;

		//Desmarca item selecionado anteriormente
		if (pSelect == null){
			xHover = xCharts.data("hover");
			//Se houver item selectionado
			if (xHover != null){
				//Ignora se for para selecionar item já selecionado
				if (dbsfaces.chartValue.pvIsEqual(pChartValue, xHover)){
					return;
				}
				xHoverChart = xHover.data("parent");
				xHoverCharts = xHoverChart.data("parent");
			}
		}

		//Configura seleção do chart pai. obs:Esta chamada previsa ser anterior ao unselect para verificar se item selecionado atual 
		dbsfaces.chart.select(pChartValue, null);

		//Desmarca item selecionado anteriormente
		dbsfaces.chartValue.pvHoverChartValue(xHoverCharts, xHoverChart, xHover, false);

		//Marca item selecionado
		dbsfaces.chartValue.pvHoverChartValue(xCharts, xChart, pChartValue, null);
			
		if (pSelect != null){	
			pSelect = !pChartValue.svgHasClass("-selected");
			if (pSelect){
				dbsfaces.chart.select(pChartValue, pSelect);
				dbsfaces.chartValue.pvSelectChartValue(xCharts, xChart, pChartValue, pSelect);
			}else{
				dbsfaces.chartValue.unSelect(pChartValue);
			}
		}
		if (pIsGroupMemberSelect == null){
			//Seleciona valores nos gráficos que pertencem ao mesmo groupid
			dbsfaces.chartValue.pvSelectGroupMembers(xCharts, xChart, pChartValue, true);
		}
	},
	
	unSelect: function(pChartValue){
		if (pChartValue == null){return;}
		var	xChart = pChartValue.data("parent");
		var	xCharts = xChart.data("parent");

		//Desmarca chartValue valor informado
		dbsfaces.chartValue.pvSelectChartValue(xCharts, xChart, pChartValue, false);
		dbsfaces.chartValue.pvHoverChartValue(xCharts, xChart, pChartValue, false);
		//Configura seleção do chart pai
		dbsfaces.chart.unSelect(pChartValue);

		return;
	},

	pvSelectGroupMembers: function(pCharts, pChart, pChartValue, pSelect){
		if (pCharts.data("groupmembers") == null){return;}
		var xGroupMembers = pCharts.data("groupmembers").not(pCharts);
		//Charts
		xGroupMembers.each(function(){
			var xCharts = $(this);
			//Chart filhos
			if (typeof(xCharts.data("children")) != "undefined"){
				//Desmaca qualquer valor selecionado no gráfico irmão
				if (xCharts.data("hover") != null){
					dbsfaces.chartValue.unSelect(xCharts.data("hover"));
				}
				var xChart = $(this).data("children").filter(".-activated");
				xChart.each(function(){
					//ChartValue filhos
					var xChartValue = $(this).data("children");
					if (typeof(xChartValue) != "undefined"){
						xChartValue.each(function(){
							//Se label for iqual
							if ($(this).data("dl") == pChartValue.data("dl")){
								if(pSelect){
									dbsfaces.chartValue.select($(this), true, true);
								}else{
									dbsfaces.chartValue.unSelect($(this));
								}
							}
						});
					}
				});
			}
		});
	},

	pvHoverChartValue: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		//Hover do item
		if (pSelect == null || pSelect){
			//Marca selecionado
			pChartValue.svgAddClass("-hover");
			pCharts.data("hover", pChartValue);
//			setTimeout(function(){
			dbsfaces.tooltip.showDelayed(pChartValue.data("tooltip"));
//			},0);
		}else{
			//Desmarca selecionado
			pChartValue.svgRemoveClass("-hover");
			pCharts.data("hover", null);
//			setTimeout(function(){
			dbsfaces.tooltip.hide(pChartValue.data("tooltip"));
//			},0);
		}
	},

	pvSelectChartValue: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		//Verifica se gráfico possui guia
		var xGuideIndex = pChart.data("guideIndex");
		var xGuide = null;
		var xType = pChart.attr("type");
		//Seleção do item
		if (pSelect !=null){
			var xChartValuePoint = pChartValue.data("pointgroup");
			var xAnimationClass = "-a_dbs_chart_selected";
			if (pSelect){
				dbsfaces.ui.moveToFront(pChartValue);
				if (xType == "pie"
			     || xType == "bar"){
					//Seleciona
					pChartValue.svgAddClass("-selected");
					//Move delta para frente do gráfico
					if (xType == "pie"){
						dbsfaces.ui.moveToFront(pChart.data("deltagroup"));
					}
				}
				//Animação
				if (xChartValuePoint !=null){
					xChartValuePoint.svgAddClass(xAnimationClass);
					xChartValuePoint.off(dbsfaces.EVENT.ON_ANIMATION_END);
					xChartValuePoint.on(dbsfaces.EVENT.ON_ANIMATION_END, function(e){
						$(this).svgRemoveClass(xAnimationClass);
					});
				}
			}else{
				if (xType == "pie"
				 || xType == "bar"){
					//Remove seleção
					pChartValue.svgRemoveClass("-selected");
				}
				//Se possuir guia, desmarca seleção
				//Animação
				if (xChartValuePoint !=null){
					xChartValuePoint.svgRemoveClass(xAnimationClass);
				}
			}
			//Atualiza controle de itens selecionados 
			dbsfaces.chart.dataRefreshSelection(pChart);
		}
	}
	
};

