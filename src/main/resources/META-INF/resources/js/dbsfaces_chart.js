dbs_chart = function(pId) {
	var xChart = $(pId);
	
	dbsfaces.chart.initialize(xChart);

	if (xChart.attr("type") == "line"){
		//Não existe cálculo delta
		if (xChart.data("deltagroup") != null){
			$(pId + " > .-delta > .-guide").on("mousedown touchstart", function(e){
				xChart.data("guideIndex", Number($(this).attr("guide")));
//				$(this).svgAddClass("-a_dbs_chart_selected");
				e.stopImmediatePropagation();
				return false;
			});
			$(pId + " > .-delta > .-guide").on("mouseup touchend", function(e){
//				$(this).svgRemoveClass("-a_dbs_chart_selected");
				xChart.data("guideIndex", 0);
//				$(this).svgRemoveClass("-a_dbs_chart_selected");
				e.stopImmediatePropagation();
				return false;
			});
			$(pId + " > .-delta > .-guide").on("mousemove touchmove", function(e){
//				$(this).svgRemoveClass("-a_dbs_chart_selected");
				dbsfaces.chart.findPoint(e, xChart);
				e.stopImmediatePropagation();
				return false;
			});		
		}
		//Procura por chartvalue a partir da linha do gráfico
		$(pId + " > .-path > .-mask").on("mousemove touchmove touchstart", function(e){
//				console.log("XXchart\t" + e.originalEvent.type + "\t" + document.elementFromPoint(e.originalEvent.pageX, e.originalEvent.pageY).id);
			//Ignora findpoint se não houver guide ativo.
			dbsfaces.chart.findPoint(e, xChart);
			e.stopImmediatePropagation();
			return false;
		});
		
		$(pId + " > .-path > .-mask").on("mouseleave", function(e){
			$(this).data("guideIndex", 0);
			e.stopImmediatePropagation();
			return false;
		});

	} 
	
};



dbsfaces.chart = {
	
	initialize: function(pChart){
		dbsfaces.chart.pvInitializeData(pChart);

		var xCharts = pChart.data("parent");
		var xChartChildren = pChart.data("children");
		var xShowLabel = (typeof(xCharts.attr("showlabel")) != "undefined");
		var xShowDelta = (typeof(xChart.attr("showDelta")) != "undefined");
		var xDrawLine = (pChart.attr("type") == "line");
		if (pChart.attr("type") == "bar"
		 || xDrawLine){
			//Verifica sopreposição dos labels e cor da linhas
			dbsfaces.chart.pvInitializeLineAndLabels(xCharts, pChart, xChartChildren, xShowLabel, xDrawLine);
			//Cria guia principal
			if (xDrawLine && xShowDelta){
				dbsfaces.chart.pvInitializeDelta(xCharts, pChart);
			}
		}
		if (pChart.attr("type") == "pie"){
			dbsfaces.chart.pvSetFontSize(xCharts, pChart);
		}
	},

	//Definir a tamanho do fonte a partir do tamanho do gráfico
	pvSetFontSize: function(pCharts, pChart){
		var xSize = pCharts.attr("diameter") / 45;
		pChart.css("font-size", xSize + "px");	
	},

	pvInitializeData: function(pChart){
		var xCharts = pChart.closest(".dbs_charts");
		pChart.data("parent", xCharts);
		//Salva chartvalues vinculados a este chart
		var xChartChildren = pChart.children(".dbs_chartValue");
		pChart.data("children", xChartChildren);
		if (typeof(pChart.attr("showDelta")) != "undefined"){
			var xDeltaGroup = dbsfaces.util.getNotUndefined(pChart.children("g.-delta"), null);
			pChart.data("deltagroup", xDeltaGroup);
		}else{
			pChart.data("deltagroup", null);

		}
		pChart.data("pathgroup", dbsfaces.util.getNotUndefined(pChart.children(".-path"), null));
		if (pChart.data("pathgroup") == null){
			pChart.data("mask", null);
		}else{
			pChart.data("mask", dbsfaces.util.getNotUndefined(pChart.data("pathgroup").children(".-mask"), null));
		}
		//Reseta guia 
		pChart.data("guideIndex", 0);
		dbsfaces.chart.dataRefreshSelection(pChart);
	},
	
	//Verifica sopreposição dos labels 
	pvInitializeLineAndLabels: function(pCharts, pChart, pChartValues, pShowLabel, pDrawLine){
		var xChartValue;
		var xChartValueLabel;
		var xChartValuePoint;
		var xPos;
		var xPosAnt = 1;
		var xStringPath = "";
		var xStarColor;
		var xEndColor;
		//Loop nos valores por ordem do index para garantir o loop na ordem em que foram criados
		for (i=1; i <= pChartValues.length; i++){
			xChartValue = pChartValues.filter("[index='" + i + "']");
			//Verifica se há sobreposição
			if (pShowLabel){
				xChartValueLabel = xChartValue.children("g.-info").children(".-label");
				xPos = Number(xChartValueLabel.attr("x")) - (xChartValueLabel.get(0).getComputedTextLength() / 2);
				if (xPos < xPosAnt){
					xChartValueLabel.get(0).classList.add("-hide");
				}else{
					xChartValueLabel.get(0).classList.remove("-hide");
					xPosAnt = Number(xChartValueLabel.attr("x")) + (xChartValueLabel.get(0).getComputedTextLength() / 2) + 4;
				}
			}
			var xAntX;
			var xAntY;
			var xY;
			var xX;
			//Configura a linha conectando os pontos
			if (pDrawLine){
				if (i==1){
					xStringPath = "M";
					//Salva cor do primeiro ponto
					xStarColor = xChartValue.data("df");
				}else {
					xStringPath += "L";
				}
				if (i == pChartValues.length){
					//Salva cor do último ponto
					xStopColor = xChartValue.data("df");
				}
				xX = Number(xChartValue.data("dx"));
				xY = Number(xChartValue.data("dy"));
				//Artificio para corrigir o problema em que a linha com gradient não é exibida quando é uma reta perfeita.
				if (xX==xAntX){xX+=0.0001}
				if (xY==xAntY){xY+=0.0001}
				xStringPath += xX + "," + xY;

				xAntX = xChartValue.data("dx");
				xAntY = xChartValue.data("dy");
			}
		}
		//Cria a linha e cor que conecta os pontos
		if (xStringPath != ""){
			//Cria cor
			var xSvg = pCharts.find(".-container > .-data > svg.-container > defs");
			var xLG = dbsfaces.svg.linearGradient(xSvg, pChart.get(0).id + "_linestroke");
			dbsfaces.svg.stop(xLG, 0, xStarColor);
			dbsfaces.svg.stop(xLG, "100%", xStopColor);
			//Path do gráfico
			var xPath = dbsfaces.svg.path(pChart.data("pathgroup"), xStringPath, null, null, null);
			xPath.svgAttr("stroke", "url(#" + pChart.get(0).id + "_linestroke)");
			//Salva path no próprio componente para agilizar o findPoint
			pChart.data("path", xPath.get(0));
		}
	},
	
	//Inicializa delta 
	pvInitializeDelta: function(pCharts, pChart){
		var xChartChildren = pChart.data("children");
		var xCV1 = null;
		var xCV2 = null;
		if (xChartChildren.length > 0){
			xCV1 = $(xChartChildren.get(0));
			xCV2 = $(xChartChildren.get(xChartChildren.length - 1));
		}
		dbsfaces.chart.pvInitializeDeltaArea(pChart);
		dbsfaces.chart.pvInitializeGuide(pCharts, pChart, xCV1, 1);
		dbsfaces.chart.pvInitializeGuide(pCharts, pChart, xCV2, 2);

		pChart.data("guideIndex", 0);
		dbsfaces.ui.moveToFront(pChart.data("mask"));
	},
	
	//Cria guias 
	pvInitializeGuide: function(pCharts, pChart, pChartValue, pGuideIndex){
		var xChartDeltaGroup = pChart.data("deltagroup");
		var xGuide = xChartDeltaGroup.children(".-guide[guide='" + pGuideIndex + "']");
		if (xGuide.length == 0){
			xGuide = dbsfaces.svg.use(xChartDeltaGroup, pCharts.get(0).id + "_guide", "-guide", null);
			xGuide.svgAttr("guide", pGuideIndex);
//			xGuide.svgAttr("rx", pChart.attr("cs") + "px");
//			xGuide.svgAttr("ry", pChart.attr("cs") + "px");
//			xGuide.svgAttr("width", pChart.attr("cs") + "px");
//			xGuide.svgAttr("height", pChart.attr("cs") + "px");
//			xGuide.css("fill", "none");
//			xGuide.css("fill-opacity", "0");
			//Salva o guia do gráfico
			pChart.data("guide" + pGuideIndex, xGuide);
			//Inicializa chartValue do guia como nulo
			pChart.data("guide" + pGuideIndex).data("cv", pChartValue);
		}
		pChart.data("guideIndex", pGuideIndex);
		dbsfaces.chart.pvSetGuide(pChart, pChartValue, true);
		return xGuide;
	},

	//Cria area entre as guias para indicar seleção 
	pvInitializeDeltaArea: function(pChart){
		var xChartDeltaGroup = pChart.data("deltagroup");
		var xDeltaArea = xChartDeltaGroup.children(".-deltaarea");
		var xChartMask = pChart.data("mask");
		if (xDeltaArea.length == 0){
			xDeltaArea = dbsfaces.svg.rect(xChartDeltaGroup, 0, xChartMask.attr("y"), 0, xChartMask.attr("height"), null, null, "-deltaarea", null, null);
			xDeltaArea.svgAttr("fill", "url(#" + pChart.get(0).id + "_linestroke)");
			pChart.data("deltaarea", xDeltaArea);
		}
	},
	//Enconta o ponto(cv) a partir da posição do cursor
	findPoint: function(e, pChart){
		var xChartPathGroup = pChart.data("pathgroup");
		var xChartPath = pChart.data("path");
		var xChartMask = pChart.data("mask");
		var xDecimals = 1;
		var xPosition = xChartMask.offset();
		var xCurrentX = dbsfaces.math.round(e.originalEvent.pageX - xPosition.left, xDecimals);
		if (xCurrentX < 0){return;}
	    var xBeginning = xCurrentX;
        var xEnd =  dbsfaces.math.round(xChartPath.getTotalLength(), xDecimals);
        var xTargetLenght;
        var xTargetPos;
        var xTargetPosX;
        //Procura ponto da caminho(path) que o X é iqual a posição X selecionada
//        var xX = 0;
        while (Math.abs(xBeginning - xEnd) > 1) {
//        	xX++;
        	xTargetLenght = xBeginning +  dbsfaces.math.round((xEnd - xBeginning) / 2, xDecimals); //Meio do caminho
			xTargetPos = xChartPath.getPointAtLength(xTargetLenght); //Ponto do path 
			xTargetPosX = dbsfaces.math.round(xTargetPos.x, xDecimals);
			if (xTargetPosX < xCurrentX){
				xBeginning = xTargetLenght;
			}else if (xTargetPosX > xCurrentX){
				xEnd = xTargetLenght;
			}else{
				break; //Encontrou posição
			}
        }
//        $("#contador").text($("#contador").text() + "\n" + xX);
        //Seleciona o chartValue 
		if (typeof(xTargetPos) != "undefined"){
			//Procura qual dos chartsValues está mais próximo a posição do cursor
			var xChartChildren = pChart.data("children");
			var xIndex = xChartPath.getPathSegAtLength(xTargetLenght);
			var xTotalSegs = $(xChartPath).svgGetPathTotalSegs();
			var xXClosest = xCurrentX;
			var xChartValue = $(xChartChildren.get(xIndex));
			var xX = Number(xChartValue.data("dx"));
			var xY = Number(xChartValue.data("dy"));
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX){
				if (xIndex > 0){
					xXClosest = $(xChartChildren.get(xIndex - 1)).data("dx");
				}
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX){
				if (xIndex < xTotalSegs){
					xXClosest = $(xChartChildren.get(xIndex + 1)).data("dx");
				}
			}
			var xXMiddle = (Number(xXClosest) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValue = $(xChartChildren.get(xIndex - 1));
			}
//			console.log(xChartValue[0].id);
			dbsfaces.chartValue.select(xChartValue, null);
		}
	},

	dataRefreshSelection: function(pChart){
		pChart.data("selection", pChart.children(".-selected"));
	},
	

	select: function(pChartValue, pSelect){
//		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");
		//Reseta posição dos guias
		dbsfaces.chart.pvSetGuide(xChart, pChartValue, pSelect);

//		if (pSelect == null){
			var xHover = xCharts.data("hover");
			//Ignora se for para selecionar item já selecionado
			if (xHover != null){
				var xHoverChart = xHover.data("parent");
				if (xHoverChart[0].id == xChart[0].id){
					return;
				}
			}
//		}
		
		//Desmarca item selecionado anteriormente
		dbsfaces.charts.select(pChartValue, pSelect);
		//Desmarca item selecionado anteriormente
		dbsfaces.chart.pvSelectChart(xCharts, xChart, xHover, false);
		//Marca item selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, pSelect);
	},

	unSelect: function(pChartValue){
		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");

		//Reseta posição dos guias
		dbsfaces.chart.pvSetGuide(xChart, pChartValue, false);

		//Desmarca item selecionado anteriormente
		dbsfaces.charts.unSelect(pChartValue);
		//Apaga ponto selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, false);
	},
	
	pvSelectChart: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		var xDim = null;
		var xChartsChildren = pCharts.data("children");
		if (pSelect == null || pSelect){
			//Ativa o DIM em todos os chart que não estiverem já setados
			xChartsChildren.each(function(){
				xChart = $(this);
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
					xChart = $(this);
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
						xChart = $(this);
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
		
	
	pvSetGuide: function(pChart, pChartValue, pSelect){
		if (pChart.attr("type") != "line"){return;}
		if (pChart.data("guideIndex") == 0){return;}
		var xChartPathGuide =  $(pChart.data("guide" + pChart.data("guideIndex")));
		if (pSelect == null || pSelect){
			xChartPathGuide.show();
			var xX = pChartValue.data("dx");
			var xY = pChartValue.data("dy");
			var xColor = pChartValue.children(".-point").css("stroke");
			xChartPathGuide.css("color", xColor);
//			xChartPathGuide.css("stroke", xColor);
//						   .css("fill", xColor);
			//Reposiciona guia nas coordenadas do chartvalue
			var xCenterY = Number(pChart.data("mask").attr("height")) / 2;
//			console.log(Number(pChart.data("mask").attr("height")) / 2);
	        dbsfaces.ui.cssTransform(xChartPathGuide, "translate3d(" + xX + "px ," + xCenterY + "px,0)");
//			xChartPathGuide.css("-moz-transform-origin", xX + "px " + xY + "px 0");
//			dbsfaces.ui.cssAllBrowser(xChartPathGuide, "transform-origin", xX + "px " + xY + "px 0");
//	        if (pSelect){
		        xChartPathGuide.data("cv", pChartValue);
//	        }
		}else{
	        xChartPathGuide.data("cv", null);
			xChartPathGuide.hide();
		}
		
		setTimeout(function(){
			dbsfaces.chart.pvShowDelta(pChart);
		},0);
	},
	
	pvShowDelta: function(pChart){
		if (pChart.attr("type") != "line"){return;}
		var xChartDeltaGroup = pChart.data("deltagroup");
		if (xChartDeltaGroup.lenght == 0){return;}
		var xDeltaArea = pChart.data("deltaarea");
		var xCV1 = pChart.data("guide1").data("cv");
		var xCV2 = pChart.data("guide2").data("cv");
		dbsfaces.chart.pvShowDeltaArea(pChart, xDeltaArea, xCV1, xCV2);
	},
	
	pvShowDeltaArea: function(pChart, pDeltaArea, pCV1, pCV2){
//		var xChartMask = pChart.data("mask");
		var xCV1 = pCV1;
		var xCV2 = pCV2;
		if (xCV1.data("dx") > xCV2.data("dx")){
			xCV1 = pCV2; 
			xCV2 = pCV1;
		}
		var xX = xCV1.data("dx");
		var xWidth = (xCV2.data("dx") - xX);
		if (xWidth == 0){
			xWidth = 0.5;
			xX -= 0.5;
		}
		pDeltaArea.svgAttr("x", xX);
		pDeltaArea.svgAttr("width", xWidth);

//		xDeltaArea = dbsfaces.svg.rect(xChartDeltaGroup, null, null, 0, 0, null, null, "-deltaarea", null, null);
//		if (xDeltaArea.length == 0){
	}

//	
//	//Escoder todos os deltas
//	//Chamado pelo dbsfaces_charts
//	hideDelta: function(pCharts, pChart){
//		dbsfaces.chart.hideChartValueDelta(pChart);
//		
//		//Procura por todos os charts da mesma familia para esconder o delta, se existir.
//		var xMembers = pCharts.data("groupMembers");
//		if (xMembers != null) {
//			xMembers.each(function(){
//				xCharts = $(this);
//				var xChart = $(this).data("children").not(pChart);
//				xChart.each(function(){
//					dbsfaces.chart.hideChartValueDelta($(this));
//				});
//			});
//		}
//	},
//	
//	hideChartValueDelta: function(pChart){
//		//Esconde delta após 5 segundos caso não tenha sido selecionado o intervalo definitivo
//		if (pChart.attr("type") == "line"
//	     && typeof(pChart.attr("showdelta")) != 'undefined'){
//			dbsfaces.chartValue.hideDelta(pChart);
//		}
//	}

};

