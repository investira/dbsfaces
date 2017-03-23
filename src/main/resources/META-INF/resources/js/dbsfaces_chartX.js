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
				caption : null, //Caption do gráfico
				captionText : null, //Texto do gráfico
				chart : pChart.children(".-chart"), //Chart - SVG
				info : pChart.children(".-info"), //Container das infos
				chartValueMin : null, //chartValue que contém o valor máximo
				chartValueMax : null, //chartValue que contém o valor mínimo
				path: null, //Desenho do caminho
				hoverChartValueData: null, //ChartValue atualmente com hover  
				delta: null, //Container do delta
				deltaHandle1Data: null, // DataHandle 1
				deltaHandle2Data: null, // DataHandle 2
				deltaValue: null, //Texto do valor + label do delta
				deltaValueText: null, //Texto do valor do delta
				deltaValueLabel: null, //Texto do label do delta
				movingDeltaHandleData: null, //Handle que está em movimento(selecionado pelo usuário)
				leftDeltaHandleData: null, //Handle mais a esquerda
				rightDeltaHandleData: null //Handle mais a direita
			},	
			type: xCharts.attr("type"), //Tipo de gráfico
			width: null, //largura do gráfico total
			height: null, //largura do gráfico total 
			med: null, //valor médio
			originalValues: pValues, //Valores recebidos
			color: pChart.attr("color"), //Cor definida pelo usuário
			colorInverted: null,
			colorLight: null,
			currentColorInverted: tinycolor(xCharts.css("color")).invertLightness().setAlpha(1).toString(),
			findPointTimeout: null,
			showDelta: pChart.hasClass("-showDelta")
		}
		if (typeof xData.color == "undefined"){
			xData.color = tinycolor(pChart.css("color")).toString();
		}
		xData.width = xData.dom.chart[0].getBoundingClientRect().width;
		xData.height = xData.dom.chart[0].getBoundingClientRect().height;
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
			//Força a exibição do primeiro e último item
			if (xI == 0 || xI == xValues.length - 1){
				xChartValueData.dom.self.addClass("-showLabel");
			}
//			xTotalAbs += Math.abs(xValues[xI].value);
			xTotal += xValues[xI].value;
			//Adiciona elemento chartvalue na lista de filhos do chart.
			pChartData.dom.children.push(xChartValueData.dom.self);  
		}
		//Marca o valor mínimo e máximo
		pChartData.dom.chartValueMin = xChartValueMin.dom.self.addClass("-min");
		pChartData.dom.chartValueMax = xChartValueMax.dom.self.addClass("-max");
		
		//Calcula valor médio e salva
		pChartData.med = xTotal / xValues.length;
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
				infoPath : null, //elemento que contém o value
				infoBoxLabel : null, //elemento que contém o box do Label
				infoBoxValue : null //elemento que contém o box do Value
			},
			value : pValue, //o objecto value
			index : pI, //index do valor
			x : null, //posição X no gráfico (dentro da escala)
			y : null //posição Y no gráfico (dentro da escala)
		}
		return xChartValueData;
	},

	pvInitializeAnalizeValuesCreateChartValue: function(pChartData, pValue, pI){
		var xChartValueData = dbsfaces.chartX.pvInitializeAnalizeValuesCreateChartValueData(pValue, pI);
		//Cria ChartValue
		xChartValueData.dom.self = dbsfaces.svg.g(pChartData.dom.chart, "dbs_chartValueX -" + pChartData.type, null, {index: pI});
		//Cria Elemento que contém infos
		xChartValueData.dom.info = dbsfaces.svg.g(xChartValueData.dom.self, "-info", null, null);
		var xDisplayValue = ((typeof pValue.displayValue == "undefined" || pValue.displayValue == "") ? pValue.value : pValue.displayValue);
		if (pChartData.type == "line"){
			//Path
			xChartValueData.dom.infoPath = dbsfaces.svg.path(xChartValueData.dom.info, null, "-path", null, null);
			//BoxLabel
			xChartValueData.dom.infoBoxLabel = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-boxLabel", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//BoxValue
			xChartValueData.dom.infoBoxValue = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-boxValue", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.dom.point = dbsfaces.svg.circle(xChartValueData.dom.self, null, null, null, "-point", null, {r:".3em"}); //'r' precisa ser um atributo por problema no FIREFOX
		}else if (pChartData.type == "bar"){
			//Box
			xChartValueData.dom.infoBoxLabel = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-boxLabel", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			xChartValueData.dom.point = dbsfaces.svg.path(xChartValueData.dom.self, null, "-point", null, null);
		}else if (pChartData.type == "pie"){
			//Box
			xChartValueData.dom.infoBoxLabel = dbsfaces.svg.rect(xChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-boxLabel", null, null); //'r' precisa ser um atributo por problema no FIREFOX
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
	
	pvInitializeLayout: function(pChartData){
		if (pChartData.type == "line"){
			dbsfaces.chartX.pvInitializeLayoutChartLine(pChartData);
		}
	},
	
	pvInitializeLayoutChartLineDeltaHandle: function(pChartData, pHandleNumber){
		var xDeltaHandle = dbsfaces.svg.g(pChartData.dom.delta, "-handle", null, {handle:pHandleNumber});
		return dbsfaces.chartX.pvInitializeLayoutChartLineDeltaHandleData(pChartData, xDeltaHandle, pHandleNumber);
	},
	
	pvInitializeLayoutChartLineDeltaHandleData: function(pChartData, pDeltaHandle, pHandleNumber){
		var xData = {
			dom : {
				self: pDeltaHandle,
				rect: dbsfaces.svg.rect(pDeltaHandle, null, null, null, null, null, null, "-rect", null, null),
				handle: dbsfaces.chartX.pvInitializeLayoutChartLineCreateHandle(pChartData, pDeltaHandle),
				chartValueData: null
			},
			number: pHandleNumber
		}
		pDeltaHandle.data("data", xData);
		return pDeltaHandle.data("data");
	},


	pvInitializeLayoutChartLineCreateHandle: function(pChartData, pDeltaHandle){
//		var xDeltaHandleHandle = dbsfaces.svg.svg(pDeltaHandle, null, null, "16", "16", "-handle", "overflow: visible;", {viewBox:"0 0 16 16"});
		var xDeltaHandleHandle = dbsfaces.svg.svg(pDeltaHandle, null, null, "1.5em", "1.5em", "-handle", "overflow: visible;", {viewBox:"0 0 16 16"});
		dbsfaces.svg.circle(xDeltaHandleHandle, "0", "0", null, "-touch", null, {r:"16", fill:"transparent"});
		dbsfaces.svg.rect(xDeltaHandleHandle, "-3", "-8", "6", "16", "2", "2", "-rect", null, {fill:pChartData.currentColorInverted, stroke:"currentColor", "stroke-width":"1px"});
		dbsfaces.svg.line(xDeltaHandleHandle, "-1", "-4", "-1", "4", "-line", null, {stroke:"currentColor", "stroke-width":"1px"});
		dbsfaces.svg.line(xDeltaHandleHandle, "1", "-4", "1", "4", "-line", null, {stroke:"currentColor", "stroke-width":"1px"});
		
		//Captura eventos para mover handle
		pDeltaHandle.on("mousedown touchstart", function(e){
			dbsfaces.chartX.setMovingDeltaHandleData(pChartData, $(this).data("data"));
			e.stopImmediatePropagation();
			return false;
		});
		pDeltaHandle.on("mousemove touchmove", function(e){
			if (e.originalEvent.type == "mousemove" 
			 && e.which == 0){
				dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
				return;
			}
			dbsfaces.chartX.findPoint(e, pChartData);
			e.stopImmediatePropagation();
			return false;
		});	
		pChartData.dom.self.on("mouseup touchend", function(e){
			dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
			e.stopImmediatePropagation();
			return false;
		});
		pChartData.dom.self.on("mouseleave", function(e){
			dbsfaces.chartX.setMovingDeltaHandleData(pChartData, null);
		});
		return xDeltaHandleHandle;
	},
	
	pvInitializeLayoutChartLine: function(pChartData){
		//Cria elemento que será a linha que conecta os pontos
		pChartData.dom.path = dbsfaces.svg.path(pChartData.dom.chart, null, "-path", "stroke:" + pChartData.color, null);
		dbsfaces.ui.moveToBack(pChartData.dom.path);

		//Cria elementos da guia do delta
		if (pChartData.showDelta){
			pChartData.dom.delta = dbsfaces.svg.g(pChartData.dom.chart, "-delta", null, null);
			//Value
			pChartData.dom.deltaValue = dbsfaces.svg.text(pChartData.dom.delta, null, null, null, "-value", null, {fill:pChartData.color});
			pChartData.dom.deltaValueText = dbsfaces.svg.tspan(pChartData.dom.deltaValue, "0", "-text", null, null);
			pChartData.dom.deltaValueLabel = dbsfaces.svg.tspan(pChartData.dom.deltaValue, "%", "-label", null, null);
			//Guia 1
			pChartData.deltaHandle1Data = dbsfaces.chartX.pvInitializeLayoutChartLineDeltaHandle(pChartData, 1);
			pChartData.dom.leftDeltaHandleData = pChartData.deltaHandle1Data;
			//Guia 2
			pChartData.deltaHandle2Data = dbsfaces.chartX.pvInitializeLayoutChartLineDeltaHandle(pChartData, 2);
			pChartData.dom.rightDeltaHandleData = pChartData.deltaHandle2Data;
		}
		
		//Captura movimento do mouse para seleciona ponto
		pChartData.dom.self.on("mousemove touchmove touchstart", function(e){
			var xChart = $(this);
			if (xChart.hasClass("-selected")
			 && xChart.hasClass("-line")){
				//Timeout para diminuir a quantidade de chamada
				clearTimeout(pChartData.findPointTimeout);
				pChartData.findPointTimeout = setTimeout(function(){
					dbsfaces.chartX.findPoint(e, xChart.data("data"));
				},5);
			}
			e.stopImmediatePropagation();
			return false;
		});
	},


	//Procura ponto da caminho(path)
	findPoint: function(e, pChartData){
		var xDecimals = 1;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		var xPosition = pChartData.dom.self.offset();
		var xCurrentX = dbsfaces.math.round(xXY.x - xPosition.left + $(window).scrollLeft() - parseFloat(pChartData.dom.self.css("padding-left")), xDecimals);
//		var xCurrentX = dbsfaces.math.round(xXY.x - xPosition.left, xDecimals);
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
			var xTotalSegs = pChartData.dom.path.svgGetPathTotalSegs();
			var xIndex = xChartPath.getPathSegAtLength(xTargetLenght);
			var xClosestX = xCurrentX;
			var xChartValueData = $(pChartData.dom.children[xIndex]).data("data");
			var xX = Number(xChartValueData.x);
			var xY = Number(xChartValueData.y);
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX && xIndex > 0){
				xClosestX = $(pChartData.dom.children[xIndex - 1]).data("data").x;
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX && xIndex < (xTotalSegs - 1)){
				xClosestX = $(pChartData.dom.children[xIndex + 1]).data("data").x;
			}
			var xXMiddle = (Number(xClosestX) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValueData = $(pChartData.dom.children[xIndex - 1]).data("data");
			}
			//Seleciona chartvalue encontrado
			dbsfaces.chartX.selectChartValue(pChartData, xChartValueData);
		}
	},
	
	selectChartValue: function(pChartData, pChartValueData){
		//Posiciona Handle
		if (pChartData.type == "line"){
			if (pChartData.showDelta){
				dbsfaces.chartX.pvShowDelta(pChartData, pChartValueData);
				return;
			}
		}
		//Seleciona chartvalue encontrado
		pChartData.dom.hoverChartValueData = dbsfaces.chartX.pvHover(pChartData, pChartValueData, pChartData.dom.hoverChartValueData);
	},
	

	setMovingDeltaHandleData: function(pChartData, pDeltaHandleData){
		//Indica se handle está em movimento
		if (pDeltaHandleData == null){
			if (pChartData.dom.movingDeltaHandleData != null){
				pChartData.dom.self.removeClass("-moving");
				pChartData.dom.movingDeltaHandleData.dom.self.removeClass("-selected");
			}
		}else{
			pChartData.dom.self.addClass("-moving");
			pDeltaHandleData.dom.self.addClass("-selected");
		}
		//Salva qual o delta handle esta sendo movimentado
		pChartData.dom.movingDeltaHandleData = pDeltaHandleData;
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
	},
	
	
	pvHover: function(pChartData, pChartValueData, pOldChartValueData){
		if (pOldChartValueData != null){
			if (pChartValueData != null 
			 && pOldChartValueData == pChartValueData){
				return pChartValueData;
			}else{
				pOldChartValueData.dom.self.removeClass("-hover");
			}
		}
		if (pChartValueData != null){
			pChartValueData.dom.self.addClass("-hover");
			//Move chartvalue para a frente de todos os outros
			dbsfaces.ui.moveToFront(pChartValueData.dom.self);
		}
		return pChartValueData;
	},
	
	pvShowDelta: function(pChartData, pChartValueData){
		if (pChartData.dom.movingDeltaHandleData == null){return;}
		var xChartsData = pChartData.dom.parent.data("data"); 
		if (xChartsData == null
		 || !xChartsData.showDelta){return;}
		
		var xX;
		var xWidth;
		var xTrocou = false;
		//Retira hover de ambos para recolocar posteriormente. Isto evita apagar hover quando left e right estão no mesmo ponto
		dbsfaces.chartX.pvHover(pChartData, null, pChartData.dom.rightDeltaHandleData.dom.chartValueData);
		dbsfaces.chartX.pvHover(pChartData, null, pChartData.dom.leftDeltaHandleData.dom.chartValueData);
		
		//Salva qual o chartvaluedata está vinculado ao handle
		pChartData.dom.movingDeltaHandleData.dom.chartValueData = pChartValueData;
		//Se for selecionado o handle a direita, mas a posição selecionada estiver mais a esquerda do que handle a esquerda,
		//Seta o handle a esquerda corrente como sendo a esquerda
		//e o handle a direita passa a ser o handle a esquerda. 
		if (pChartData.dom.movingDeltaHandleData == pChartData.dom.rightDeltaHandleData){
			if (pChartValueData.x < parseFloat(pChartData.dom.leftDeltaHandleData.dom.handle.svgAttr("x"))){
				pChartData.dom.rightDeltaHandleData = pChartData.dom.leftDeltaHandleData;
				pChartData.dom.leftDeltaHandleData = pChartData.dom.movingDeltaHandleData;
			}
		//Vice-versa quando selecionado o handle a esquerda, conforme explicação acima.
		}else{
			if (pChartValueData.x > parseFloat(pChartData.dom.rightDeltaHandleData.dom.handle.svgAttr("x"))){
				pChartData.dom.leftDeltaHandleData = pChartData.dom.rightDeltaHandleData;
				pChartData.dom.rightDeltaHandleData = pChartData.dom.movingDeltaHandleData;
			}
		}
		//Configura posição e tamanho do rect
		if (pChartData.dom.leftDeltaHandleData.dom.chartValueData != null){
			pChartData.dom.leftDeltaHandleData.dom.rect.svgAttr("width", pChartData.dom.leftDeltaHandleData.dom.chartValueData.x - xChartsData.infoWidth);
			pChartData.dom.leftDeltaHandleData.dom.rect.svgAttr("x", xChartsData.infoWidth);
			//Seta hover
			dbsfaces.chartX.pvHover(pChartData, pChartData.dom.leftDeltaHandleData.dom.chartValueData, null);
		}
		if (pChartData.dom.rightDeltaHandleData.dom.chartValueData != null){
			pChartData.dom.rightDeltaHandleData.dom.rect.svgAttr("width", xChartsData.width - pChartData.dom.rightDeltaHandleData.dom.chartValueData.x + xChartsData.infoWidth);
			pChartData.dom.rightDeltaHandleData.dom.rect.svgAttr("x", pChartData.dom.rightDeltaHandleData.dom.chartValueData.x);
			//Seta hover
			dbsfaces.chartX.pvHover(pChartData, pChartData.dom.rightDeltaHandleData.dom.chartValueData, null);
		}
		//Configura posição do handle
		pChartData.dom.movingDeltaHandleData.dom.handle.svgAttr("x", pChartValueData.x);
		//Exibe valor do delta
		dbsfaces.chartX.pvShowDeltaValue(pChartData);
	},
	
	pvShowDeltaValue: function(pChartData){
		var xValue = dbsfaces.chartX.pvCalcDelta(pChartData);
		if (xValue == null){
			pChartData.dom.deltaValueText.text("(na)");
			pChartData.dom.deltaValueLabel.text("");
		}else{
			pChartData.dom.deltaValueText.text(xValue);
			pChartData.dom.deltaValueLabel.text("%");
		}
	},

	pvCalcDelta: function(pChartData){
		if (pChartData.dom.rightDeltaHandleData.dom.chartValueData == null
		 || pChartData.dom.rightDeltaHandleData.dom.chartValueData == null){
			return null; 
		}
		var xLeftValue = pChartData.dom.leftDeltaHandleData.dom.chartValueData.value.value;
		var xRightValue = pChartData.dom.rightDeltaHandleData.dom.chartValueData.value.value;
		var xChartsData = pChartData.dom.parent.data("data");
		if (xLeftValue == 0
		 || xRightValue == 0
		 || Math.sign(xLeftValue) != Math.sign(xRightValue)
		 || xChartsData == null){
			return null;
		}
		var xValue;
		if (xChartsData.isPerc){
			xValue = (xRightValue - xLeftValue);
			xValue *= 100;
		}else{
			if (xLeftValue < 0){
				xValue = (xLeftValue / xRightValue);
			}else{
				xValue = (xRightValue / xLeftValue);;
			}
			xValue = dbsfaces.math.round(xValue, 4);
			xValue = (xValue - 1) * 100;
		}
		return dbsfaces.format.number(xValue, 2);
	}

};

