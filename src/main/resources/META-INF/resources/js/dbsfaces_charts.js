dbs_charts = function(pId) {
	var xCharts = $(pId).find(".dbs_chart");
	$(pId).on("mousedown", function(e){
		xCharts.each(function(){
			dbsfaces.chart.hideDelta($(this));
		});
	});
};

dbsfaces.charts = {
};

