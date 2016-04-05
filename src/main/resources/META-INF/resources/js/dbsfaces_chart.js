dbs_chart = function(pId) {
	var xChart = $(pId);
	
	dbsfaces.chart.initialize(xChart);
//	$(pId + "[type='line'] > g.-path > .-mask").on("mousedown", function(e){
////		$(pId + "[type='line'] > g.-path > .-mask").on("mousemove touchmove", function(e){
//		console.log(e.originalEvent.type);
//		dbsfaces.chart.findPoint(e, xChart);
//		e.preventDefault();
//		return false;
//	});
	$(pId + "[type='line'] > .-path > .-guide").on("mousedown touchstart", function(e){
//		console.log("chart guide\t" + e.originalEvent.type);
	});

//	$(pId + "[type='line'] > .-path > .-guide").on("mousemove touchstart", function(e){
//		console.log("chart guide\t" + e.originalEvent.type);
//		dbsfaces.chart.findPoint(e, xChart);
//	});
	//Procura por chartvalue a partir da linha do gráfico
	$(pId + "[type='line'] > .-path > .-mask").on("mousemove touchstart touchmove", function(e){
//		$(pId + "[type='line'] > g.-path > .-mask").on("mousemove touchmove", function(e){
//		console.log("chart\t" + e.originalEvent.type);
		dbsfaces.chart.findPoint(e, xChart);
		e.preventDefault();
		return false;
	});

	xChart.on("mouseleave", function(e){
//		dbsfaces.chartValue.unSelect(xChart.data("parent").data("selected"));
//		console.log("charts\t" + e.originalEvent.type);

//		$(this).data("chart").each(function(){
//			dbsfaces.chart.stopDeltaDrag($(this));
//		});
		dbsfaces.charts.unSelect($(this));
	});

//	$(pId + "[type='line'][showdelta] > g.-path").on("mousemove touchmove", function(e){
//		dbsfaces.chart.findPoint(e, xChart);
//		e.preventDefault();
//		return false;
//	});
	
//	$(pId).on("touchstart", function(e){
//	});

	
//	var events = $._data(xChart[0], "events");
//	console.log(events);
//	$(pId).on("*", function(e){
//		console.log(e);
//	});

//	$(pId).on("mousemove", function(e){
//		console.log("chart");
//		if (e.target == $(pId + " > g.-path")){
//			console.log("chart find");
//			dbsfaces.chart.findPoint(e, xChart);
//		}
//	});
////	.dbs_chart > .-delta > .-guide, .dbs_chart > .-delta > .-info > .-point
////	$(pId + " > .-delta > .-guide").on("mousedown touchstart", function(e){
//	$(pId + " > .-delta").on("mousedown touchstart", function(e){
//		var xGuide = $(e.target).parent();
////		console.log($(e.target).parent().attr("class"));
////		console.log(xGuide.attr("class"));
//		if (xGuide.attr("class").indexOf('-guide') != -1){
//			xChart.data("guide", xGuide.attr("guide"));
//			console.log(xGuide.attr("guide"));
//			dbsfaces.chart.startDeltaDrag(xChart);
////			e.preventDefault();
////			return false;
//		}
////		console.log($(e.target).parent().attr("class"));
//	});
//
//	$(pId).on("mouseup touchend", function(e){
////		console.log("mouseup touchend");
//		dbsfaces.chart.stopDeltaDrag(xChart);
//		e.preventDefault();
//		return false;
//	});
	
};

dbsfaces.chart = {
	startDeltaDrag: function(pChart){
//		dbsfaces.chartValue.removeDelta(pChart);
		pChart.data("drag", true);
	},

	stopDeltaDrag: function(pChart){
		pChart.data("drag", false);
	},

	isDeltaDragging: function(pChart){
		return pChart.data("drag");
	},
	
	initialize: function(pChart){
		//Reseta ponto para calculo do delta
//		dbsfaces.chartValue.removeDelta(pChart);

		dbsfaces.chart.pvInitializeData(pChart);

		var xCharts = pChart.data("parent");
		var xChartChildren = pChart.data("children");
		var xShowLabel = (typeof(xCharts.attr("showlabel")) != "undefined");
		var xDrawLine = (pChart.attr("type") == "line");
		if (pChart.attr("type") == "bar"
		 || pChart.attr("type") == "line"){
			//Verifica sopreposição dos labels e cor da linhas
			dbsfaces.chart.pvInitializeLineAndLabels(xCharts, pChart, xChartChildren, xShowLabel, xDrawLine);
			//Cria guia principal
			dbsfaces.chart.pvInitializeGuides(xCharts, pChart);
		}
	},

	pvInitializeData: function(pChart){
		var xCharts = pChart.closest(".dbs_charts");
		pChart.data("parent", xCharts);
		//Salva chartvalues vinculados a este chart
		var xChartChildren = pChart.children(".dbs_chartValue");
		pChart.data("children", xChartChildren);
		var xDeltaGroup = dbsfaces.util.getNotUndefined(pChart.children("g.-delta"), null);
		pChart.data("deltagroup", xDeltaGroup);
		
		pChart.data("pathgroup", dbsfaces.util.getNotUndefined(pChart.children(".-path"), null));
		if (pChart.data("pathgroup") == null){
			pChart.data("mask", null);
		}else{
			pChart.data("mask", dbsfaces.util.getNotUndefined(pChart.data("pathgroup").children(".-mask"), null));
		}
		//Define guia 1 como guia inicial 
		pChart.data("guide", 1);
		//Ponto #1
		pChart.data("cv1", null);
		//Ponto #2
		pChart.data("cv2", null);
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
	
	//Verifica sopreposição dos labels 
	pvInitializeGuides: function(pCharts, pChart){
		if (pChart.attr("type") == "line"){
			var xChartPathGroup = pChart.data("pathgroup");
			var xGuide1 = xChartPathGroup.children(".-guide[guide='1']");
			if (xGuide1.length == 0){
				xGuide1 = dbsfaces.svg.use(xChartPathGroup, pCharts.get(0).id + "_guide", "-guide", null);
				xGuide1.svgAttr("guide", 1);
				pChart.data("guide1", xGuide1);
			}
			var xGuide2 = xChartPathGroup.children(".-guide[guide='2']");
			if (xGuide2.length == 0){
				xGuide2 = dbsfaces.svg.use(xChartPathGroup, pCharts.get(0).id + "_guide", "-guide", null);
				xGuide2.svgAttr("guide", 2);
				pChart.data("guide2", xGuide2);
			}
		}
	},
	
	
	findPoint: function(e, pChart){
//		console.log("findpoint");
		var xChartPathGroup = pChart.data("pathgroup");
		var xChartPath = pChart.data("path");
//		var xChartPathGuide1 = xChartPathGroup.children(".-guide[guide='" + pChart.data("guide") + "']");
		var xChartPathGuide1 = pChart.data("guide" + pChart.data("guide"));
		var xChartMask = pChart.data("mask");
		var xDecimals = 1;
//        var xCurrentX = dbsfaces.math.round(e.originalEvent.pageX - xChartPath.getBoundingClientRect().left - e.originalEvent.offsetX, 1);
//        var xCurrentX = dbsfaces.math.round(e.originalEvent.offsetX, 1);
		var xPosition = $(e.currentTarget).offset();
		var xCurrentX = dbsfaces.math.round(e.originalEvent.pageX - xPosition.left, xDecimals);
		if (xCurrentX < 0){return;}
	    var xBeginning = xCurrentX;
        var xEnd =  dbsfaces.math.round(xChartPath.getTotalLength(), xDecimals);
        var xTargetLenght;
        var xTargetPos;
        var xTargetPosX;
        //Procura ponto da caminho(path) que o X é iqual a posição X selecionada
        while (Math.abs(xBeginning - xEnd) > 1) {
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
        //Posiciona o Guide 
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

	select: function(pChartValue, pSelect){
		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");
		//Marca item selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, pSelect);
		var xSelected = xCharts.data("selected");
		//Ignora se for para selecionar item já selecionado
		if (xSelected != null){
			var xSelectedChart = xSelected.data("parent");
			if (xSelectedChart[0].id == xChart[0].id){
				return;
			}
		}
		//Desmarca item selecionado anteriormente
		dbsfaces.charts.select(pChartValue, pSelect);
	},

	unSelect: function(pChartValue){
		if (pChartValue == null){return;}
		var xChart = pChartValue.data("parent");
		var xCharts = xChart.data("parent");

		//Apaga ponto selecionado
		dbsfaces.chart.pvSelectChart(xCharts, xChart, pChartValue, false);
		//Desmarca item selecionado anteriormente
		dbsfaces.charts.unSelect(pChartValue);
	},
	
	pvSelectChart: function(pCharts, pChart, pChartValue, pSelect){
		if (pChartValue == null){return;}
		var xChartsChildren = pCharts.data("children");
		var xSelect = pSelect;
		//Reseta posição dos guias
		dbsfaces.chart.pvSetGuide(pChart, pChartValue, pSelect);
	},
		
	
	pvSetGuide: function(pChart, pChartValue, pSelect){
		if (pChart.attr("type") != "line"){return;}
		var xChartPathGuide =  $(pChart.data("guide" + pChart.data("guide")));
		if (pSelect == null || pSelect){
			xChartPathGuide.show();
			var xX = pChartValue.data("dx");
			var xY = pChartValue.data("dy");
			xChartPathGuide.css("stroke", pChartValue.children(".-point").css("stroke"));
			//Reposiciona guia nas coordenadas do chartvalue
	        dbsfaces.ui.cssTransform(xChartPathGuide, "translate3d(" + xX + "px ," + xY + "px,0)");
			dbsfaces.ui.cssAllBrowser(xChartPathGuide, "transform-origin", xX + "px " + xY + "px 0");
		}else{
			xChartPathGuide.hide();
		}
	},
	

	//Escoder todos os deltas
	//Chamado pelo dbsfaces_charts
	hideDelta: function(pCharts, pChart){
		dbsfaces.chart.hideChartValueDelta(pChart);
		
		//Procura por todos os charts da mesma familia para esconder o delta, se existir.
		var xMembers = pCharts.data("groupMembers");
		if (xMembers != null) {
			xMembers.each(function(){
				xCharts = $(this);
				var xChart = $(this).data("children").not(pChart);
				xChart.each(function(){
					dbsfaces.chart.hideChartValueDelta($(this));
				});
			});
		}
	},
	
	hideChartValueDelta: function(pChart){
		//Esconde delta após 5 segundos caso não tenha sido selecionado o intervalo definitivo
		if (pChart.attr("type") == "line"
	     && typeof(pChart.attr("showdelta")) != 'undefined'){
			dbsfaces.chartValue.hideDelta(pChart);
		}
	}

};

