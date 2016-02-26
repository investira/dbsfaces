dbs_chartValue = function(pId) {
	var xChart = $(pId).closest(".dbs_chart");
	if (xChart.attr("type") == "line"){
		dbsfaces.chartValue.drawLines(pId);
	}
	
	setTimeout(function(){
		dbsfaces.chartValue.showLabel(pId);
	},0);
	
	$(pId).mouseenter(function (e){
		//Coloca item como primeiro elemento para aparecer acima dos demais
		this.parentElement.appendChild(this);
		dbsfaces.tooltip.showTooltip(pId + '_tooltip');
		$(pId + '_tooltip').addClass("-selected");
		if ($(pId + " > .-info > .-label").length > 0){
			$(pId + " > .-info > .-label").get(0).classList.add("-selected");
		}
		if ($(pId + " > .-info > .-value").length > 0){
			$(pId + " > .-info > .-value").get(0).classList.add("-selected");
		}
		dbsfaces.chartValue.selectValue(pId, true);
	});
	$(pId).mouseleave(function (e){
		dbsfaces.tooltip.hideTooltip(pId + '_tooltip');
		$(pId + '_tooltip').removeClass("-selected");
		if ($(pId + " > .-info > .-label").length > 0){
			$(pId + " > .-info > .-label").get(0).classList.remove("-selected");
		}
		if ($(pId + " > .-info > .-value").length > 0){
			$(pId + " > .-info > .-value").get(0).classList.remove("-selected");
		}
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
		var xValuePosterior = pValue.nextAll(".dbs_chartValue").first().children(".-point");
		if (xValuePosterior.length == 0){return;}
		var xValue = pValue.children(".-point");
		var xId = pValue.attr("id");
		var xX1 = xValue.attr("cx");
		var xY1 = xValue.attr("cy");
		var xX2 = xValuePosterior.attr("cx");
		var xY2 = xValuePosterior.attr("cy");
		var xLine = pLineGroup.children("line[index='" + xId + "']");
		if (xLine.length == 0){
			xLine = $(document.createElementNS('http://www.w3.org/2000/svg','line'));
		}
		xLine.attr("index", xId)
			 .attr("x1", xX1)
			 .attr("y1", xY1)
			 .attr("x2", xX2)
			 .attr("y2", xY2);
		pLineGroup.append(xLine);
	},
	
	showLabel: function(pId){
		var xChart = $(pId).closest(".dbs_chart");
		if (xChart.attr("type") == "bar"
		 || xChart.attr("type") == "line"){
			dbsfaces.chartValue.showLabelBarAndLine(pId);
		}
	},
	
	//Verifica sopreposição dos labels
	showLabelBarAndLine: function(pId){
		var xValue = $(pId);
		if (xValue.length == 0){return;}

		var xLabelAtual = xValue.children(".-info").children(".-label");
		if (xLabelAtual.length == 0){return;}

		//Procura valor anterior que contenha label sendo exibido
		var xLabelAnterior = xValue.prevAll(".dbs_chartValue").children(".-info").children(".-label").not("[class ~= '-hide']").first();
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
	},

	selectValue: function(pId, pSelect){
		var xValue = $(pId);
		if (xValue.length == 0){return;}
		
		var xCharts = xValue.closest(".dbs_charts");
		var xLabelAtual = xValue.children(".-info");

		var xGridLabels = xCharts.find(".-container > .-data > .-container > .-content > .-value > .-grid > .-label");
//		var xDataLabels = xValue.siblings(".dbs_chartValue").children(".-info").children(".-label").not("[class ~= '-hide']").not(xLabelAtual);
		var xDataLabels = xCharts.find(".dbs_chartValue").children(".-info").not(xLabelAtual);
		if (pSelect){
			xDataLabels.hide();
			xGridLabels.hide();
			xValue.get(0).classList.add("-selected");
		}else{
			xDataLabels.show();
			xGridLabels.show();
			xValue.get(0).classList.remove("-selected");
		}
	}

};

