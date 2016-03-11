dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
	var xChart = xChartValue.closest(".dbs_chart");
	var xCharts = xChart.closest(".dbs_charts");

	dbsfaces.chartValue.initialize(xChart, xChartValue);
	
	//Inicia seleção para cálculo do delta (computer e mobile)
	$(pId).on("mousedown touchstart", function(e){
		dbsfaces.chartValue.selectDelta(xChart, xChartValue, 1);
		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
		e.preventDefault();
		return false;
	});

	//Desmaca item selecionado
	$(pId).mouseleave(function (e){
		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, false);
	});
	
	//Seleciona nova posição do delta e do item selecionado
	$(pId).mousemove(function (e){
		dbsfaces.chartValue.selectDelta(xChart, xChartValue, null);
		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
	});

	//Seleciona nova posição do delta e do item selecionado (Mobile)
	$(pId).on("touchmove", function(e){
		//Verifica se elemento é um dbs_chartValue
		var xTarget = document.elementFromPoint(e.originalEvent.touches[0].clientX, e.originalEvent.touches[0].clientY);
		if (typeof(xTarget) != "undefined"){
			var xTargetChartValue = $(xTarget).parent();
			if (typeof(xTargetChartValue) != "undefined"){
				var xClass = xTargetChartValue.attr("class");
				if (typeof(xClass) != "undefined" 
				 && xClass.indexOf('dbs_chartValue') != -1){
					var xTargetChart = xTargetChartValue.closest(".dbs_chart");
					if (xTargetChart.get(0) == xChart.get(0)){
						dbsfaces.chartValue.selectDelta(xChart, xTargetChartValue, null);
						e.preventDefault();
						return false;
					}
				}
			}
			
		}
	});
	


};

//dbsfaces.chartValue.chartValueData = function(){
//	this.X = null;
//	this.Y = null;
//	this.DV = null;
//	this.DeltaLabel = null;
//	this.DeltaValue = null;
//}

dbsfaces.chartValue = {
	chartValueData: function(){
	    this.legs = 4;
	    this.temperament = 'Apathetic';
	    this.sound = 'Meow';
	},
	
	initialize: function(pChart, pChartValue){
		if (pChart.attr("type") == "line"){
			dbsfaces.chartValue.initializeLine(pChart, pChartValue);
		}else if (pChart.attr("type") == "pie"){
			dbsfaces.chartValue.initializePie(pChartValue);
		}
	},
	
	initializeLine: function(pChart, pChartValue){
		var xLineGroup = pChart.children("g.-line");
		var xChartValueAnterior = pChartValue.prevAll(".dbs_chartValue").first();
		
		dbsfaces.chartValue.drawLine(xLineGroup, xChartValueAnterior);
		dbsfaces.chartValue.drawLine(xLineGroup, pChartValue);
	},

	initializePie: function(pChartValue){
		var xChartValueInfo = pChartValue.children(".-info");
		var xChartValueValue = xChartValueInfo.children(".-value");
		var xChartValueBox = xChartValueInfo.children(".-box");
		var xTransform = "translateY(-0.70em) ";
		var xWidth = xChartValueValue.get(0).getComputedTextLength() + 5;
		//Ajusta largura do box do label
		if (xChartValueValue.length != 0){
			xChartValueBox.attr("width", xWidth);
			if (xChartValueValue.attr("style").indexOf("text-anchor:end") != -1){
				xTransform += "translateX(-" + xWidth + "px)";
			}
			dbsfaces.ui.transform(xChartValueBox, xTransform);
		}
	},
	
	//Encode de linha que liga os pontos
	drawLine: function(pLineGroup, pChartValue){
		if (pChartValue.length == 0){return;}
		var xChartValuePosterior = pChartValue.nextAll(".dbs_chartValue").first().children(".-point");
		if (xChartValuePosterior.length == 0){return;}
		var xChartValuePoint = pChartValue.children(".-point");
		var xId = pChartValue.attr("id");
		var xStyle = "stroke:" + xChartValuePoint.attr("fill");
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
				 	.attr("style", xStyle);
		//Cria linha
		}else{
			xSvgLine = dbsfaces.svg.line(pLineGroup, xX1, xY1, xX2, xY2, null, xStyle);
			xSvgLine.attr("index", xId);
		}
	},
	
	selectDelta: function(pChart, pChartValue, pMouseDown){
		//Não permite seleção para gráficos diferente de line ou 
		//quando valor do ponto selecionado for zero
		if (pChart.attr("type") != "line"
		 ||	typeof(pChart.attr("showdelta")) == 'undefined'
		 || pChartValue.attr("value") == "0.0"){
			return;
		}
		//Se for move
		if (pMouseDown == null){
			//Sai se não estiver com mousedown
			if(dbsfaces.chart.getMouseDown(pChart) != 1){
				return;
			}
		}else if (pMouseDown == 1){
			dbsfaces.chartValue.removeDelta(pChart);
			dbsfaces.chart.setMouseDown(pChart, pMouseDown);
		} 
		
		var xChartValuePoint = pChartValue.children(".-point");
		//Marca ponto origem
		if (pMouseDown == 1){
			var xChartValueInfo = pChartValue.children(".-info");
			xValue = xChartValueInfo.children(".-value").text();
			if (xValue != 0){
				pChart.get(0).setAttribute("dx1", xChartValuePoint.attr("cx"));
				pChart.get(0).setAttribute("dy1", xChartValuePoint.attr("cy"));
				pChart.get(0).setAttribute("dv1", pChartValue.attr("value"));
				pChart.get(0).setAttribute("dl1", pChartValue.attr("label"));
				pChart.get(0).setAttribute("dd1", xChartValueInfo.children(".-value").text());
				pChartValue.get(0).classList.add("-selectedDelta");
			}
		//Marca ponto destino
//		}else if (typeof(pChart.attr("dx2")) == 'undefined'){
		}
		pChart.get(0).setAttribute("dx2", xChartValuePoint.attr("cx"));
		pChart.get(0).setAttribute("dy2", xChartValuePoint.attr("cy"));

		dbsfaces.chartValue.showDelta(pChart, pChartValue, -1, -1);
	},
	
	
	showDelta: function(pChart, pChartValue){
		if (pChart.length == 0
		|| typeof(pChart.attr("showdelta")) == 'undefined'){return;}
		var xDeltaGroup = pChart.children("g.-delta");
		if (xDeltaGroup.length == 0){
			return;
		}
		
		//Sai se ponto incial não foi selecionado
		var xTmpX1 = pChart.attr("dx1");
		var xTmpY1 = pChart.attr("dy1");
		if (typeof(xTmpX1) == 'undefined'){
			dbsfaces.chartValue.removeDeltaGroup(xDeltaGroup);
			return;
		}

		var xTmpX2 = pChart.attr("dx2");
		var xTmpY2 = pChart.attr("dy2");
		//Força ponto final como o temporário ou iqual ao ponto inicial, se ponto final não foi informado, 
		if (typeof(xTmpX2) == 'undefined'){
			//Se ponto temporário não foi informado
			xTmpX2 = xTmpX1;
			xTmpY2 = xTmpY1;
		}

		var xTmpDV1 = pChart.attr("dv1");
		if (typeof(xTmpDV1) != 'undefined'){
			xTmpDV1 = Number(xTmpDV1);
		}
		var xTmpDV2 = pChartValue.attr("value");
		if (typeof(xTmpDV1) != 'undefined'){
			xTmpDV2 = Number(xTmpDV2);
		}
		//Sai de valor selecionado for zero ou sinal entre os dois valores não for o mesmo.
		if (xTmpDV2 == 0 
		 || Math.sign(xTmpDV2) != Math.sign(xTmpDV1)){
			return;
		}
		//
		var xTmpLabel1 = pChart.attr("dl1");
		var xTmpValue1 = pChart.attr("dd1");
		var xTmpLabel2 = pChartValue.attr("label");
		var xTmpValue2 = pChartValue.children(".-info").children(".-value").text();
		

		var xX1;
		var xY1;
		var xDV1;
		var xDeltaLabel1;
		var xDeltaValue1;
		var xX2;
		var xY2;
		var xDV2;
		var xDeltaLabel2;
		var xDeltaValue2;
		var xDeltaAreaWidth = xTmpX2 - xTmpX1;
		if (xDeltaAreaWidth == 0){
			xDeltaAreaWidth = 1;
		}
		if(xDeltaAreaWidth < 0){
			xDeltaAreaWidth = Math.abs(xDeltaAreaWidth);
			xX1 = xTmpX2;
			xY1 = xTmpY2;
			xDeltaLabel1 = xTmpLabel2;
			xDeltaValue1 = xTmpValue2;
			xDV1 = xTmpDV2;

			xX2 = xTmpX1;
			xY2 = xTmpY1;
			xDeltaLabel2 = xTmpLabel1;
			xDeltaValue2 = xTmpValue1;
			xDV2 = xTmpDV1;
		}else{
			xX1 = xTmpX1;
			xY1 = xTmpY1;
			xDeltaLabel1 = xTmpLabel1;
			xDeltaValue1 = xTmpValue1;
			xDV1 = xTmpDV1;

			xX2 = xTmpX2;
			xY2 = xTmpY2;
			xDeltaLabel2 = xTmpLabel2;
			xDeltaValue2 = xTmpValue2;
			xDV2 = xTmpDV2;
		}

		var xDeltaValue = dbsfaces.chartValue.calcDelta(xDV1, xDV2);
		var xChartValuePoint = pChartValue.find(".-point");
		var xStroke = "stroke:" + xChartValuePoint.attr("fill") + ";";

		var xSvgDeltaArea = xDeltaGroup.children(".-deltaarea");
		//Cria linha
		if (xSvgDeltaArea.length == 0){
			xSvgDeltaArea = dbsfaces.svg.rect(xDeltaGroup, xX1, 1, xDeltaAreaWidth, "100%", null, null, "-deltaarea", xStroke, xChartValuePoint.attr("fill"));
		//Altera linha se já existir
		}else{
			xSvgDeltaArea.attr("x", xX1)
					 	 .attr("width", xDeltaAreaWidth);
		}		
		//Group info----------------------
		var xDeltaInfoGroup = xDeltaGroup.children(".-info");

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
			xPoint = dbsfaces.svg.ellipse(xDeltaInfoGroup, "0", "0", "0.3em", "0.3em", null, xStroke, null);
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
	calcDelta: function(pDV1, pDV2){
		if (pDV1 == 0
		 || pDV2 == 0){
			return "-";
		}
		var xValue;
		if (pDV1 < 0){
			xValue = (pDV1 / pDV2);
		}else{
			xValue = (pDV2 / pDV1);
		}
		xValue = (xValue - 1) * 100;
		return dbsfaces.format.number(xValue, 2) + "%";
	},

	//Chamado pelo dbsfaces_chart.js
	hideDelta: function(pChart){
		pChart.children("g.-delta").fadeOut("slow", function(){
			dbsfaces.chartValue.removeDelta(pChart);
		});
	},

	removeDeltaGroup: function(pDeltaGroup){
		if (pDeltaGroup.length == 0){return;}
		pDeltaGroup.empty();
		pDeltaGroup.show();
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
		var xChartValues = pChart.find(".dbs_chartValue.-selectedDelta");
		xChartValues.each(function(){
			this.classList.remove("-selectedDelta");
		});
	},



	//Coloca item como primeiro elemento para aparecer acima dos demais
	moveToFront: function(pChartValue){
		pChartValue.parentElement.appendChild(pChartValue);
	},
	
	selectChartValue: function(pCharts, pChart, pChartValue, pSelect, pIsGroup){
		var xChartLine = pChart.children(".-line");
		var xChartValuePoint = pChartValue.children(".-point");
		var xGridLabels = pCharts.find(".-container > .-data > .-container > .-content > .-value > .-grid > .-label");
		var xChartValues = pCharts.find(".dbs_chartValue").not(pChartValue);

		//Seleciona o próprio item
		dbsfaces.chartValue.setSelected(pChartValue, pSelect);
		
		//Selecion demais itens
		if (pSelect){
			//Posiciona item como primeiro elemento para aparecer acima dos demais
			if (pChart.attr("type") == "bar"
			 || pChart.attr("type") == "line"){
				dbsfaces.chartValue.moveToFront(pChartValue.get(0));
			}
			xGridLabels.hide();
			//Aumenta transparencia para dos outros para enfatizar o item selecionado
			if (xChartLine.length > 0){
				xChartLine.get(0).classList.add("-dim");
			}
			//Retira dim do selecionado e incluir nos restantes
			pChartValue.get(0).classList.remove("-dim");
			xChartValues.each(function(){
				xChartValueInfo = $(this).children(".-info");
				xChartValueInfo.hide();
				this.classList.add("-dim");
				dbsfaces.chartValue.setSelected($(this), false);
			});
		//Esconde valor 	
		}else{
			xGridLabels.show();
			if (xChartLine.length > 0){
				xChartLine.get(0).classList.remove("-dim");
			}
			//Retira dim do restante
			xChartValues.each(function(){
				this.classList.remove("-dim");
				dbsfaces.chartValue.setSelected($(this), pSelect);
			});
		}
	},
	
	setSelected: function(pChartValue, pSelect){
		var xTooltipId = "#" + dbsfaces.util.jsid(pChartValue.get(0).id) + '_tooltip';
		var xChartValueInfo = pChartValue.children(".-info");
		var xLabel = xChartValueInfo.children(".-label");
		var xValue = xChartValueInfo.children(".-value");
		if (pSelect){
			dbsfaces.tooltip.showTooltip(xTooltipId);
			$(xTooltipId).addClass("-selected");
			if (xLabel.length > 0){
				xLabel.get(0).classList.add("-selected");
			}
			if (xValue.length > 0){
				xValue.get(0).classList.add("-selected");
			}
			pChartValue.get(0).classList.add("-selected");
			xChartValueInfo.show();
		}else{
			dbsfaces.tooltip.hideTooltip(xTooltipId);
			$(xTooltipId).removeClass("-selected");
			if (xLabel.length > 0){
				xLabel.get(0).classList.remove("-selected");
			}
			if (xValue.length > 0){
				xValue.get(0).classList.remove("-selected");
			}
			pChartValue.get(0).classList.remove("-selected");
		}
	},
	
	selectValue: function(pCharts, pChart, pChartValue, pSelect){
		//Seleciona o próprio item
		dbsfaces.chartValue.selectChartValue(pCharts, pChart, pChartValue, pSelect, false);

		//Seleciona itens com mesmo label em outros gráficos do mesmo grupoid
		var xGroupId = pCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xLabel = pChartValue.attr("label");
			var xFamily = $("div.dbs_charts[groupid='" + xGroupId + "']").not(pCharts);
			xFamily.each(function(){
				xCharts = $(this);
				var xChartValues = xCharts.find(".dbs_chart > g.dbs_chartValue[label='" + xLabel + "']");
				xChartValues.each(function(){
					dbsfaces.chartValue.selectChartValue(xCharts, pChart, $(this), pSelect, true);
				});
			});
		}
		
	}

};

