dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
	var xChart = xChartValue.closest(".dbs_chart");
	var xCharts = xChart.closest(".dbs_charts");
	
	dbsfaces.chartValue.initialize(xChart, xChartValue);
	
	//Inicia seleção para cálculo do delta (computer e mobile)
	$(pId).on("mousedown touchstart", function(e){
//		console.log("mousedown touchstart");
		dbsfaces.chart.startDeltaDrag(xChart);
		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, true);
		e.preventDefault();
		return false;
	});

	//Desmaca item selecionado
	$(pId).mouseleave(function (e){
//		console.log("mouseleave");
		dbsfaces.chartValue.selectValue(xCharts, xChart, xChartValue, false);
	});
	
	//Seleciona nova posição do delta e do item selecionado
	$(pId).mousemove(function (e){
//		console.log("mousemove");
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
						dbsfaces.chartValue.selectValue(xTargetChartValue.closest(".dbs_charts"), xTargetChart, xTargetChartValue, true);
						e.preventDefault();
						return false;
					}
				}
			}
		}
	});
};


dbsfaces.chartValue = {
	initialize: function(pChart, pChartValue){
		//Indicar que nunca foi utilizado para cálculo do delta
		pChartValue.data("dx", null);
		///Inicializa conforme tipo de gráfico
		if (pChart.attr("type") == "pie"){
			dbsfaces.chartValue.initializePie(pChartValue);
		}else if (pChart.attr("type") == "line"){
			dbsfaces.chartValue.initializeLine(pChart, pChartValue);
		}	
	},
	
	initializeLine: function(pChart, pChartValue){
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
	
	selectDelta: function(pChart, pChartValue){
		//Não permite seleção para gráficos diferente de line ou 
		//quando valor do ponto selecionado for zero
		if (pChart.attr("type") != "line"
		 ||	typeof(pChart.attr("showdelta")) == 'undefined'
		 || pChartValue.attr("value") == "0.0"){
			return;
		}

		var xChartValuePoint = pChartValue.children(".-point");
		var xChartValueInfo = pChartValue.children(".-info");
		xValue = xChartValueInfo.children(".-value").text();
		if (xValue != 0){
			//Salva valores dentro do próprio componente para facilar o uso no momento do encode do delta
			if (pChartValue.data("dx") == null){
				pChartValue.data("dx", xChartValuePoint.attr("cx"));
				pChartValue.data("dy", xChartValuePoint.attr("cy"));
				pChartValue.data("df", xChartValuePoint.attr("fill"));
				pChartValue.data("dv", pChartValue.attr("value"));
				pChartValue.data("dl", pChartValue.attr("label"));
				pChartValue.data("dd", xChartValueInfo.children(".-value").text());
			}
			//Marca ponto origem
			if (pChart.data("cv1") == null){
				pChart.data("cv1", pChartValue);
			}
			pChart.data("cv2", pChartValue);
			dbsfaces.chartValue.showDelta(pChart, pChartValue, -1, -1);
		}
	},
	
	
	showDelta: function(pChart, pChartValue){
		if (pChart.length == 0
		|| typeof(pChart.attr("showdelta")) == 'undefined'){return;}
		var xDeltaGroup = pChart.children("g.-delta");
		if (xDeltaGroup.length == 0){
			return;
		}
		var xCV1 = pChart.data("cv1");
		var xCV2 = pChart.data("cv2");
		
		//Sai se ponto incial não foi selecionado
		if (xCV1 == null
		 || xCV2 == null){
			dbsfaces.chartValue.removeDeltaGroup(xDeltaGroup);
			return;
		}

		var xTmpX1 = xCV1.data("dx");
		var xTmpY1 = xCV1.data("dy");
		var xTmpX2 = xCV2.data("dx");
		var xTmpY2 = xCV2.data("dy");

		var xTmpDV1 = xCV1.data("dv");
		if (typeof(xTmpDV1) != 'undefined'){
			xTmpDV1 = Number(xTmpDV1);
		}
		var xTmpDV2 = xCV2.data("dv");
		if (typeof(xTmpDV1) != 'undefined'){
			xTmpDV2 = Number(xTmpDV2);
		}
		//Sai de valor selecionado for zero ou sinal entre os dois valores não for o mesmo.
		if (xTmpDV2 == 0 
		 || Math.sign(xTmpDV2) != Math.sign(xTmpDV1)){
			return;
		}
		//
		var xTmpLabel1 = xCV1.data("dl");
		var xTmpValue1 = xCV1.data("dd");
		var xTmpLabel2 = xCV2.data("dl");
		var xTmpValue2 = xCV2.data("dd");
		var xTmpFill1 = xCV1.data("df");
		var xTmpFill2 = xCV2.data("df");
		
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
		var xFill;
		var xDeltaAreaWidth = xTmpX2 - xTmpX1;
		if (xDeltaAreaWidth == 0){
			xDeltaAreaWidth = 1;
		}
		if(xDeltaAreaWidth < 0){
			xDeltaAreaWidth = Math.abs(xDeltaAreaWidth);
			xFill = xTmpFill2;
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
			xFill = xTmpFill1;
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
		var xStroke = "stroke:" + xFill + ";";

		var xSvgDeltaArea = xDeltaGroup.children(".-deltaarea");
		//Cria linha
		if (xSvgDeltaArea.length == 0){
			xSvgDeltaArea = dbsfaces.svg.rect(xDeltaGroup, xX1, 1, xDeltaAreaWidth, "100%", null, null, "-deltaarea", xStroke, xFill);
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
		
		//CV1=======================
		var xPoint1 = xDeltaGroup.children(".-point.-p1");
		if (xPoint1.length == 0){
			xPoint1 = dbsfaces.chartValue.createPoint(xDeltaGroup, xStroke, "-point -p1");
		}
		xPoint1.attr("style", "transform: translate3d(" + xX1 + "px ," + xY1 + "px,0);");

		//CV2=======================
		var xPoint2 = xDeltaGroup.children(".-point.-p2");
		if (xPoint2.length == 0){
			xPoint2 = dbsfaces.chartValue.createPoint(xDeltaGroup, xStroke, "-point -p2");
		}
		xPoint2.attr("style", "transform: translate3d(" + xX2 + "px ," + xY2 + "px,0);");

		//Box=======================
		var xWidth = 0;
		var xSvgDeltaBox = xDeltaInfoGroup.children("rect.-box");
		if (xSvgDeltaBox.length == 0){
			xSvgDeltaBox = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-5.5em", "0em", "4em", 3, 3, "-box", xStroke, null);
		}
		var xSvgDeltaBox2 = xDeltaInfoGroup.children("rect.-box2");
		if (xSvgDeltaBox2.length == 0){
			xSvgDeltaBox2 = dbsfaces.svg.rect(xDeltaInfoGroup, "0", "-5.5em", "0em", "1.5em", 3, 3, "-box2", null, xFill);
		}

		//Info Delta=======================
		var xSvgDeltaValue = xDeltaInfoGroup.children("text.-value");
		if (xSvgDeltaValue.length == 0){
			xSvgDeltaValue = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-2.8em", xDeltaValue, "-value", null, null);
		}else{
			xSvgDeltaValue.text(xDeltaValue);
		}
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaValue, null);
		
		//Info label1=======================
		var xSvgDeltaLabel1 = xDeltaInfoGroup.children("text.-label1");
		if (xSvgDeltaLabel1.length == 0){
			xSvgDeltaLabel1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-3em", "", "-label1", null, null);
		}
		xSvgDeltaLabel1.text("x:");
		if (xDeltaLabel1 != ""){
			xSvgDeltaLabel1.text(xSvgDeltaLabel1.text() + xDeltaLabel1 + ":");
		}

		var xSvgDeltaValue1 = xDeltaInfoGroup.children("text.-value1");
		if (xSvgDeltaValue1.length == 0){
			xSvgDeltaValue1 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-3em", "", "-value1", null, null);
		}
		xSvgDeltaValue1.text(xDeltaValue1);
		xWidth = dbsfaces.chartValue.getMaxWidth(xWidth, xSvgDeltaLabel1, xSvgDeltaValue1);

		//Info label2=======================
		var xSvgDeltaLabel2 = xDeltaInfoGroup.children("text.-label2");
		if (xSvgDeltaLabel2.length == 0){
			xSvgDeltaLabel2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-2em", "", "-label2", null, null);
		}
		xSvgDeltaLabel2.text("y:");
		if (typeof(xDeltaLabel2) != "undefined" 
		 && xDeltaLabel2 != ""){
			xSvgDeltaLabel2.text(xSvgDeltaLabel2.text() + xDeltaLabel2 + ":");
		}

		var xSvgDeltaValue2 = xDeltaInfoGroup.children("text.-value2");
		if (xSvgDeltaValue2.length == 0){
			xSvgDeltaValue2 = dbsfaces.svg.text(xDeltaInfoGroup, "0", "-2em", "", "-value2", null, null);
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
	
	createPoint: function(pDeltaGroup, pStroke, pStyleClass){
		var xPoint = dbsfaces.svg.g(pDeltaGroup, null, null, null,null, pStyleClass, null);
		dbsfaces.ui.moveToBack(xPoint);
		dbsfaces.svg.ellipse(xPoint, "0", "0", "1em", "1em", null, pStroke, null);
		dbsfaces.svg.ellipse(xPoint, "0", "0", ".2em", ".2em", null, pStroke, null);
		return xPoint;
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
		pChart.data("cv1", null);
		pChart.data("cv2", null);
	},



	selectChartValue: function(pCharts, pChart, pChartValue, pSelect, pIsGroup){
		var xChartValues = pChart.data("chartvalue").not(pChartValue);
		var xChartLine = pChart.children(".-line");
		var xGridLabels = pCharts.find(".-container > .-data > .-container > .-content > .-value > .-grid > .-label");

		//Seleciona o próprio item
		dbsfaces.chartValue.setSelected(xChartValues, pChartValue, pSelect);

		if (pChart.attr("type") == "bar"
		 || pChart.attr("type") == "line"){
			if (pSelect){
				//Posiciona item como primeiro elemento para aparecer acima dos demais
				dbsfaces.ui.moveToFront(pChartValue.get(0));
			}
		}
		
		//Selecion demais itens
		if (pSelect){
			//Aumenta transparencia para dos outros para enfatizar o item selecionado
			pCharts.get(0).classList.add("-dim");
		//Esconde valor 	
		}else{
			pCharts.get(0).classList.remove("-dim");
		}
	},
	
	setSelected: function(pChartValues, pChartValue, pSelect){
		var xTooltipId = "#" + dbsfaces.util.jsid(pChartValue.get(0).id) + '_tooltip';
		pChartValues.svgRemoveClass("-selected");
		pChartValue.svgRemoveClass("-selected");
		if (pSelect){
			pChartValue.svgAddClass("-selected");
			dbsfaces.tooltip.showTooltip(xTooltipId);
		}else{
			dbsfaces.tooltip.hideTooltip(xTooltipId);
		}
	},
	
	selectValue: function(pCharts, pChart, pChartValue, pSelect){
		if (dbsfaces.chart.isDeltaDragging(pChart)){
			dbsfaces.chartValue.selectDelta(pChart, pChartValue);
			return;
		}

		//Seleciona o próprio item
		dbsfaces.chartValue.selectChartValue(pCharts, pChart, pChartValue, pSelect, false);

		setTimeout(function(){
		//Seleciona itens com mesmo label em outros gráficos do mesmo grupoid
		var xGroupId = pCharts.attr("groupid");
		if (typeof(xGroupId) != 'undefined'){
			var xLabel = pChartValue.attr("label");
			var xFamily = $("div.dbs_charts[groupid='" + xGroupId + "']").not(pCharts);
			xFamily.each(function(){
				xCharts = $(this);
				var xChart = xCharts.data("chart");
				var xChartValues = xChart.data("chartvalue").filter("[label='" + xLabel + "']");
				xChartValues.each(function(){
					dbsfaces.chartValue.selectChartValue(xCharts, xChart, $(this), pSelect, true);
				});
			});
		}
		},0);
		
	}

};

