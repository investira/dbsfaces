dbs_chart = function(pId) {
	var xChart = $(pId);
	var xCharts = xChart.closest(".dbs_charts");
	var xChartValues = xChart.children(".dbs_chartValue");
	xChart.data("chartvalue", xChartValues);
	
	dbsfaces.chart.initialize(xCharts, xChart, xChartValues);

	$(pId).on("mouseup touchend", function(e){
		e.preventDefault();
		dbsfaces.chart.setMouseDown(xChart, 0);
	});
	
//	$(pId).on("mousedown touchstart", function(e){
//		var xChartValue = $(e.target);
//		if (xChartValue.hasClass("dbs_chartValue")){
//			dbsfaces.chartValue.selectDelta(xChart, xChartValue, 1);
//			dbsfaces.chartValue.selectValue(xChartValue, true);
//			e.preventDefault();
//			return false;
//		}
//	});
//
//	//Desmaca item selecionado
//	$(pId).mouseleave(function (e){
//		var xChartValue = $(e.target);
//		if (xChartValue.hasClass("dbs_chartValue")){
//			dbsfaces.chartValue.selectValue(xChartValue, false);
//		}
//	});
//	
//	//Seleciona nova posição do delta e do item selecionado
//	$(pId).mousemove(function (e){
//		console.log(e.target);
//		var xChartValue = $(e.target);
//		if (xChartValue.hasClass("dbs_chartValue")){
////			dbsfaces.chartValue.selectDelta(xChart, xChartValue, null);
////			dbsfaces.chartValue.selectValue(xChartValue, true);
//		}
//	});
//
//	//Seleciona nova posição do delta e do item selecionado (Mobile)
//	$(pId).on("touchmove", function(e){
//		var xChartValue = $(e.target);
//		if (xChartValue.hasClass("dbs_chartValue")){
//			//Verifica se elemento é um dbs_chartValue
//			var xTarget = document.elementFromPoint(e.originalEvent.touches[0].clientX, e.originalEvent.touches[0].clientY);
//			if (typeof(xTarget) != "undefined"){
//				var xTargetChartValue = $(xTarget).parent();
//				if (typeof(xTargetChartValue) != "undefined"){
//					var xClass = xTargetChartValue.attr("class");
//					if (typeof(xClass) != "undefined" 
//					 && xClass.indexOf('dbs_chartValue') != -1){
//						var xTargetChart = xTargetChartValue.closest(".dbs_chart");
//						if (xTargetChart.get(0) == xChart.get(0)){
//							dbsfaces.chartValue.selectDelta(xChart, xTargetChartValue, null);
//							e.preventDefault();
//							return false;
//						}
//					}
//				}
//			}
//		}
//	});

	
};

dbsfaces.chart = {
	setMouseDown: function(pChart, pDown){
		pChart.data("m", pDown);
	},

	getMouseDown: function(pChart){
		return pChart.data("m");
	},

	initialize: function(pCharts, pChart, pChartValues){
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
		var xPath = "";
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
					xPath = "M";
					//Salva cor do primeiro ponto
					xStarColor = xChartValuePoint.css("stroke");
				}else {
					xPath += "L";
				}
				if (i == pChartValues.length){
					//Salva cor do último ponto
					xStopColor = xChartValuePoint.css("stroke");
				}
				xPath += xChartValuePoint.attr("cx") + "," + xChartValuePoint.attr("cy"); 
			}
		}
		//Cria a linha que conecta os pontos
		if (xPath != ""){
			var xSvg = pCharts.find(".-container > .-data > svg.-container > defs");
			var xLG = dbsfaces.svg.linearGradient(xSvg);
			xLG.attr("id", "linestroke");
			dbsfaces.svg.stop(xLG, 0, xStarColor);
			dbsfaces.svg.stop(xLG, "100%", xStopColor);
			dbsfaces.svg.path(pChart.children("g.-line"), xPath, null, null, null);
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

