dbs_chartX = function(pId, pValues) {
	var xChart = $(pId);
	dbsfaces.chartX.initialize(xChart, pValues);

};



dbsfaces.chartX = {
	initialize: function(pChart, pValues){
		dbsfaces.chartX.pvInitializeData(pChart, pValues);
		dbsfaces.chartX.pvInitializeAnalizeValues(pChart, pValues);
		dbsfaces.chartX.pvInitializeLayout(pChart, pValues);
	},

	pvInitializeData: function(pChart, pValues){
		var xCharts = pChart.closest(".dbs_chartsX");
		pChart.data("parent", xCharts);
		pChart.data("values", pValues);
		pChart.data("selected", null);
//		dbsfaces.chartX.addChartValue(pChart, 123);
//		dbsfaces.chartX.clearChartValue(pChart);
	},

	pvInitializeAnalizeValues: function(pChart){
//		pChart.data("values", []);
		var xValues = pChart.data("values");
		var xMax = null;
		var xMin = null;
		var xTotal = 0;
		var xType = pChart.data("parent").attr("type");
		//Varifica valores máximos e mínimos e cria elemento do valor
		for (var xI = 0; xI < xValues.length; xI++){
			if (xMin == null || xValues[xI].value < xMin){
				xMin = xValues[xI].value;
			}
			if (xMax == null || xValues[xI].value > xMax){
				xMax = xValues[xI].value;
			}
			xTotal += Math.abs(xValues[xI].value);
			//Cria elemento chartvalue
			dbsfaces.chartX.pvInitializeAnalizeValuesCreateChartValue(pChart, xType, xValues[xI], xI);
		}
		pChart.data("min", xMin);
		pChart.data("max", xMax);
		pChart.data("total", xTotal);
		pChart.data("children", pChart.children(".dbs_chartValueX"));
	},
	
	pvInitializeAnalizeValuesCreateChartValue: function(pChart, pType, pValue, pI){
		var xDisplayValue = ((typeof pValue.displayValue == "undefined" || pValue.displayValue == "") ? pValue.value : pValue.displayValue);
		var xChartValue = dbsfaces.svg.g(pChart, "dbs_chartValueX -" + pType, null, {"index": pI});
//		var xChartValuePoint = dbsfaces.svg.path(xChartValue, null, "-point", null, null);
//		var xChartValuePoint = dbsfaces.svg.g(xChartValue, "-point", null, null);
//		var xChartValueInfoLabel = dbsfaces.svg.g(xChartValueInfo, "-label", null, null);
//		var xChartValueInfoLabelText = dbsfaces.svg.text(xChartValueInfoLabel, null, null, pValue.label, null, null);
//		var xChartValueInfoLabelTextSpan = dbsfaces.svg.tspan(xChartValueInfoLabelText, pValue.label, "-normal", null, null);
		var xChartValuePoint;
		var xChartValueInfo = dbsfaces.svg.g(xChartValue, "-info", null, null);
//		//Texto do Label
		var xChartValueInfoLabel = dbsfaces.svg.text(xChartValueInfo, null, null, pValue.label, "-label", null, null);
		//Texto do Valor
		var xChartValueInfoValue = dbsfaces.svg.text(xChartValueInfo, null, null, xDisplayValue, "-value", null, null);
		var xChartValueInfoBox = null;
		if (pType == "line"){
			//Box
			xChartValueInfoBox = dbsfaces.svg.rect(xChartValueInfo, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValuePoint = dbsfaces.svg.circle(xChartValue, null, null, null, "-point", null, {"r": ".2em"}); //'r' precisa ser um atributo por problema no FIREFOX
		}else if (pType == "bar"){
			//Box
			xChartValueInfoBox = dbsfaces.svg.rect(xChartValueInfo, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValuePoint = dbsfaces.svg.path(xChartValue, null, "-point", null, null);
//			//Texto reduzido
//			xChartValueInfoLabelTextSpanSmall = dbsfaces.svg.tspan(xChartValueInfoLabelText, null, "-small", null, null);
		}else if (pType == "pie"){
			//Box
			xChartValueInfoBox = dbsfaces.svg.rect(xChartValueInfo, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValuePoint = dbsfaces.svg.path(xChartValue, null, "-point", null, null);
		}
		xChartValue.data("parent", pChart);
		xChartValue.data("value", pValue);
		xChartValue.data("index", pI);
		xChartValue.data("point", xChartValuePoint);
		xChartValue.data("info", xChartValueInfo);
		xChartValue.data("infolabel", xChartValueInfoLabel);
		xChartValue.data("infovalue", xChartValueInfoValue);
		xChartValue.data("infobox", xChartValueInfoBox);
	},

	pvInitializeLayout: function(pChart){
		var xType = pChart.data("parent").attr("type");
		if (xType = "line"){
			dbsfaces.chartX.pvInitializeLayoutChartLine(pChart);
		}
	},

	pvInitializeLayoutChartLine: function(pChart){
		//Cria linha que conecta pontos
		var xChartLinePath = dbsfaces.svg.path(pChart, null, "-line", null, null);
		pChart.data("linepath", xChartLinePath);
		//Captura movimento do mouse para seleciona ponto
		pChart.on("mousemove touchmove touchstart", function(e){
			dbsfaces.chartX.findPoint(e, pChart);
			e.stopImmediatePropagation();
			return false;
		});
	},

	findPoint: function(e, pChart){
		var xDecimals = 1;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		var xPosition = pChart.offset();
		var xCurrentX = dbsfaces.math.round(xXY.x - xPosition.left + $(window).scrollLeft(), xDecimals);
		if (xCurrentX < 0){return;}
		var xChartPath = pChart.data("linepath")[0];
	    var xBeginning = xCurrentX;
        var xEnd = dbsfaces.math.round(xChartPath.getTotalLength(), xDecimals);
        var xTargetLenght;
        var xTargetPos;
        var xTargetPosX;
        //Procura ponto da caminho(path) que o X é iqual a posição X selecionada
        while (Math.abs(xBeginning - xEnd) > 1) {
        	xTargetLenght = xBeginning +  dbsfaces.math.round((xEnd - xBeginning) / 2, xDecimals); //Meio do caminho
			xTargetPos = xChartPath.getPointAtLength(xTargetLenght); //Ponto do path 
			xTargetPosX = dbsfaces.math.round(xTargetPos.x, xDecimals);
			if (xTargetPosX < xCurrentX){
				xBeginning = xTargetLenght;
			}else if (xTargetPosX > xCurrentX){
				xEnd = xTargetLenght;
			}else{
				break; //Encontrou posição
			}
        }
		if (typeof(xTargetPos) != "undefined"){
			//Procura qual dos chartsValues está mais próximo a posição do cursor
			var xChartChildren = pChart.data("children");
			var xTotalSegs = pChart.data("linepath").svgGetPathTotalSegs();
			var xIndex = xChartPath.getPathSegAtLength(xTargetLenght);
			var xClosestX = xCurrentX;
			var xChartValue = $(xChartChildren[xIndex]);
			var xX = Number(xChartValue.data("x"));
			var xY = Number(xChartValue.data("y"));
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX){
				if (xIndex > 0){
					xClosestX = $(xChartChildren[xIndex - 1]).data("x");
				}
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX){
				if (xIndex < xTotalSegs){
					xClosestX = $(xChartChildren[xIndex + 1]).data("x");
				}
			}
			var xXMiddle = (Number(xClosestX) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValue = $(xChartChildren[xIndex - 1]);
			}
			//Seleciona chartvalue encontrado
			dbsfaces.chartX.select(pChart, xChartValue);
		}
	},

	select: function(pChart, pChartValue){
		if (pChart.data("selected") != null){
			pChart.data("selected").removeClass("-selected");
		}else if(pChart.data("selected") == pChartValue){
			return;
		}
		pChartValue.addClass("-selected");
		pChart.data("selected", pChartValue);
	},
	
	addChartValue: function(pChart, pValue, pLabel, pDisplayValue, pTooltip){
		if (typeof pValue == "undefined"){
			return;
		}
		if (typeof pLabel == "undefined"){
			pLabel = pValue.toString();
		}
		if (typeof pDisplayValue == "undefined"){
			pDisplayValue = pLabel;
		}
		if (typeof pTooltip == "undefined"){
			pTooltip = "";
		}
		var xValue = JSON.parse('{ "value":' + pValue + ', "label":"' + pLabel + '", "displayValue":"' + pDisplayValue + '", "tooltip":"' + pTooltip + '"}');
		pChart.data("values").push(xValue);
	},
	
	clearChartValue: function(pChart){
		pChart.data("values", []);
	}
};

