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
			dom : {
				self : pChart, //O próprio chart
				parent : xCharts, //Pai
				children : null, //Filhos
				chart : pChart.children(".-chart"), //Container dos filhos
				info :pChart.children(".-info"), //Container das infos
				chartValueMin : null, //chartValue que contém o valor máximo
				chartValueMax : null, //chartValue que contém o valor mínimo
				path: null, //Elemento que contém o desenho do caminho
				chartValueHover: null //ChartValue com hover 
			},	
			type: xCharts.attr("type"), //Tipo de gráfico
			med: null, //valor médio
			originalValues: pValues //Valores recebidos
		}
		pChart.data("data", xData);
//		dbsfaces.chartX.addChartValue(pChart, 123);
//		dbsfaces.chartX.clearChartValue(pChart);
	},

	pvInitializeAnalizeValues: function(pChartData){
		var xValues = pChartData.originalValues;
		var xChartValueMax = null;
		var xChartValueMin = null;
		var xMed = 0
//		var xTotalAbs = 0;
		var xTotal = 0;
		pChartData.dom.children = [];
		//Varifica valores máximos e mínimos e cria elemento do valor
		for (var xI = 0; xI < xValues.length; xI++){
			//Cria elemento chartvalue
			var xChartValueData = dbsfaces.chartX.pvInitializeAnalizeValuesCreateChartValue(pChartData, xValues[xI], xI);
			if (xChartValueMin == null || xValues[xI].value < xChartValueMin.value.value){
				xChartValueMin = xChartValueData;
			}
			if (xChartValueMax == null || xValues[xI].value > xChartValueMax.value.value){
				xChartValueMax = xChartValueData;
			}
//			xTotalAbs += Math.abs(xValues[xI].value);
			xTotal += xValues[xI].value;
			//Adiciona elemento chartvalue na lista de filhos do chart.
			pChartData.dom.children.push(xChartValueData.dom.self);  
		}
		//Marca o valor mínimo
		pChartData.dom.chartValueMin = xChartValueMin.dom.self.addClass("-min");
		dbsfaces.chartX.pvInitializeAnalizeValuesMinMax(pChartData, xChartValueMin);
		
		//Marca o valor máximo
		pChartData.dom.chartValueMax = xChartValueMax.dom.self.addClass("-max");
		dbsfaces.chartX.pvInitializeAnalizeValuesMinMax(pChartData, xChartValueMax);
		
		//Calcula valor médio e salva
		pChartData.med = xTotal / xValues.length;
	},

	
	pvInitializeAnalizeValuesMinMax: function(pChartData, pChartValueData){
		//Alinha a esquerda ou direita com forme a posição do item a partir do centro
		if (pChartValueData.index <= (pChartData.originalValues.length / 2)){
			pChartValueData.dom.self.addClass("-right");
		}else{
			pChartValueData.dom.self.addClass("-left");
		}
		dbsfaces.ui.moveToFront(pChartValueData.dom.self);
	},

	pvInitializeAnalizeValuesCreateChartValue: function(pChartData, pValue, pI){
		var xChartValueData = dbsfaces.chartX.pvInitializeAnalizeValuesCreateChartValueData(pValue, pI);
		//Cria ChartValue
		xChartValueData.dom.self = dbsfaces.svg.g(pChartData.dom.chart, "dbs_chartValueX -" + pChartData.type, null, {"index": pI});
		//Cria Elemento que contém infos
		xChartValueData.dom.info = dbsfaces.svg.g(xChartValueData.dom.self, "-info", null, null);
		var xDisplayValue = ((typeof pValue.displayValue == "undefined" || pValue.displayValue == "") ? pValue.value : pValue.displayValue);
		if (pChartData.type == "line"){
			//Box
			xChartValueData.dom.infoBox = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.dom.point = dbsfaces.svg.circle(xChartValueData.dom.self, null, null, null, "-point", null, {"r": ".4em"}); //'r' precisa ser um atributo por problema no FIREFOX
		}else if (pChartData.type == "bar"){
			//Box
			xChartValueData.dom.infoBox = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.dom.point = dbsfaces.svg.path(xChartValueData.dom.self, null, "-point", null, null);
		}else if (pChartData.type == "pie"){
			//Box
			xChartValueData.dom.infoBox = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".3em", ".3em", "-box", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.dom.point = dbsfaces.svg.path(xChartValueData.dom.self, null, "-point", null, null);
		}
		//Texto do Label
		xChartValueData.dom.infoLabel = dbsfaces.svg.text(xChartValueData.dom.info, null, null, pValue.label, "-label", null, null);
		//Texto do Valor
		xChartValueData.dom.infoValue = dbsfaces.svg.text(xChartValueData.dom.info, null, null, xDisplayValue, "-value", null, null);

		xChartValueData.dom.self.data("data", xChartValueData);

		return xChartValueData;
	},

	pvInitializeAnalizeValuesCreateChartValueData: function(pValue, pI){
		var xChartValueData = {
			dom : {
				self : null, // o próprio chartvalue
				parent : null,  //o pai(chart)
				point : null, //elemento point
				info : null, //elemento que contém infos
				infoLabel : null, //elemento que contém o label
				infoValue : null, //elemento que contém o value
				infoBox : null //elemento que contém o box
			},
			value : pValue, //o objecto value
			index : pI, //index do valor
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
		pChartData.dom.path = dbsfaces.svg.path(pChartData.dom.chart, null, "-path", null, null);
		dbsfaces.ui.moveToBack(pChartData.dom.path);

		
		//Captura movimento do mouse para seleciona ponto
		pChartData.dom.self.on("mousemove touchmove touchstart", function(e){
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
		var xPosition = pChartData.dom.self.offset();
		var xCurrentX = dbsfaces.math.round(xXY.x - xPosition.left + $(window).scrollLeft(), xDecimals);
		if (xCurrentX < 0){return;}
		var xChartPath = pChartData.dom.path[0];
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
			var xTotalSegs = pChartData.dom.path.svgGetPathTotalSegs();
			var xIndex = xChartPath.getPathSegAtLength(xTargetLenght);
			var xClosestX = xCurrentX;
			var xChartValueData = $(pChartData.dom.children[xIndex]).data("data");
			var xX = Number(xChartValueData.x);
			var xY = Number(xChartValueData.y);
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX){
				if (xIndex > 0){
					xClosestX = $(pChartData.dom.children[xIndex - 1]).data("data").x;
				}
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX){
				if (xIndex < xTotalSegs){
					xClosestX = $(pChartData.dom.children[xIndex + 1]).data("data").x;
				}
			}
			var xXMiddle = (Number(xClosestX) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValueData = $(pChartData.dom.children[xIndex - 1]).data("data");
			}
			//Seleciona chartvalue encontrado
			dbsfaces.chartX.hover(pChartData, xChartValueData);
		}
	},

	hover: function(pChartData, pChartValueData){
		if (pChartData.dom.chartValueHover != null){
			pChartData.dom.chartValueHover.removeClass("-hover");
		}else if(pChartData.dom.chartValueHover == pChartValueData.dom.self){
			return;
		}
		pChartValueData.dom.self.addClass("-hover");
		pChartData.dom.chartValueHover = pChartValueData.dom.self;
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
		pChartData.originalValues.push(xValue);
	},
	
	clearChartValue: function(pChart){
		pChartData.originalValues = [];
	}
};

