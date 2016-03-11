dbs_chart = function(pId) {
	var xChart = $(pId);
	
	setTimeout(function(){
		dbsfaces.chart.showLabel(xChart);
	},0);

	$(pId).on("mouseup touchend", function(e){
		e.preventDefault();
		dbsfaces.chart.setMouseDown(xChart, 0);
	});
	
};

dbsfaces.chart = {
	setMouseDown: function(pChart, pDown){
		pChart.data("m", pDown);
	},

	getMouseDown: function(pChart){
		return pChart.data("m");
	},

	showLabel: function(pChart){
		var xCharts = pChart.closest(".dbs_charts");
		if (typeof(xCharts.attr("showlabel")) != "undefined"){
			if (pChart.attr("type") == "bar"
			 || pChart.attr("type") == "line"){
				dbsfaces.chart.showLabelBarAndLine(pChart);
			}
		}
	},
	
	//Verifica sopreposição dos labels
	showLabelBarAndLine: function(pChart){
		var xChartValues = pChart.children(".dbs_chartValue");
		var xChartValue;
		var xChartValueLabel;
		var xPos;
		var xPosAnt = 1;
		for (i=1; i <= xChartValues.length; i++){
			xChartValue = xChartValues.filter("[index='" + i + "']");
			xChartValueLabel = xChartValue.children(".-info").children(".-label");
			xPos = Number(xChartValueLabel.attr("x")) - (xChartValueLabel.get(0).getComputedTextLength() / 2);
			if (xPos < xPosAnt){
				xChartValueLabel.get(0).classList.add("-hide");
			}else{
				xChartValueLabel.get(0).classList.remove("-hide");
				xPosAnt = Number(xChartValueLabel.attr("x")) + (xChartValueLabel.get(0).getComputedTextLength() / 2) + 4;
			}
		}
	},

	hideDelta: function(pChart){
		dbsfaces.chart.hideChartValueDelta(pChart);
		var xCharts = pChart.closest(".dbs_charts");
		//Procura por todos os charts da mesma familia para esconder o delta, se existir.
		var xGroupId = xCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xFamily = $("div.dbs_charts[groupid='" + xGroupId + "']");
			xFamily.each(function(){
				xCharts = $(this);
				var xChart = $(this).find(".dbs_chart").not(pChart);
				xChart.each(function(){
					dbsfaces.chart.hideChartValueDelta(xChart);
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

