dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
	dbsfaces.chartValue.initialize(xChartValue);
};



dbsfaces.chartValue = {
	initialize: function(pChartValue){
		dbsfaces.chartValue.pvInitializeData(pChartValue);
	},

	pvInitializeData: function(pChartValue){
	}
	
};

