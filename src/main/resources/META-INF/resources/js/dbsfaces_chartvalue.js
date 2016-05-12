dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
	
	dbsfaces.chartValue.initialize(xChartValue);
	
//	dbsfaces.chartValue.selectDelta(xChart, xChartValue);
	//Seleciona nova posição do delta e do item selecionado
	if (xChartValue.data("parent").attr("type") != "line"){
		$(pId).on("mouseenter", function(e){
//			console.log("chartvalue\t" + e.originalEvent.type + "\t" + $(e.target).attr("class"));
			dbsfaces.chartValue.select(xChartValue, null);
//			e.preventDefault();
			e.stopImmediatePropagation();
			return false;
		});
//		$(pId).on("mouseleave", function(e){
////			console.log("chartvalue\t" + e.originalEvent.type);
//			dbsfaces.chartValue.select(null, null);
////			e.preventDefault();
//			e.stopImmediatePropagation();
//			return false;
//		});
	
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

//	$(pId).on("mouseleave", function(e){
//		console.log("chartvalue\t" + e.originalEvent.type);
//		dbsfaces.chartValue.unSelect(xChartValue);
//		e.preventDefault();
//		return false;
//	});
	
//	//Inicia seleção para cálculo do delta (computer e mobile)
//	$(pId).on("mousedown touchstart", function(e){
//		console.log("mousedown touchstart");
//		dbsfaces.chart.startDeltaDrag(xChart);
//		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
//		e.preventDefault();
//		return false;
//	});
//
//	//Desmaca item selecionado
//	$(pId).mouseleave(function (e){
////		console.log("mouseleave");
//		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, false);
//	});
//	
//	//Seleciona nova posição do delta e do item selecionado
//	$(pId).mousemove(function (e){
////		console.log("mousemove");
//		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
//	});
//
//	//Seleciona nova posição do delta e do item selecionado (Mobile)
//	$(pId).on("≈", function(e){
//		console.log("touchmove");
//		//Verifica se elemento é um dbs_chartValue
//		var xTarget = document.elementFromPoint(e.originalEvent.touches[0].clientX, e.originalEvent.touches[0].clientY);
//		if (typeof(xTarget) != "undefined"){
//			var xTargetChartValue = $(xTarget).parent();
//			if (typeof(xTargetChartValue) != "undefined"){
//				var xClass = xTargetChartValue.attr("class");
//				if (typeof(xClass) != "undefined" 
//				 && xClass.indexOf('dbs_chartValue') != -1){
//					var xTargetChart = xTargetChartValue.closest(".dbs_chart");
//					if (xTargetChart.get(0) == xChart.get(0)){
//						dbsfaces.chartValue.selectValue(xTargetChartValue.closest(".dbs_charts"), xTargetChart, xTargetChartValue, true);
//						e.preventDefault();
//						return false;
//					}
//				}
//			}
//		}
//	});
};



dbsfaces.chartValue = {
	initialize: function(pChartValue){
		//Indicar que nunca foi utilizado para cálculo do delta
		dbsfaces.chartValue.pvInitializeData(pChartValue);
		xChart = pChartValue.data("parent");
		///Inicializa conforme tipo de gráfico
		if (xChart.attr("type") == "pie"){
			dbsfaces.chartValue.pvInitializePie(pChartValue);
		}else if (xChart.attr("type") == "line"){
			dbsfaces.chartValue.pvInitializeLine(pChartValue);
		}	
	},

	pvInitializeData: function(pChartValue){
		var xChart = pChartValue.closest(".dbs_chart");
		pChartValue.data("parent", xChart);

		var xChartValuePoint = pChartValue.children(".-point");
		var xChartValueInfo = pChartValue.children(".-info");
		pChartValue.data("infogroup", xChartValueInfo);
		xValue = xChartValueInfo.children(".-value").text();
		if (xValue != 0){
			//Salva valores dentro do próprio componente para facilar o uso no momento do encode do delta
			pChartValue.data("dx", Number(xChartValuePoint.attr("cx")));
			pChartValue.data("dy", Number(xChartValuePoint.attr("cy")));
			pChartValue.data("df", xChartValuePoint.css("color"));
			pChartValue.data("dv", pChartValue.attr("value"));
			pChartValue.data("dl", pChartValue.attr("label"));
			pChartValue.data("dd", xChartValueInfo.children(".-value").text());
		}else{
			pChartValue.data("dx", null);
		}
	},

	pvInitializeLine: function(pChartValue){
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
		dbsfaces.chartValue.pvHoverChartValue(xHoverCharts, xHoverChart, xHover, false);
		dbsfaces.chart.unSelect(xHover);
	},

	select: function(pChartValue, pSelect){
//		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
//		console.log("guide number\t" + xChart.data("guide"));
//		var xGuideIndex = xChart.data("guide");
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
//		//Desmarca outros chartvalues
//		if (xCurrentCharts.data("groupMembers") != null){
//			xCurrentCharts = xCurrentCharts.data("groupMembers");
//		}
//		xCurrentCharts = xCurrentCharts.filter(".-selected");
////		xCurrentCharts = xCurrentCharts;
//		//Charts
//		xCurrentCharts.each(function(){
//			var xCharts = $(this);
//			var xChartsChildren = xCharts.data("children").not(xCurrentChart); //Ignora chart corrente, já que será matido o -selected
////			var xChartsChildren = xCharts.data("children"); //Ignora chart corrente, já que será matido o -selected
//			//Chart
//			xChartsChildren.each(function(){
//				var xChart = $(this);
//				var xChartValue = xChart.data("cv" + xChart.data("guide"));
//				if (xChartValue != null){
//					console.log("before\t" + $("#" + dbsfaces.util.jsid(xChartValue.get(0).id)).attr("class"));
//				}
//				dbsfaces.chartValue.pvHoverChartValue(xCharts, xChart, xChartValue, false);
//				if (xChartValue != null){
//					console.log("after \t" + $("#" + dbsfaces.util.jsid(xChartValue.get(0).id)).attr("class"));
//				}
////				var xChartChildren = xChart.data("children");
////				var xChartValueSelected = xChartChildren.filter(".-selected");
////				var xChartValueSelected = xChartChildren;
////				console.log(xChartValueSelected.length);
////				xChartValueSelected.each(function(){
////					console.log($(this).attr("class"));
////					console.log($("#" + dbsfaces.util.jsid($(this).get(0).id)).attr("class"));
////					dbsfaces.chartValue.pvHoverChartValue(xChart, $(this), false);
////				});
//			});
//		});
//		var xChart = pChartValue.data("parent");
//		var xCharts = xChart.data("parent");
	},

	pvHoverChartValue: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		//Hover do item
		if (pSelect == null || pSelect){
			//Marca selecionado
			pChartValue.svgAddClass("-hover");
			pCharts.data("hover", pChartValue);
		}else{
			//Desmarca selecionado
			pChartValue.svgRemoveClass("-hover");
			pCharts.data("hover", null);
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
			var xChartValuePoint = pChartValue.children(".-point");
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
			dbsfaces.chart.dataRefreshSelection(pChart);
		}
	}
	
};

