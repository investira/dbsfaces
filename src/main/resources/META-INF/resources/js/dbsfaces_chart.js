dbs_chart = function(pId) {
	var xChart = $(pId);

	$(pId).mouseenter(function (e){
		dbsfaces.chart.hideDelta(xChart);
	});
};

dbsfaces.chart = {
	
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
	     && typeof(pChart.attr("showdelta")) != 'undefined'
		 && typeof(pChart.attr("dx2")) == 'undefined'){
			//Cancela setTimeout anterior caso exista
			window.clearTimeout(pChart.data("fto"));
			//Cria setTimeou para esconder delta em 5s e armazena o timeout do próprio componente
			//para poder ser recuperado posterioemente em caso de cancelamento
			pChart.data("fto", setTimeout(function(){
				dbsfaces.chartValue.hideDelta(pChart);
			},5000));
		}
	}

};

