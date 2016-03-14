dbs_charts = function(pId) {
	var xCharts = $(pId).find("g.dbs_chart");
	$(pId).data("chart", xCharts);
	$(pId).on("mousedown", function(e){
		xCharts.each(function(){
			dbsfaces.chart.hideDelta($(pId), $(this));
		});
	});
};

dbsfaces.charts = {
};

