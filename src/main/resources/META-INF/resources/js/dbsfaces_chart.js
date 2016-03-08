dbs_chart = function(pId) {
	var xChart = $(pId);
	var xFadeTimeout;
	$(pId).mouseenter(function (e){
		//Esconde delta após 5 segundos caso não tenha sido selecionado o intervalo definitivo
		if (xChart.attr("type") == "line"
	     && typeof(xChart.attr("showdelta")) != 'undefined'
		 && typeof(xChart.attr("dx2")) == 'undefined'){
			window.clearTimeout(xFadeTimeout);
			xFadeTimeout = setTimeout(function(){
				if (typeof(xChart.attr("dx2")) == 'undefined'){
					xChart.children("g.-delta").fadeOut("slow", function(){
						dbsfaces.chartValue.removeDelta(xChart);
					});
				}
				return;
			},5000);
		}
	});
};

dbsfaces.chart = {
};

