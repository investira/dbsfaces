dbs_chartValue = function(pId) {
	var xChart = $(pId).closest(".dbs_chart");
	if (xChart.attr("type") == "line"){
		dbsfaces.chartValue.drawLines(pId);
	}
	
	$(pId).click(function(e){
		dbsfaces.chartValue.selectDelta(pId);
	});
	
	$(pId).mouseenter(function (e){
		dbsfaces.chartValue.selectValue(pId, true);
	});
	$(pId).mouseleave(function (e){
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
		var xSvgLine = pLineGroup.children("line[index='" + xId + "']");
		//Altera linha caso já exista
		if (xSvgLine.length != 0){
			xSvgLine.attr("x1", xX1)
				 	.attr("y1", xY1)
				 	.attr("x2", xX2)
				 	.attr("y2", xY2)
				 	.attr("stroke", xStroke);
		//Cria linha
		}else{
			xSvgLine = dbsfaces.svg.line(pLineGroup, xX1, xY1, xX2, xY2, null, "stroke:" + xStroke);
			xSvgLine.attr("index", xId);
		}
	},
	
	selectDelta: function(pId){
		var xChartValue = $(pId);
		var xChart = xChartValue.closest(".dbs_chart");
		if (typeof(xChart.attr("showdelta")) == 'undefined'){
			return;
		}
		//Não permite seleção para gráficos diferente de line ou 
		//quando valor do ponto selecionado for zero
		if (xChart.attr("type") != "line"
		 ||	typeof(xChart.attr("showdelta")) == 'undefined'
		 || xChartValue.attr("value") == "0.0"){
			return;
		}
		var xChartValuePoint = xChartValue.children(".-point");
		//Desmaca pontos
		if (typeof(xChart.attr("dx1")) != 'undefined'
		 && typeof(xChart.attr("dx2")) != 'undefined'){
			dbsfaces.chartValue.removeDelta(xChart);
		}
		//Marca ponto origem
		if (typeof(xChart.attr("dx1")) == 'undefined'){
			var xChartValueInfo = xChartValue.children(".-info");
			xValue = xChartValueInfo.children(".-value").text();
			if (xValue != 0){
				xChart.get(0).setAttribute("dx1", xChartValuePoint.attr("cx"));
				xChart.get(0).setAttribute("dy1", xChartValuePoint.attr("cy"));
				xChart.get(0).setAttribute("dv1", xChartValue.attr("value"));
				xChart.get(0).setAttribute("dl1", xChartValue.attr("label"));
				xChart.get(0).setAttribute("dd1", xChartValueInfo.children(".-value").text());
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
	
	
	showDelta: function(pChart, pChartValue, pXTmp, pYTmp){
		if (pChart.length == 0
		|| typeof(pChart.attr("showdelta")) == 'undefined'){return;}
		var xDeltaGroup = pChart.children("g.-delta");
		if (xDeltaGroup.length == 0){
			return;
		}
		//Sai se ponto incial não foi selecionado
		var xX1 = pChart.attr("dx1");
		var xY1 = pChart.attr("dy1");
		if (typeof(xX1) == 'undefined'
		 && pXTmp == -1){
			dbsfaces.chartValue.removeDeltaGroup(xDeltaGroup);
			return;
		}

		var xX2 = pChart.attr("dx2");
		var xY2 = pChart.attr("dy2");
		//Se ponto final não foi informado, força ponto final como o temporário ou iqual ao ponto inicial
		if (typeof(xX2) == 'undefined'){
			//Se ponto temporário não foi informado
			if (pXTmp == -1){
				xX2 = xX1;
				xY2 = xY1;
			}else{
				xX2 = pXTmp;
				xY2 = pYTmp;
			}
		//Sai se pointo final foi selecionado
		}else{
			return;
		}

		var xDV1 = pChart.attr("dv1");
		if (typeof(xDV1) == 'undefined'){
			xDV1 = pChartValue.attr("value");
		}
		//Sai de valor selecionado for zero ou sinal entre os dois valores não for o mesmo.
		if (pChartValue.attr("value") == "0.0" ||
		    Math.sign(pChartValue.attr("value")) != Math.sign(xDV1)){
			return;
		}

		//
		var xDeltaLabel1 = pChart.attr("dl1");
		var xDeltaValue1 = pChart.attr("dd1");
		var xDeltaLabel2 = pChartValue.attr("label");
		var xDeltaValue2 = pChartValue.children(".-info").children(".-value").text();
		var xDeltaValue = dbsfaces.chartValue.calcDelta(pChart, pChartValue);

		var xChartValuePoint = pChartValue.find(".-point");
		var xStroke = "stroke:" + xChartValuePoint.attr("fill") + ";";

		//Sai se ponto inicial foi selecionado
		if (typeof(xX1) == 'undefined'){
			xX1 = pXTmp;
			xY1 = pYTmp;
			xDeltaLabel1 = xDeltaLabel2;
			xDeltaValue1 = xDeltaValue2;
			xDeltaLabel2 = "";
			xDeltaValue2 = "";
			xDeltaValue = "-";
		}

		//Line
		var xSvgLine = xDeltaGroup.children("line.-deltaline");
		//Cria linha
		if (xSvgLine.length == 0){
			xSvgLine = dbsfaces.svg.line(xDeltaGroup, xX1, xY1, xX2, xY2, "-deltaline", xStroke);
			//Altera linha se já existir
		}else{
			xSvgLine.attr("x1", xX1)
					 .attr("y1", xY1)
					 .attr("x2", xX2)
					 .attr("y2", xY2)
					 .attr("style", xStroke);
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
		var xSvgDeltaBox = xDeltaInfoGroup.children("rect.-box");
		if (xSvgDeltaBox.length == 0){
			xSvgDeltaBox = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-4em", "0em", "4em", 3, 3, "-box", xStroke, null);
		}
		var xSvgDeltaBox2 = xDeltaInfoGroup.children("rect.-box2");
		if (xSvgDeltaBox2.length == 0){
			xSvgDeltaBox2 = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-4em", "0em", "1.5em", 3, 3, "-box2", null, xChartValuePoint.attr("fill"));
		}
		var xPoint = xDeltaInfoGroup.children("ellipse");
		if (xPoint.length == 0){
			xPoint = dbsfaces.svg.ellipse(xDeltaInfoGroup, "0", "0", "0.3em", "0.3em", null, null, xChartValuePoint.attr("fill"));
		}

		//Info Delta=======================
		var xSvgDeltaValue = xDeltaInfoGroup.children("text.-value");
		if (xSvgDeltaValue.length == 0){
			xSvgDeltaValue = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-1.8em", xDeltaValue, "-value", null, null);
		}else{
			xSvgDeltaValue.text(xDeltaValue);
		}
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaValue, null);
		
		//Info label1=======================
		var xSvgDeltaLabel1 = xDeltaInfoGroup.children("text.-label1");
		if (xSvgDeltaLabel1.length == 0){
			xSvgDeltaLabel1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-1.5em", "", "-label1", null, null);
		}
		xSvgDeltaLabel1.text("x:");
		if (xDeltaLabel1 != ""){
			xSvgDeltaLabel1.text(xSvgDeltaLabel1.text() + xDeltaLabel1 + ":");
		}

		var xSvgDeltaValue1 = xDeltaInfoGroup.children("text.-value1");
		if (xSvgDeltaValue1.length == 0){
			xSvgDeltaValue1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-1.5em", "", "-value1", null, null);
		}
		xSvgDeltaValue1.text(xDeltaValue1);
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaLabel1, xSvgDeltaValue1);

		//Info label2=======================
		var xSvgDeltaLabel2 = xDeltaInfoGroup.children("text.-label2");
		if (xSvgDeltaLabel2.length == 0){
			xSvgDeltaLabel2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-.5em", "", "-label2", null, null);
		}
		xSvgDeltaLabel2.text("y:");
		if (typeof(xDeltaLabel2) != "undefined" 
		 && xDeltaLabel2 != ""){
			xSvgDeltaLabel2.text(xSvgDeltaLabel2.text() + xDeltaLabel2 + ":");
		}

		var xSvgDeltaValue2 = xDeltaInfoGroup.children("text.-value2");
		if (xSvgDeltaValue2.length == 0){
			xSvgDeltaValue2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-.5em", "", "-value2", null, null);
		}
		xSvgDeltaValue2.text(xDeltaValue2);
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaLabel2, xSvgDeltaValue2);


		//Box=======================
//		xPadding = xWidth * .20;
//		xWidthLeft = xWidth/2;

		xWidthLeft = xWidth / 2 + 2;
		xSvgDeltaLabel1.attr("x", -xWidthLeft);
		xSvgDeltaLabel2.attr("x", -xWidthLeft);
		xSvgDeltaValue1.attr("x", xWidthLeft);
		xSvgDeltaValue2.attr("x", xWidthLeft);

		xWidth *= 1.15;
		xSvgDeltaBox.attr("x",xWidth/-2)
					.attr("width", xWidth);
		xSvgDeltaBox2.attr("x",xWidth/-2)
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
		if (typeof(pChart.attr("dv1")) == 'undefined'){
			return dbsfaces.format.number(0, 2) + "%";
		}
		var xDV1 = Number(pChart.attr("dv1"));
		var xDV2 = Number(pChartValue.attr("value"));
		if (xDV1 == 0
		 || xDV2 == 0){
			return "-";
		}
		var xValue;
		if (xDV1 < 0){
			xValue = (xDV1 / xDV2);
		}else{
			xValue = (xDV2 / xDV1);
		}
		xValue = (xValue - 1) * 100;
		return dbsfaces.format.number(xValue, 2) + "%";
	},

	hideDelta: function(pChart){
		if (typeof(pChart.attr("dx2")) == 'undefined'){
			pChart.children("g.-delta").fadeOut("slow", function(){
				dbsfaces.chartValue.removeDelta(pChart);
			});
		}
	},

	removeDelta: function(pChart){
		var xDeltaGroup = pChart.children("g.-delta");
		if (xDeltaGroup.length != 0){
			dbsfaces.chartValue.removeDeltaGroup(xDeltaGroup);
		}
		dbsfaces.chartValue.removeDeltaAttrs(pChart);
	},
	
	removeDeltaAttrs: function(pChart){
		pChart.get(0).removeAttribute("dx1");
		pChart.get(0).removeAttribute("dy1");
		pChart.get(0).removeAttribute("dx2");
		pChart.get(0).removeAttribute("dy2");
		pChart.get(0).removeAttribute("dv1");
		pChart.get(0).removeAttribute("dl1");
		pChart.get(0).removeAttribute("dd1");
//		pChart.get(0).removeAttribute("stylddeeeeeeeeeeeeexde");
		var xChartValues = pChart.find(".dbs_chartValue.-selectedDelta");
		xChartValues.each(function(){
			this.classList.remove("-selectedDelta");
		});
	},

	removeDeltaGroup: function(pDeltaGroup){
		if (pDeltaGroup.length == 0){return;}
		pDeltaGroup.empty();
		pDeltaGroup.show();
	},


	//Coloca item como primeiro elemento para aparecer acima dos demais
	moveToFront: function(pItem){
		var xChart = $(pItem).closest(".dbs_chart");
		if (xChart.attr("type") == "bar"
		 || xChart.attr("type") == "line"){
//			pItem.parentElement.appendChild(pItem);
		}
	},
	
	selectChartValue: function(pCharts, pChartValue, pSelect, pIsGroup){
		var xChartValueInfo = pChartValue.children(".-info");
		var xChart = pChartValue.closest(".dbs_chart");
		var xLabel = xChartValueInfo.children(".-label");
		var xValue = xChartValueInfo.children(".-value");
		var xChartLine = xChart.children(".-line");
		var xTooltipId = "#" + dbsfaces.util.jsid(pChartValue.get(0).id) + '_tooltip';
		if (pSelect){
			//Posiciona item como primeiro elemento para aparecer acima dos demais
			if (xChart.attr("type") == "bar"
			 || xChart.attr("type") == "line"){
//				pChartValue.get(0).parentElement.appendChild(pChartValue.get(0));
//				pChartValue.get(0).parentElement.insertBefore(pChartValue.get(0), xChart.children(".-delta").get(0));
			}
			
			dbsfaces.tooltip.showTooltip(xTooltipId);
			$(xTooltipId).addClass("-selected");
			if (xLabel.length > 0){
				xLabel.get(0).classList.add("-selected");
			}
			if (xValue.length > 0){
				xValue.get(0).classList.add("-selected");
			}
		}else{
			dbsfaces.tooltip.hideTooltip(xTooltipId);
			$(xTooltipId).removeClass("-selected");
			if (xLabel.length > 0){
				xLabel.get(0).classList.remove("-selected");
			}
			if (xValue.length > 0){
				xValue.get(0).classList.remove("-selected");
			}
		}
		var xChartValuePoint = pChartValue.children(".-point");
		var xGridLabels = pCharts.find(".-container > .-data > .-container > .-content > .-value > .-grid > .-label");
		var xChartValues = pCharts.find(".dbs_chartValue").not(pChartValue);
		var xDataLabels = xChartValues.children(".-info");
//		var xDataLabels = xValue.siblings(".dbs_chartValue").children(".-info").children(".-label").not("[class ~= '-hide']").not(xChartValueInfo);
		//Exibe valor selecionado
		if (pSelect){
			xDataLabels.hide();
			xGridLabels.hide();
			//Aumenta transparencia para dos outros para enfatizar o item selecionado
			if (xChartLine.length > 0){
				xChartLine.get(0).classList.add("-dim");
			}
			xChartValues.each(function(){
				this.classList.add("-dim");
			});
			pChartValue.get(0).classList.add("-selected");
		//Esconde valor 	
		}else{
			xDataLabels.show();
			xGridLabels.show();
			if (xChartLine.length > 0){
				xChartLine.get(0).classList.remove("-dim");
			}
			//Retira transparencia
			xChartValues.each(function(){
				this.classList.remove("-dim");
			});
			pChartValue.get(0).classList.remove("-selected");
		}
		//Exibe informação do delta
		if (xChart.attr("type") == "line"){
			//Exibe delta quando seleção for no componente principal
			if (pIsGroup
			&& typeof(xChart.attr("dx2")) == 'undefined'){
				//Remove delta dos outros componentes do mesmo grupo
				dbsfaces.chartValue.removeDelta(xChart);
			}else{
				dbsfaces.chartValue.showDelta(xChart, pChartValue, xChartValuePoint.attr("cx"), xChartValuePoint.attr("cy"));
			}
		}
	},
	
	selectValue: function(pId, pSelect){
		var xChartValue = $(pId);
		if (xChartValue.length == 0){return;}
		var xCharts = xChartValue.closest(".dbs_charts");
		
		dbsfaces.chartValue.selectChartValue(xCharts, xChartValue, pSelect, false);

		var xGroupId = xCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xLabel = xChartValue.attr("label");
			var xFamily = $("div.dbs_charts[groupid='" + xGroupId + "']").not(xCharts);
			xFamily.each(function(){
				xCharts = $(this);
				var xChartValues = $(this).find(".dbs_chartValue[label='" + xLabel + "']");
				xChartValues.each(function(){
					dbsfaces.chartValue.selectChartValue(xCharts, $(this), pSelect, true);
				});
			});
		}
		
	}

};

