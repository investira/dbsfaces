dbs_chartX = function(pId, pValues) {
	var xChart = $(pId);
	dbsfaces.chartX.initialize(xChart, pValues);

};



dbsfaces.chartX = {
	initialize: function(pChart, pValues){
		dbsfaces.chartX.pvInitializeData(pChart, pValues);
		dbsfaces.chartX.pvInitializeAnalizeValues(pChart.data("data"));
		dbsfaces.chartX.pvInitializeLayout(pChart.data("data"));
	},

	pvInitializeData: function(pChart, pValues){
		var xCharts = pChart.closest(".dbs_chartsX");
		var xData = {
			self: pChart, //O próprio chart
			type: xCharts.attr("type"), //Tipo de gráfico
			parent: xCharts, //Pai
			children: null, //Filhos
			chart: pChart.children(".-chart"), //Container dos filhos
			info: pChart.children(".-info"), //Container das infos
			values: pValues, //Valores recebidos
			selected: null, //Se grático está selecionado
			chartValueMin: null, //chartValue que contém o valor máximo
			chartValueMax: null,//chartValue que contém o valor mínimo
			med: null, //valor médio
			path: "" //Elemento que contém o caminho
		}
		pChart.data("data", xData);
//		dbsfaces.chartX.addChartValue(pChart, 123);
//		dbsfaces.chartX.clearChartValue(pChart);
	},

	pvInitializeAnalizeValues: function(pChartData){
//		pChart.data("values", []);
		var xValues = pChartData.values;
		var xMax = null;
		var xMin = null;
		var xMed = 0
//		var xTotalAbs = 0;
		var xTotal = 0;
		//Varifica valores máximos e mínimos e cria elemento do valor
		for (var xI = 0; xI < pChartData.values.length; xI++){
			//Cria elemento chartvalue
			var xChartValueData = dbsfaces.chartX.pvInitializeAnalizeValuesCreateChartValue(pChartData, xValues[xI], xI);
			if (xMin == null || pChartData.values[xI].value < xMin.value.value){
				xMin = xChartValueData;
			}
			if (xMax == null || pChartData.values[xI].value > xMax.value.value){
				xMax = xChartValueData;
			}
//			xTotalAbs += Math.abs(xValues[xI].value);
			xTotal += pChartData.values[xI].value;
		}
		pChartData.chartValueMin = xMin.self.addClass("-min");
		dbsfaces.chartX.pvInitializeAnalizeValuesMinMax(pChartData, xMin);
		
		pChartData.chartValueMax = xMax.self.addClass("-max");
		dbsfaces.chartX.pvInitializeAnalizeValuesMinMax(pChartData, xMax);
		
		pChartData.min = xTotal / pChartData.values.length;
		pChartData.children = pChartData.chart.children(".dbs_chartValueX");
	},

	
	pvInitializeAnalizeValuesMinMax: function(pChartData, pChartValueData){
		//Alinha a esquerda ou direita com forme a posição do item a partir do centro
		if (pChartValueData.index <= (pChartData.values.Lenght / 2)){
			pChartValueData.self.addClass("-right");
		}else{
			pChartValueData.self.addClass("-left");
		}
	},

	pvInitializeAnalizeValuesCreateChartValue: function(pChartData, pValue, pI){
		
		var xChartValueData = dbsfaces.chartX.pvInitializeAnalizeValuesCreateChartValueData(pValue, pI);

		//Cria ChartValue
		xChartValueData.self = dbsfaces.svg.g(pChartData.chart, "dbs_chartValueX -" + pChartData.type, null, {"index": pI});
		//Cria Elemento que contém infos
		xChartValueData.info = dbsfaces.svg.g(xChartValueData.self, "-info", null, null);
		var xDisplayValue = ((typeof pValue.displayValue == "undefined" || pValue.displayValue == "") ? pValue.value : pValue.displayValue);
		if (pChartData.type == "line"){
			//Box
			xChartValueData.infoBox = dbsfaces.svg.rect(xChartValueData.info, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.point = dbsfaces.svg.circle(xChartValueData.self, null, null, null, "-point", null, {"r": ".5em"}); //'r' precisa ser um atributo por problema no FIREFOX
		}else if (pChartData.type == "bar"){
			//Box
			xChartValueData.infoBox = dbsfaces.svg.rect(xChartValueData.info, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.point = dbsfaces.svg.path(xChartValueData.self, null, "-point", null, null);
		}else if (pChartData.type == "pie"){
			//Box
			xChartValueData.infoBox = dbsfaces.svg.rect(xChartValueData.info, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.point = dbsfaces.svg.path(xChartValueData.self, null, "-point", null, null);
		}
		//Texto do Label
		xChartValueData.infoLabel = dbsfaces.svg.text(xChartValueData.info, null, null, pValue.label, "-label", null, null);
		//Texto do Valor
		xChartValueData.infoValue = dbsfaces.svg.text(xChartValueData.info, null, null, xDisplayValue, "-value", null, null);

		xChartValueData.self.data("data", xChartValueData);

		return xChartValueData;
	},

	pvInitializeAnalizeValuesCreateChartValueData: function(pValue, pI){
		var xChartValueData = {
			self : null, // o próprio chartvalue
			parent : null, //o pai(chart)
			value : pValue, //o objecto value
			index : pI, //index do valor
			point : null, //elemento point
			info : null, //elemento que contém infos
			infoLabel : null, //elemento que contém o label
			infoValue : null, //elemento que contém o value
			infoBox : null, //elemento que contém o box
			x : null, //posição X no gráfico (dentro da escala)
			y : null //posição Y no gráfico (dentro da escala)
		}
		return xChartValueData;
	},
	
	pvInitializeLayout: function(pChartData){
		if (pChartData.type == "line"){
			dbsfaces.chartX.pvInitializeLayoutChartLine(pChartData);
		}
	},

	pvInitializeLayoutChartLine: function(pChartData){
		//Cria elemento que será a linha que conecta pontos
		pChartData.path = dbsfaces.svg.path(pChartData.chart, null, "-path", null, null);
		
		//Captura movimento do mouse para seleciona ponto
		pChartData.self.on("mousemove touchmove touchstart", function(e){
			var xChart = $(this);
			if (xChart.hasClass("-selected")){
				dbsfaces.chartX.findPoint(e, xChart.data("data"));
			}
			e.stopImmediatePropagation();
			return false;
		});
	},

	findPoint: function(e, pChartData){
		var xDecimals = 1;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		var xPosition = pChartData.self.offset();
		var xCurrentX = dbsfaces.math.round(xXY.x - xPosition.left + $(window).scrollLeft(), xDecimals);
		if (xCurrentX < 0){return;}
		var xChartPath = pChartData.path[0];
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
//			var xChartChildren = pChartData.children;
			var xTotalSegs = pChartData.path.svgGetPathTotalSegs();
			var xIndex = xChartPath.getPathSegAtLength(xTargetLenght);
			var xClosestX = xCurrentX;
			var xChartValue = $(pChartData.children[xIndex]);
			var xX = Number(xChartValue.data("x"));
			var xY = Number(xChartValue.data("y"));
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX){
				if (xIndex > 0){
					xClosestX = $(pChartData.children[xIndex - 1]).data("x");
				}
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX){
				if (xIndex < xTotalSegs){
					xClosestX = $(pChartData.children[xIndex + 1]).data("x");
				}
			}
			var xXMiddle = (Number(xClosestX) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValue = $(pChartData.children[xIndex - 1]);
			}
			//Seleciona chartvalue encontrado
			dbsfaces.chartX.select(pChartData, xChartValue);
		}
	},

	select: function(pChartData, pChartValue){
		if (pChartData.selected != null){
			pChartData.selected.removeClass("-selected");
		}else if(pChartData.selected == pChartValue){
			return;
		}
		pChartValue.addClass("-selected");
		pChartData.selected = pChartValue;
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
		pChartData.values.push(xValue);
	},
	
	clearChartValue: function(pChart){
		pChartData.values = [];
	}
};

