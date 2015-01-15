dbs_chartValue = function(pId) {
	dbsfaces.chartValue.drawLines(pId);
};

dbsfaces.chartValue = {
	drawLines: function(pId){
		var xValue = $(pId);
		if (xValue.length == 0){
			return;
		}
		var xLineGroup = $(pId).siblings(".-line");
		var xValueAnterior = xValue.prevAll(".dbs_chartValue").first();
		
		dbsfaces.chartValue.drawLine(xLineGroup, xValueAnterior);
		dbsfaces.chartValue.drawLine(xLineGroup, xValue);
		
		return;
	},
	
	drawLine: function(pLineGroup, pValue){
		if (pValue.length == 0){return;}
		var xValuePosterior = pValue.nextAll(".dbs_chartValue").first().children("ellipse");
		if (xValuePosterior.length == 0){return;}
		var xValue = pValue.children("ellipse");
		var xId = pValue.attr("id");
		var xX1 = xValue.attr("cx");
		var xY1 = xValue.attr("cy");
		var xX2 = xValuePosterior.attr("cx");
		var xY2 = xValuePosterior.attr("cy");
		var xLine = pLineGroup.children("line[index='" + xId + "']");
		if (xLine.length == 0){
			xLine = $(document.createElementNS('http://www.w3.org/2000/svg','line'));
		}
		xLine.attr("index", xId);
		xLine.attr("x1", xX1);
		xLine.attr("y1", xY1);
		xLine.attr("x2", xX2);
		xLine.attr("y2", xY2);
		pLineGroup.append(xLine);
	}
};

