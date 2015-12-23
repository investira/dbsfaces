dbs_chartValue = function(pId) {
	var xChart = $(pId).closest(".dbs_chart");
	if (xChart.attr("type") == "line"){
		dbsfaces.chartValue.drawLines(pId);
	}
	
	setTimeout(function(){
		dbsfaces.chartValue.showLabel(pId);
	},0);
	
	$(pId).mouseenter(function (e){
		 $(pId + " > .-extrainfo > span").addClass("-selected");
		 $(pId + " > .-xlabel").get(0).classList.add("-selected");
		 $(pId + " > .-ylabel").get(0).classList.add("-selected");
		 dbsfaces.chartValue.selectValue(pId, true);
	});
	$(pId).mouseleave(function (e){
		$(pId + " > .-extrainfo > span").removeClass("-selected");
		$(pId + " > .-xlabel").get(0).classList.remove("-selected");
		$(pId + " > .-ylabel").get(0).classList.remove("-selected");
		dbsfaces.chartValue.selectValue(pId, false);
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
		if (xValue.length == 0){return;}

		var xLabelAtual = xValue.children(".-xlabel");
		if (xLabelAtual.length == 0){return;}

		//Procura valor anterior que contenha label sendo exibido
		var xLabelAnterior = xValue.prevAll(".dbs_chartValue").children(".-xlabel").not("[class ~= '-hide']").first();
		var xXAtual = 0;
		var xXAnterior = 0;
		//Calcula posição final da label anterior
		if (xLabelAnterior.length != 0){
			xXAnterior = Number(xLabelAnterior.attr("x")) + (xLabelAnterior.get(0).getComputedTextLength() / 2) + 4;
		}
		//Calcula posição inicial da label atual
		xXAtual = Number(xLabelAtual.attr("x")) - (xLabelAtual.get(0).getComputedTextLength() / 2);
		//Exclui sobreposição de texto
		if (xXAnterior > xXAtual){
			xLabelAtual.get(0).classList.add("-hide");
		}else{
			xLabelAtual.get(0).classList.remove("-hide");
		}

		//Centraliza o texto do extrainfo
		var xXLabel = $(pId + " > .-extrainfo > .-x");
		xXLabel.offset({left: xXAtual})
	},

	selectValue: function(pId, pSelect){
		var xValue = $(pId);
		if (xValue.length == 0){return;}
		
		var xXLabelAtual = xValue.children(".-xlabel");

		var xYLabels = xValue.closest(".dbs_charts").find(".-container > .-data > .-container > .-content > .-value > .-grid > .-label");
		var xXLabels = xValue.siblings(".dbs_chartValue").children(".-xlabel").not("[class ~= '-hide']").not(xXLabelAtual);
		if (pSelect){
			xXLabels.hide();
			xYLabels.hide();
		}else{
			xXLabels.show();
			xYLabels.show();
		}
	}

};

