dbs_chart = function(pId) {
	var xChart = $(pId);
	var xCharts = xChart.closest(".dbs_charts");
	var xChartValues = xChart.children(".dbs_chartValue");
	//Salva chartvalues vinculados a este chart
	xChart.data("chartvalue", xChartValues);
	
	dbsfaces.chart.initialize(xCharts, xChart, xChartValues);

	$(pId).on("mouseup touchend", function(e){
//		console.log("mouseup touchend");
		dbsfaces.chart.stopDeltaDrag(xChart);
		e.preventDefault();
		return false;
	});
	
};

dbsfaces.chart = {
	startDeltaDrag: function(pChart){
		dbsfaces.chartValue.removeDelta(pChart);
		pChart.data("drag", true);
	},

	stopDeltaDrag: function(pChart){
		pChart.data("drag", false);
	},

	isDeltaDragging: function(pChart){
		return pChart.data("drag");
	},

	initialize: function(pCharts, pChart, pChartValues){
		//Reseta ponto para calculo do delta
		dbsfaces.chartValue.removeDelta(pChart);
		
		//Verifica sopreposição dos labels e cor da linhas
		var xShowLabel = (typeof(pCharts.attr("showlabel")) != "undefined");
		var xDrawLine = (pChart.attr("type") == "line");
		if (pChart.attr("type") == "bar"
		 || pChart.attr("type") == "line"){
			dbsfaces.chart.showLabelBarAndLine(pCharts, pChart, pChartValues, xShowLabel, xDrawLine);
		}
	},
	
	
	
	//Verifica sopreposição dos labels 
	showLabelBarAndLine: function(pCharts, pChart, pChartValues, pShowLabel, pDrawLine){
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
				xChartValueLabel = xChartValue.children(".-info").children(".-label");
				xPos = Number(xChartValueLabel.attr("x")) - (xChartValueLabel.get(0).getComputedTextLength() / 2);
				if (xPos < xPosAnt){
					xChartValueLabel.get(0).classList.add("-hide");
				}else{
					xChartValueLabel.get(0).classList.remove("-hide");
					xPosAnt = Number(xChartValueLabel.attr("x")) + (xChartValueLabel.get(0).getComputedTextLength() / 2) + 4;
				}
			}
			//Configura a linha conectando os pontos
			if (pDrawLine){
				xChartValuePoint = xChartValue.children(".-point");
				if (i==1){
					xStringPath = "M";
					//Salva cor do primeiro ponto
					xStarColor = xChartValuePoint.css("stroke");
				}else {
					xStringPath += "L";
				}
				if (i == pChartValues.length){
					//Salva cor do último ponto
					xStopColor = xChartValuePoint.css("stroke");
				}
				xStringPath += xChartValuePoint.attr("cx") + "," + xChartValuePoint.attr("cy"); 
			}
		}
		//Cria a linha que conecta os pontos
		if (xStringPath != ""){
			var xSvg = pCharts.find(".-container > .-data > svg.-container > defs");
			var xLG = dbsfaces.svg.linearGradient(xSvg);
			xLG.attr("id", "linestroke");
			dbsfaces.svg.stop(xLG, 0, xStarColor);
			dbsfaces.svg.stop(xLG, "100%", xStopColor);
			var xPath = dbsfaces.svg.path(pChart.children("g.-line"), xStringPath, null, null, null);
			xPath.attr("stroke", "url(#linestroke)");
		}
	},

	//Escoder todos os deltas
	//Chamado pelo dbsfaces_charts
	hideDelta: function(pCharts, pChart){
		dbsfaces.chart.hideChartValueDelta(pChart);
		
		//Procura por todos os charts da mesma familia para esconder o delta, se existir.
		var xGroupId = pCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xFamily = $("div.dbs_charts[groupid='" + xGroupId + "']");
			xFamily.each(function(){
				xCharts = $(this);
				var xChart = $(this).data("chart").not(pChart);
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

