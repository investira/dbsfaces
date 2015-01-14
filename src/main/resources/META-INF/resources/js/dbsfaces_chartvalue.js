dbs_chartValue = function(pId) {
	dbsfaces.chartValue.drawLine(pId);
};

dbsfaces.chartValue = {
	drawLine: function(pId){
		var xChart = $(pId).closest(".dbs_chart");
		var xSVG = xChart.find("svg:first");
		var xValues = xChart.find("svg:first > .-content > .-value > .dbs_chartValue > ellipse");
		if (xValues.length > 0){
			//Exclui linhas
			$("svg:first > .-content > .-value > .-line").remove();
			//Inclui
			$("svg:first > .-content > .-value").append("<g class='-line'> </g>");
			xLine = $("svg:first > .-content > .-value > .-line");
		}
		
		return;
	}	
};

