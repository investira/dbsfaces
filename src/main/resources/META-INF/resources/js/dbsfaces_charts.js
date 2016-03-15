dbs_charts = function(pId) {
	var xCharts = $(pId).find("g.dbs_chart");
	//Salva chart's vinculados a este charts
	$(pId).data("chart", xCharts);
	$(pId).on("mousedown", function(e){
		xCharts.each(function(){
			dbsfaces.chart.hideDelta($(pId), $(this));
		});
	});
	$(pId).on("mouseleave mouseup", function(e){
		xCharts.each(function(){
			dbsfaces.chart.stopDeltaDrag($(this));
		});
	});

};

dbsfaces.charts = {
};

