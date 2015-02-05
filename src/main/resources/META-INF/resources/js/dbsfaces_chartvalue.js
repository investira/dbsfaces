dbs_chartValue = function(pId) {
	var xChart = $(pId).closest(".dbs_chart");
	if (xChart.attr("type") == "line"){
		dbsfaces.chartValue.drawLines(pId);
	}
	
	dbsfaces.chartValue.showLabel(pId);
	
	$(pId).mouseenter(function (e){
		var xContent = $(pId + " > .-extrainfo > span");
		xContent.show();
	});
	$(pId).mouseleave(function (e){
		var xContent = $(pId + " > .-extrainfo > span");
		xContent.hide();
	});
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
	},
	
	showLabel: function(pId){
		var xValue = $(pId);
		if (xValue.length == 0){
			return;
		}
		var xLabelAtual = xValue.children(".-label");
		if (xLabelAtual.length == 0){return;}

		//Procura valor anterior que contenha label sendo exibido
		var xLabelAnterior = xValue.prevAll(".dbs_chartValue").children(".-label").not("[class ~= '-hide']").first();
		var xXAtual = 0;
		var xLabelAtualWidth = xLabelAtual.width();
		var xXAnterior = 0;
		//Se não existir coluna anterior a ser exibida,
		if (xLabelAnterior.length == 0){
			xXAnterior = xLabelAtualWidth/2;
			xXAtual = Number(xLabelAtual.attr("x"));
		}else{
			xXAnterior = Number(xLabelAnterior.attr("x")) + xLabelAnterior.width() + 4;
			xXAtual = Number(xLabelAtual.attr("x"));
		}
		if (xXAnterior > xXAtual){
			xLabelAtual.get(0).classList.add("-hide");
		}else{
			xLabelAtual.get(0).classList.remove("-hide");
		}

		var xXLabel = $(pId + " > .-extrainfo > .-x");
		//Centraliza o texto do extrainfo
		xXLabel.offset({left: (xXAtual - (xLabelAtualWidth / 2) - 2) })


	}
};

