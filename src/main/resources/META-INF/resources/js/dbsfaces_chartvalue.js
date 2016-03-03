dbs_chartValue = function(pId) {
	var xChart = $(pId).closest(".dbs_chart");
	if (xChart.attr("type") == "line"){
		dbsfaces.chartValue.drawLines(pId);
	}
	
	setTimeout(function(){
		dbsfaces.chartValue.showLabel(pId);
	},0);
	
	$(pId).click(function(e){
		dbsfaces.chartValue.selectDelta(pId);
	});
	
	$(pId).mouseenter(function (e){
		//Coloca item como primeiro elemento para aparecer acima dos demais
		dbsfaces.chartValue.moveToFront(this);

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
		var xChartValue = $(pId);
		if (xChartValue.length == 0){
			return;
		}
		var xChart = xChartValue.closest(".dbs_chart");
		var xLineGroup = xChart.children("g.-line");
		var xChartValueAnterior = xChartValue.prevAll(".dbs_chartValue").first();
		
		dbsfaces.chartValue.drawLine(xLineGroup, xChartValueAnterior);
		dbsfaces.chartValue.drawLine(xLineGroup, xChartValue);
		
		return;
	},
	
	drawLine: function(pLineGroup, pChartValue){
		if (pChartValue.length == 0){return;}
		var xChartValuePosterior = pChartValue.nextAll(".dbs_chartValue").first().children(".-point");
		if (xChartValuePosterior.length == 0){return;}
		var xChartValuePoint = pChartValue.children(".-point");
		var xId = pChartValue.attr("id");
		var xStroke = xChartValuePoint.attr("fill");
		var xX1 = xChartValuePoint.attr("cx");
		var xY1 = xChartValuePoint.attr("cy");
		var xX2 = xChartValuePosterior.attr("cx");
		var xY2 = xChartValuePosterior.attr("cy");
		var xLine = pLineGroup.children("line[index='" + xId + "']");
		//Altera linha caso já exista
		if (xLine.length != 0){
			xLine.attr("x1", xX1)
				 .attr("y1", xY1)
				 .attr("x2", xX2)
				 .attr("y2", xY2)
				 .attr("stroke", xStroke);
		//Cria linha
		}else{
			var xLine = dbsfaces.svg.line(pLineGroup, xX1, xY1, xX2, xY2, null, "stroke:" + xStroke);
			xLine.attr("index", xId);
		}
	},
	
	
	showLabel: function(pId){
		var xChart = $(pId).closest(".dbs_chart");
		if (xChart.attr("type") == "bar"
		 || xChart.attr("type") == "line"){
			dbsfaces.chartValue.showLabelBarAndLine(pId);
		}
	},
	
	selectDelta: function(pId){
		var xChartValue = $(pId);
		var xChart = xChartValue.closest(".dbs_chart");
		if (xChart.attr("type") != "line"){
			return;
		}
		var xChartValuePoint = xChartValue.children(".-point");
		//Desmaca pontos
		if (typeof(xChart.attr("dx1")) != 'undefined'
		 && typeof(xChart.attr("dx2")) != 'undefined'){
			xChart.get(0).removeAttribute("dx1");
			xChart.get(0).removeAttribute("dy1");
			xChart.get(0).removeAttribute("dx2");
			xChart.get(0).removeAttribute("dy2");
			var xChartValues = xChart.find(".dbs_chartValue.-selectedDelta");
			xChartValues.each(function(){
				this.classList.remove("-selectedDelta");
			});
		}
		//Marca ponto origem
		if (typeof(xChart.attr("dx1")) == 'undefined'){
			xChart.get(0).setAttribute("dx1", xChartValuePoint.attr("cx"));
			xChart.get(0).setAttribute("dy1", xChartValuePoint.attr("cy"));
			xChartValue.get(0).classList.add("-selectedDelta");
		//Marca ponto destino
		}else if (typeof(xChart.attr("dx2")) == 'undefined'){
			xChart.get(0).setAttribute("dx2", xChartValuePoint.attr("cx"));
			xChart.get(0).setAttribute("dy2", xChartValuePoint.attr("cy"));
			xChartValue.get(0).classList.add("-selectedDelta");
		}
		dbsfaces.chartValue.showDelta(xChart, xChartValue, -1, -1);
	},
	
	
	showDelta: function(pChart, pChartValue, pX, pY){
		if (pChart.length == 0){return;}
		var xDeltaGroup = pChart.children("g.-delta");
		var xX1 = pChart.attr("dx1");
		var xY1 = pChart.attr("dy1");
		if (typeof(xX1) == 'undefined'){
			dbsfaces.chartValue.removeDelta(xDeltaGroup);
			return;
		}
		var xX2 = pChart.attr("dx2");
		var xY2 = pChart.attr("dy2");
		if (typeof(xX2) == 'undefined'
		 && pX == -1){
			dbsfaces.chartValue.removeDelta(xDeltaGroup);
			return;
		}
		if (typeof(xX2) == 'undefined'){
			xX2 = pX;
			xY2 = pY;
		}
		var xChartValuePoint = pChartValue.find(".-point");
		var xStroke = xChartValuePoint.attr("fill");
		//Line
		var xLine = xDeltaGroup.children("line.-deltaline");
		if (xLine.length > 0){
			xLine.attr("x1", xX1)
				 .attr("y1", xY1)
				 .attr("x2", xX2)
				 .attr("y2", xY2)
				 .attr("stroke", xStroke);
		}else{
			dbsfaces.svg.line(xDeltaGroup, xX1, xY1, xX2, xY2, "-deltaline", "stroke:" + xStroke);
		}
		//Line
//		var xLine = xDeltaGroup.children("rect[class='-deltaline']");
//		if (xLine.length > 0){
//			xLine.attr("x1", xX1)
//				 .attr("y1", xY1)
//				 .attr("x2", xX2)
//				 .attr("y2", xY2)
//				 .attr("stroke", xStroke);
//		}else{
//			dbsfaces.svg.line(xDeltaGroup, xX1, xY1, xX2, xY2, "-deltaline", "stroke:" + xStroke);
//		}
	},
	
	removeDelta: function(pDeltaGroup){
		if (pDeltaGroup.length == 0){return;}
		pDeltaGroup.empty();
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

	//Coloca item como primeiro elemento para aparecer acima dos demais
	moveToFront: function(pItem){
		var xChart = $(pItem).closest(".dbs_chart");
		if (xChart.attr("type") == "bar"
		 || xChart.attr("type") == "line"){
//			pItem.parentElement.appendChild(pItem);
		}
	},
	
	selectValue: function(pId, pSelect){
		var xChartValue = $(pId);
		if (xChartValue.length == 0){return;}
		
		var xChart = xChartValue.closest(".dbs_chart");
		var xCharts = xChart.closest(".dbs_charts");
		var xChartValuePoint = xChartValue.children(".-point");
		var xLabelAtual = xChartValue.children(".-info");

		var xGridLabels = xCharts.find(".-container > .-data > .-container > .-content > .-value > .-grid > .-label");
//		var xDataLabels = xValue.siblings(".dbs_chartValue").children(".-info").children(".-label").not("[class ~= '-hide']").not(xLabelAtual);
		var xChartValues = xCharts.find(".dbs_chartValue").not(xChartValue);
		var xDataLabels = xChartValues.children(".-info");
		if (pSelect){
			xDataLabels.hide();
			xGridLabels.hide();
			xChartValues.each(function(){
				this.classList.add("-dim");
			});
			xChartValue.get(0).classList.add("-selected");
			
		}else{
			xDataLabels.show();
			xGridLabels.show();
			xChartValues.each(function(){
				this.classList.remove("-dim");
			});
			xChartValue.get(0).classList.remove("-selected");
		}
		if (xChart.attr("type") == "line"){
			if (typeof(xChart.attr("dx1")) != 'undefined'){
				dbsfaces.chartValue.showDelta(xChart, xChartValue, xChartValuePoint.attr("cx"), xChartValuePoint.attr("cy"));
			}
		}
	}

};

