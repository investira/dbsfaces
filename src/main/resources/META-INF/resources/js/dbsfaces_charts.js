dbs_charts = function(pId, pPreRender, pDeltaList) {
	var xCharts = $(pId);
	//Prepara gráfico com a altura e largura
	$(window).resize(function(e){
		dbsfaces.charts.resize(xCharts);
	});
	
	if (pPreRender){
		dbsfaces.charts.preInitialize(xCharts, false);
	//Render final do gráfico
	}else{
		dbsfaces.charts.initialize(xCharts, pDeltaList);
		//Exibe gráfico
		xCharts.data("container").removeClass("-hide");
		if (xCharts.data("error") == null){
			$(pId).on("mouseleave", function(e){
				//Verificar se componente que disparou esta fora deste chart. 
				//Artifício necessário pois o Firefox dispara este evento diversas vezes 
				var xE = $(e.originalEvent.toElement || e.relatedTarget).closest(".dbs_charts");
		        if (xE == "undefined"
		        || xE.length == 0
		        || xE[0] != this ){
		    		dbsfaces.charts.lostFocus($(pId));
					e.stopImmediatePropagation();
					return false;
		        }
		 	});
			
			$(pId + " > .-container > .-data > .-captions > .-content").on("mousedown", function(e){
				dbsfaces.charts.activateChart(xCharts, $(this));
			});
			$(pId + " > .-container > .-data > .-deltaList > .-container > .-content > .-th_action").on("mousedown touchstart", function(e){
				dbsfaces.chart.deltaListSelect(xChart, $(this));
				e.stopImmediatePropagation();
				return false;
			});
		}else{
			xCharts.data("container").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
//				.dbs_chart[type='bar'] > .dbs_chartValue > .-point,
//				.dbs_chart[type='pie'] > .dbs_chartValue > .-point{
//					transition: fill-opacity .5s, stroke-opacity .5s, stroke-dashoffset 2s ease 2s !important;
//				}
			});
		}
	}
};

dbsfaces.charts = {
	preInitialize: function(pCharts, pOnResize){
		//Faz o render do gráfico já com a altura e largura definido
		setTimeout(function(e){
			var xWidth = parseInt(pCharts[0].getBoundingClientRect().width);
			var xHeight = parseInt(pCharts[0].getBoundingClientRect().height);
			var xFontSize = parseInt(pCharts.css("font-size"));
			var xParams = null;
			if(pOnResize == null || pOnResize == false){
				xParams = [xWidth, xHeight, xFontSize];
			}
//			console.log(pCharts[0].id + "\t request" + "\t" + xWidth + "\t" + xHeight + "\t" + xFontSize);
			dbsfaces.ajax.request(pCharts[0].id, pCharts[0].id, pCharts[0].id, dbsfaces.ui.ajaxTriggerLoaded, dbsfaces.ui.showLoadingError(dbsfaces.util.jsid(pCharts[0].id)), xParams);
		},0);
	},

	initialize: function(pCharts, pDeltaList){
		dbsfaces.charts.pvInitializeData(pCharts, pDeltaList);
		dbsfaces.charts.pvInitializeGroupMember(pCharts);
		dbsfaces.charts.pvInitializeGuides(pCharts);
		if (pCharts.data("error") == null){
			dbsfaces.charts.pvInitializeChartActivate(pCharts);
			dbsfaces.charts.pvInitializeLayout(pCharts);
		}
	},

	pvInitializeData: function(pCharts, pDeltaList){
		//Salva chart's vinculados a este charts
		pCharts.data("children", pCharts.find(".dbs_chart"));
		pCharts.data("grid", pCharts.find(".dbs_charts-grid").first());
		pCharts.data("defs", pCharts.find("svg > defs").first());
		pCharts.data("fontsize", parseFloat(pCharts.css("font-size")));
		//Item selecionado temporariamente(Hover)
		pCharts.data("hover", null);
		pCharts.data("container", pCharts.children(".-container"));
		pCharts.data("caption", pCharts.data("container").children(".-caption"));
		pCharts.data("footer", pCharts.data("container").children(".-footer"));
		pCharts.data("data", pCharts.data("container").children(".-data"));
		pCharts.data("error", null);
		pCharts.data("showdelta", (typeof(pCharts.attr("showdelta")) != 'undefined'));
		pCharts.data("deltalistgroup", pCharts.data("data").children(".-deltalist"));
		pCharts.data("deltalist", null);
		if (pCharts.data("deltalistgroup").length > 0){
			pCharts.data("deltalist", pDeltaList);
		}
	},

	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvInitializeGuides: function(pCharts){
		if (pCharts.attr("type") != "line"){return;}

		dbsfaces.charts.pvCreateDefGuides(pCharts);

//		//Seta posição dos guides do delta
		var xChartsChildren = pCharts.data("children");
		xChartsChildren.each(function(){
			//Verifica se não há registro marca antes de desmarcar
			var xChart = $(this);
			var xMask = xChart.data("mask");
			if (xMask.length > 0){
				if(xMask.svgAttr("height") < 0){
					pCharts.data("error", "height of " + pCharts.css("height") + " is to low");
					return;
				};
				if(xMask.svgAttr("width") < 0){
					pCharts.data("error", "width of " + pCharts.css("width") + " is to low");
					return;
				};
			}
			if (pCharts.data("showdelta")){
				if (xChart.attr("type") == "line"){
					if (xChart.data("guide1") != null){
						dbsfaces.chart.setGuideIndex(xChart, 1);
						dbsfaces.chartValue.select(xChart.data("guide1").data("cv"), null);
					}
					if (xChart.data("guide2") != null){
						dbsfaces.chart.setGuideIndex(xChart, 2);
						dbsfaces.chartValue.select(xChart.data("guide2").data("cv"), null);
					}
					dbsfaces.chart.setGuideIndex(xChart, 0);
				}
			}
		});
		if (pCharts.data("error") == null){
			dbsfaces.charts.lostFocus(pCharts);
		}else{
			pCharts.data("data").text(pCharts.data("error"));
		}
	},
	
	//Cria guia padrão para indicar a posição no gráfico tipo line
	pvCreateDefGuides: function(pCharts){
		var xDefs = pCharts.data("defs");
		var xMarker = xDefs.children(".-point");
		var xElement;
		//Cria quia individual
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, null, null, null);
			xMarker.svgAttr("id", pCharts.get(0).id + "_point");
			xMarker.svgAttr("class", "-point");
			//Circulo externo
			xElement = dbsfaces.svg.ellipse(xMarker,  null, null, "1em", "1em", null, null, {"fill": "none", "stroke": "currentColor"});
			//Circulo interno
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, ".2em", ".2em", null, null, {"fill": "white", "stroke": "currentColor"});
		}
		//Cria cria de calculo do delta
		var xMarker = xDefs.children(".-guide");
		if (xMarker.length == 0){
			xMarker = dbsfaces.svg.g(xDefs, "-guide", null, {"id": pCharts.get(0).id + "_guide"});
			xElement = dbsfaces.svg.ellipse(xMarker, null, null, "20px", "20px", null, null, {"fill": "none"}); //"rx:inherit; ry:inherit;"
			xElement = dbsfaces.svg.rect(xMarker, "-3.5", "-8.5", "6px", "16px", "2", "2", null, null, {"fill": "white", "stroke": "currentColor"});
			xElement = dbsfaces.svg.rect(xMarker, "-2", "-5", "1px", "9px", null, null, null, null, {"fill": "currentColor"});
			xElement = dbsfaces.svg.rect(xMarker, "0", "-5", "1px", "9px", null, null, null, null, {"fill": "currentColor"});
		}
	},

	pvInitializeChartActivate: function(pCharts){
		var xFirstChart = pCharts.data("children").first();
		var xContainerData = pCharts.data("data");
		var xContent = xContainerData.find("> .-container > .-content");
		var xChartCaptions = xContainerData.children(".-captions");
		
		//Verifica se existe labels de cada gráfico definidas
		if (xChartCaptions.length != 0
		 && xChartCaptions.children().length > 0){
			//Ativa todos os labels
			xChartCaptions.children(".-content").svgAddClass("-activated");
			//Reposiciona valores para dar espaço os labels de título do gráfico
			//Ajusta altura conforme tamanho do fonte definido no css. Artíficio pois height do css não funciona em todos os browsers
//			var xChartCaptionsHeight = xChartCaptions.find("rect")[0].first().attr("height");
			var xChartCaptionsHeight = xChartCaptions[0].getBoundingClientRect().height;
			//Reposiciona gráfico
			dbsfaces.ui.cssTransform(xContent.children(".-value"), "translateY(" + xChartCaptionsHeight + "px)");
			//Ativa o primeiro gráfico que possuir delta
			dbsfaces.charts.activateChart(pCharts, xChartCaptions.children(".-content:first"));
		}else{
			//Ativa o primeiro gráfico
			dbsfaces.charts.pvActivateChartOne(xFirstChart, null, true);
			dbsfaces.charts.pvActivateDelta(pCharts, xFirstChart, true);
		}
	},

	//Charts que possuem groupid iquais
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
	
	pvInitializeGetTotal: function(pCharts){
		var xTotal = 0;
		//Conta o total de valores de todos os gráficos para posteriormente calcular a altura de cada linha de valor
		xChartsChildren.each(function(){
			var xChart = $(this);
			xTotal += xChart.data("children").length;
		});
		return xTotal;
	},
	//Ajusta posição das informações no gráfico pie
	pvInitializeLayout: function(pCharts){
		if (pCharts.attr("type") != "pie"){return;}
		var xChartsChildren = pCharts.data("children");
		var xParams = {total: 0,
					   count: 0,
					   color: [],
					   colorStop: []};
		//Conta o total de valores de todos os gráficos para posteriormente calcular a altura de cada linha de valor
		xChartsChildren.each(function(){
			var xChart = $(this);
			xParams.total += xChart.data("children").length;
		});
		//Ajusta posição do box e label de todos os valores dos gráficos e retorna todas as cores utilizadas para servir de background 
		xChartsChildren.each(function(){
			var xChart = $(this);
			xParams = dbsfaces.charts.pvInitializeLayoutInfo(pCharts,
															 xChart, 
															 xParams);
		});
	},
	
	pvInitializeLayoutInfo: function(pCharts, pChart, pParams){
		var xChartValues = pChart.data("children");
		xChartValues.each(function(){
			var xChartValue = $(this);
			var xChartValueValue = xChartValue.data("value");
			var xChartValueValuePerc = xChartValueValue.children(".-perc");
			if (xChartValueValuePerc==0){return;}
			var xChartValueValueLabel = xChartValueValue.children(".-label");
			var xChartValueValueValue = xChartValueValue.children(".-value");
			var xFontSize = parseFloat(xChartValueValue.css("font-size")); 
			var xWidth = xFontSize * 3.2; //Assume o máximo de 4 caracteres..xChartValueValuePerc.text().length;
//			var xHeight = xChartValueValuePerc[0].height.baseVal.value;
			var xHeight = xChartValueValuePerc[0].getBoundingClientRect().height;
			var xY = (xHeight / 2);
//			var xRadius = parseInt(xFontSize * .3); 
			var xChartValueInfo = xChartValue.data("infogroup");
			var xChartValueBox = xChartValueValue.children(".-box");
//			var xPath = "";
//			xPath += "M0,-" + xY;
//			xPath += "l0," + xHeight;
//			xPath += "a" + xRadius + "," + xRadius + " 0 0,0 " + xRadius + "," + xRadius;
//			xPath += "l" + xWidth + ",0";
//			xPath += "a" + xRadius + "," + xRadius + " 0 0,0 " + xRadius + ",-" + xRadius;
//			xPath += "l0,-" + xHeight;
//			xPath += "a" + xRadius + "," + xRadius + " 0 0,0 -" + xRadius + ",-" + xRadius;
//			xPath += "l-" + xWidth + ",0";
//			xPath += "a" + xRadius + "," + xRadius + " 0 0,0 -" + xRadius + "," + xRadius;
			
			xChartValueBox.attr("x", 0)
						  .attr("y", -xY)
						  .attr("width", xWidth)
						  .attr("height", xHeight);
		});
//		return pParams;
	},
	


	resize: function(pCharts){
		var xTimeoutId = "chartsTimeout" + pCharts[0].id;
		//Timeout para evitar chamadas repetidas desnecessárias. Alguns browsers chamam resize para widht e height.
		//Salva timeout no próprio body para depois poder cancela-lo.Timeout não é salvo do próprio componente pois seria pedido depois que componente fosse recriado no update do preInitialize.
		clearTimeout($("body").data(xTimeoutId));
		$("body").data(xTimeoutId, 
			setTimeout(function(e){
				var xWidthNew = parseInt(pCharts[0].getBoundingClientRect().width);
				var xHeightNew = parseInt(pCharts[0].getBoundingClientRect().height);
				var xWidth = parseInt(pCharts.css("width"));
				var xHeight = parseInt(pCharts.css("height"));
				
				var xWidth = parseInt(pCharts.attr("w"));
				var xHeight = parseInt(pCharts.attr("h"));
//				console.log(pCharts[0].id + "\t" + xWidth + "\t" + xWidthNew + "\t" + xHeight + "\t" + xHeightNew);
				//Não efetua atualizaçãso se tamanho do gráfico NÃO foi alterado		
				if (Math.abs(xWidth - xWidthNew) < 2
				 && Math.abs(xHeight - xHeightNew) < 2){
					return;
				}
				dbsfaces.charts.preInitialize(pCharts, true);
			},500)
		);
	},


	lostFocus: function(pCharts){
		//Verifica se não há registro marca antes de desmarcar
		var xHover = pCharts.data("hover");
		var xDoUnSelect = true;
		var xChartsChildren = pCharts.data("children");
//		xChartsChildren.each(function(){
//			//Verifica se não há registro marcado antes de desmarcar
//			var xChart = $(this);
////			if (typeof(xChart.attr("showdelta")) != "undefined"
////			 || xChart.data("selection").length > 0){
////				xDoUnSelect = false;
////				return;
////			}
//			if (xChart.data("selection").length > 0){
//				xDoUnSelect = false;
//				return;
//			}
//		});
//		if (xDoUnSelect){
			dbsfaces.chartValue.lostFocus(xHover);
//		}
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
						if (xChart.data("selection").length > 0){
							xDim = null; //Cancela desativação do DIM
							return;
						}
//						if ((xChart.data("selection").length > 1)
//						 || (xChart.data("selection").length > 0
//						  && xChart.data("selection")[0].id != pChartValue[0].id)){
//							xDim = null; //Cancela desativação do DIM
//							return;
//						}
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
//		if (pChart.attr("type") == "line"
//		 || pChart.attr("type") == "bar"){
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
//		}

	},
	
	activateChart: function(pCharts, pCaption){
		var xSiblings = pCaption.siblings();
		var xSiblingsActivated = xSiblings.filter(".-activated");
		var xChart= $(dbsfaces.util.jsid(pCaption.attr("chartid")));

		//Desmarca todos
		xSiblingsActivated.each(function(){
			var xSibilingCaption = $(this);
			var xSibilingChart = $(dbsfaces.util.jsid(xSibilingCaption.attr("chartid")));
			dbsfaces.charts.pvActivateDelta(pCharts, xSibilingChart, false);
			dbsfaces.charts.pvActivateChartOne(xSibilingChart, xSibilingCaption, false);
			//Desmarca qualquer valor marcado 
			dbsfaces.chartValue.unSelect(xSibilingChart.data("parent").data("hover"));
		});
		//Ativa delta
		dbsfaces.charts.pvActivateDelta(pCharts, xChart, true);
		//Marca selecionado
		dbsfaces.charts.pvActivateChartOne(xChart, pCaption, true);
		dbsfaces.ui.moveToFront(xChart);
	},

	pvActivateChartOne: function(pChart, pCaption, pActivate){
		if (pActivate){
			pChart.svgAddClass("-activated");
			if (pCaption != null){
				pCaption.svgAddClass("-activated").svgAddClass("-th_i");
			}
		}else{
			pChart.svgRemoveClass("-activated");
			if (pCaption != null){
				pCaption.svgRemoveClass("-activated").svgRemoveClass("-th_i");
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
	},
	
	deltaListSelect: function(pChart, pDeltaListButton){
		var xDeltaList = pCharts.data("deltalist");
		var xButtonId = pDeltaListButton.attr("id");
		//Procura qual o item da lista de delta foi selecionado
		for (var xI=0; xI < xDeltaList.length; xI++){
			if (xDeltaList[xI].Id == xButtonId){
				//Posiciona guia #1
				dbsfaces.chart.deltaListSelectSetGuide(pChart, xDeltaList[xI].StartLabel, 1);
				//Posiciona guia #2
				dbsfaces.chart.deltaListSelectSetGuide(pChart, xDeltaList[xI].EndLabel, 2);
			}
		}
	},
	
	deltaListSelectSetGuide: function(pChart, pSelectedLabel, pGuideIndex){
		var xChartValues = pChart.data("children");
		//Procura qual o chartvalue possui o label informado
		for (var xI=0; xI < xChartValues.length; xI++){
			var xChartValue = $(xChartValues[xI]);
			if (xChartValue.data("dl") == pSelectedLabel){
				//Seta número da guia
				dbsfaces.chart.setGuideIndex(pChart, pGuideIndex);
				//Seta guia
				dbsfaces.chart.pvSetGuide(pChart, xChartValue, true);
				break;
			}
		}
	}

		
};

