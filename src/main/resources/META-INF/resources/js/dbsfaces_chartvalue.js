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
		//Não permite seleção para gráficos diferente de line ou 
		//quando valor do ponto selecionado for zero
		if (xChart.attr("type") != "line"
		 || xChartValue.attr("value") == "0.0"){
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
			xValue = xChartValue.children(".-info").children(".-value").text();
			if (xValue != 0){
				xChart.get(0).setAttribute("dx1", xChartValuePoint.attr("cx"));
				xChart.get(0).setAttribute("dy1", xChartValuePoint.attr("cy"));
				xChart.get(0).setAttribute("dv1", xChartValue.attr("value"));
				xChart.get(0).setAttribute("dl1", xChartValue.children(".-info").children(".-label").text());
				xChart.get(0).setAttribute("dd1", xChartValue.children(".-info").children(".-value").text());
				xChartValue.get(0).classList.add("-selectedDelta");
			}
		//Marca ponto destino
		}else if (typeof(xChart.attr("dx2")) == 'undefined'){
//			xValue = xChartValue.children(".-info").children(".-value").text();
			xChart.get(0).setAttribute("dx2", xChartValuePoint.attr("cx"));
			xChart.get(0).setAttribute("dy2", xChartValuePoint.attr("cy"));
		}
		dbsfaces.chartValue.showDelta(xChart, xChartValue, -1, -1);
	},
	
	
	showDelta: function(pChart, pChartValue, pX, pY){
		if (pChart.length == 0){return;}
		var xDeltaGroup = pChart.children("g.-delta");
		//Sai se ponto incial não foi selecionado
		var xX1 = pChart.attr("dx1");
		var xY1 = pChart.attr("dy1");
		if (typeof(xX1) == 'undefined'){
			dbsfaces.chartValue.removeDelta(xDeltaGroup);
			return;
		}
		//Sai se pointo final não foi selecionado nem ponto temporário informado
		var xX2 = pChart.attr("dx2");
		var xY2 = pChart.attr("dy2");
		if (typeof(xX2) == 'undefined'
		 && pX == -1){
			dbsfaces.chartValue.removeDelta(xDeltaGroup);
			return;
		}
		//Sai se ponto final foi selecionado
		if (typeof(xX2) != 'undefined'){
			return;
		}
		if (pChartValue.attr("value") == "0.0" ||
		    Math.sign(pChartValue.attr("value")) != Math.sign(pChart.attr("dv1"))){
			return;
		}

		//Define ponto final como o ponto temporário informado
		xX2 = pX;
		xY2 = pY;
		var xChartValuePoint = pChartValue.find(".-point");
		var xStroke = "stroke:" + xChartValuePoint.attr("fill") + ";";
		//Line
		var xLine = xDeltaGroup.children("line.-deltaline");
		//Altera linha se já existir
		if (xLine.length > 0){
			xLine.attr("x1", xX1)
				 .attr("y1", xY1)
				 .attr("x2", xX2)
				 .attr("y2", xY2)
				 .attr("style", xStroke);
		//Cria linha
		}else{
			dbsfaces.svg.line(xDeltaGroup, xX1, xY1, xX2, xY2, "-deltaline", xStroke);
		}
		
		//Group info----------------------
		var xDeltaInfoGroup = xDeltaGroup.children(".-info")

		var xStyle = "transform: translate3d(" + xX2 + "px ," + xY2 + "px,0);"
		//Cria Group info
		if (xDeltaInfoGroup.length == 0){
			xDeltaInfoGroup = dbsfaces.svg.g(xDeltaGroup, null, null, "4em","4em", "-info", xStyle);
		}else{
			xDeltaInfoGroup.attr("style", xStyle);
		}
		
		//Box=======================
		var xWidth = 0;
		var xBox = xDeltaInfoGroup.children("rect.-box");
		if (xBox.length == 0){
			xBox = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-4em", "0em", "4em", 3, 3, "-box", xStroke, null);
		}
		var xBox2 = xDeltaInfoGroup.children("rect.-box2");
		if (xBox2.length == 0){
			xBox2 = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-4em", "0em", "1.5em", 3, 3, "-box2", null, xChartValuePoint.attr("fill"));
		}
		var xPoint = xDeltaInfoGroup.children("ellipse");
		if (xPoint.length == 0){
			xPoint = dbsfaces.svg.ellipse(xDeltaInfoGroup, "0", "0", "0.3em", "0.3em", null, null, xChartValuePoint.attr("fill"));
		}

		//Info Delta=======================
		var xDeltaValue = xDeltaInfoGroup.children("text.-value");
		if (xDeltaValue.length == 0){
			xDeltaValue = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-1.8em", "", "-value", null, null);
		}else{
			xDeltaValue.text(dbsfaces.chartValue.calcDelta(pChart, pChartValue));
		}
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xDeltaValue, null);
		
		//Info label1=======================
		var xDeltaLabel1 = xDeltaInfoGroup.children("text.-label1");
		if (xDeltaLabel1.length == 0){
			xDeltaLabel1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-1.5em", "", "-label1", null, null);
		}
		xDeltaLabel1.text("x:");
		xLabel = pChart.attr("dl1");
		if (xLabel != ""){
			xDeltaLabel1.text(xDeltaLabel1.text() + xLabel + ":");
		}

		var xDeltaValue1 = xDeltaInfoGroup.children("text.-value1");
		if (xDeltaValue1.length == 0){
			xDeltaValue1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-1.5em", "", "-value1", null, null);
		}
		xDeltaValue1.text(pChart.attr("dd1"));
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xDeltaLabel1, xDeltaValue1);

		//Info label2=======================
		var xDeltaLabel2 = xDeltaInfoGroup.children("text.-label2");
		if (xDeltaLabel2.length == 0){
			xDeltaLabel2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-.5em", "", "-label2", null, null);
		}
		xDeltaLabel2.text("y:");
		xLabel = pChartValue.children(".-info").children(".-label").text();
		if (xLabel != ""){
			xDeltaLabel2.text(xDeltaLabel2.text() + xLabel + ":");
		}

		var xDeltaValue2 = xDeltaInfoGroup.children("text.-value2");
		if (xDeltaValue2.length == 0){
			xDeltaValue2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-.5em", "", "-value2", null, null);
		}
		xDeltaValue2.text(pChartValue.children(".-info").children(".-value").text());
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xDeltaLabel2, xDeltaValue2);


		//Box=======================
//		xPadding = xWidth * .20;
//		xWidthLeft = xWidth/2;

		xWidthLeft = xWidth / 2 + 2;
		xDeltaLabel1.attr("x", -xWidthLeft);
		xDeltaLabel2.attr("x", -xWidthLeft);
		xDeltaValue1.attr("x", xWidthLeft);
		xDeltaValue2.attr("x", xWidthLeft);

		xWidth *= 1.15;
		xBox.attr("x",xWidth/-2)
			.attr("width", xWidth);
		xBox2.attr("x",xWidth/-2)
			 .attr("width", xWidth);
	},
	
	getMaxWidth: function(pMax, pLabel, pValue){
		var xWidth = 0;
		if (pLabel != null){
			xWidth += pLabel.get(0).getComputedTextLength();
		}
		if (pValue != null){
			xWidth += pValue.get(0).getComputedTextLength();
		}
		if (xWidth > pMax){
			return xWidth;
		}
		return pMax;
	},
	
	//Coloca item como primeiro elemento para aparecer acima dos demais
	calcDelta: function(pChart, pChartValue){
		var xDV1 = Number(pChart.attr("dv1"));
		var xDV2 = Number(pChartValue.attr("value"));
		if (xDV1 == 0
		 || xDV2 == 0){
			return "-";
		}
		var xValue = ((xDV2 / xDV1) - 1) * 100;
		return dbsfaces.format.number(xValue, 2) + "%";
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

