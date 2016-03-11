dbs_charts = function(pId) {
	$(pId).on("mousedown", function(e){
		console.log("charts mousedown touchstart");
		var xCharts = $(pId).find(".dbs_chart");
		xCharts.each(function(){
			dbsfaces.chart.hideDelta($(this));
		});
	});
};

dbsfaces.charts = {
};

