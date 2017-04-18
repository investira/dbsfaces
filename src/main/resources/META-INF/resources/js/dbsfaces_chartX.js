dbs_chartX = function(pId, pValues) {
	var xChart = $(pId);
	dbsfaces.chartX.initialize(xChart, pValues);
	
	xChart.on("mouseleave", function(e){
		xChart = $(this);
		//Seleciona chartvalue encontrado
		dbsfaces.chartX.selectChartValue(xChart.data("data"), null);
		e.stopImmediatePropagation();
		return false;
	});
};



dbsfaces.chartX = {
	initialize: function(pChart, pValues){
		var xChartData = dbsfaces.chartX.pvInitializeData(pChart, pValues);
		dbsfaces.chartX.pvInitializeChartValues(xChartData);
		dbsfaces.chartX.pvInitializeLayout(xChartData);
	},

	pvInitializeData: function(pChart, pValues){
		var xCharts = pChart.closest(".dbs_chartsX");
		var xData = {
			dom : {
				self : pChart, //O próprio chart
				parent : xCharts, //Pai
				childrenData : [], //Filhos
				caption : null, //Caption do gráfico
				captionText : null, //Texto do gráfico
				chart : pChart.children(".-chart"), //Chart - SVG
				info : pChart.children(".-info"), //Container das infos
				minChartValueData : null, //chartValue que contém o valor máximo
				maxChartValueData : null, //chartValue que contém o valor mínimo
				path: null, //Desenho do caminho
				links: null, //Links entres os chartvalues
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
			index: 0, //Número do gráfico - Configurado da inicialização do dbschats
			width: null, //largura do gráfico total
			height: null, //largura do gráfico total 
			totalValue: 0, //Somatório para ser utilizado no cálculo do percentual que cada valor representa
			medValue: null, //valor médio
			originalValues: pValues, //Valores recebidos
//			normalizedValuesData: [], //Valores organizados considerando a relação entre eles e ordenados
			labelsGroupCaptions: [], //Títulos dos grupos dos labels
			labelsGroupCount: 0, //Quantidade de grupos de labels
			relationships: [], //Relacionamentos entres os chartvalues
			color: pChart.attr("color"), //Cor definida pelo usuário
			colorInverted: null, //Cor configurada na pvInitializeLayoutColor do dbscharts
			colorLight: null, //Cor configurada na pvInitializeLayoutColor do dbscharts
			currentColorInverted: tinycolor(xCharts.css("color")).invertLightness().setAlpha(1).toString(),
			findPointTimeout: null,
			showDelta: pChart.hasClass("-showDelta"),
//			relationalCaptions: null, //Títulos dos grupos de relacionamento
			globalSequence: 0 //Número sequencial do item do chartValue, considerando todos os gráficos 
		}
		//COnfigura como cor nula quando não tiver sido informada. Posteriormente será calculado uma cor baseada no atributo CSS color(currentColor).
		if (typeof xData.color == "undefined"){
			xData.color = null;
		}
		xData.width = xData.dom.chart[0].getBoundingClientRect().width;
		xData.height = xData.dom.chart[0].getBoundingClientRect().height;
		pChart.data("data", xData);
//		dbsfaces.chartX.addChartValue(pChart, 123);
//		dbsfaces.chartX.clearChartValue(pChart);
		return xData;
	},
	
	pvInitializeCreateNormalizedValues: function(pChartData){
		
		//Cria lista com a relação(e somatório) entre todos os labels do gráfico
		dbsfaces.chartX.pvInitializeCreateNormalizedValuesList(pChartData);
		
		//Ordena por ordem descrescente do somatório
		if (pChartData.type == "pie"){
			pChartData.normalizedValuesData.sort(function(a, b){
				return b.labelGroupIndex - a.labelGroupIndex;
	//		    var x = a.label.toLowerCase();
	//		    var y = b.label.toLowerCase();
	//		    if (x < y) {return -1;}
	//		    if (x > y) {return 1;}
	//		    return 0;
			});
		}
//		pChartData.labelGroupData.forEach(function(pLabelGroupData){
			pChartData.normalizedValuesData.forEach(function(pNormalizedValuesData){
//				if (pLabelGroupData.labelGroupIndex == pNormalizedValuesData.labelGroupIndex){
//					console.log(pChartData.totalValue + "\t" + pNormalizedValuesData.label + "\t" + pNormalizedValuesData.key + "\t" + pNormalizedValuesData.total);
//				}
			});
//		});
	},
	
	pvInitializeChartValues: function(pChartData){
		pChartData.labelGroupData = [];
		if (pChartData.originalValues.length > 0){
			var xMaxChartValueData = null;
			var xMinChartValueData = null;
			var xMed = 0
			pChartData.relationships = [];
			//Loop por todos os valores da originais recebidos
			for (var xI = 0; xI < pChartData.originalValues.length; xI++){
				var xOriginalValue = pChartData.originalValues[xI];
				xOriginalValue.displayValue = ((typeof xOriginalValue.displayValue == "undefined" || xOriginalValue.displayValue == "") ? xOriginalValue.value : xOriginalValue.displayValue);

				//Somatório para ser utilizado posteriormente no cálculo do percentual que cada valor representa
				if (pChartData.type == "pie"){
					pChartData.totalValue += Math.abs(xOriginalValue.value);
				}else{
					pChartData.totalValue += xOriginalValue.value;
				}

				//Quebra grupo de labels
				var xLabels = [];
				if (typeof xOriginalValue.label == "undefined"){
					xLabels.push(xI); //Força que o label seja o index para que sempre exista um label
				}else{
					xLabels = xOriginalValue.label.split(/[;]+/);
				}
				
				//Salva quantidade máxima de grupos de labels existentes
				pChartData.labelsGroupCount = Math.max(pChartData.labelsGroupCount, xLabels.length);

				xOriginalValue.key = ""
				//Lista de chartvalue que estão vinculados ao valor original
				var xRelationalChartValueData = [];
				//Cria um chartValuedata para cada labelgroup e cada label.
				//Valores serão agrupados por labelgroup e label
				xLabels.forEach(function(pLabel, pLabelGroupIndex){
					var xChartValueData = null;
					var xLabel = pLabel.trim();
					//Procura se já existe chartValue com este label no mesmo labelindex 
					for (var xN = 0; xN < pChartData.dom.childrenData.length; xN++){
						if (pChartData.dom.childrenData[xN].labelGroupIndex == pLabelGroupIndex
						 && pChartData.dom.childrenData[xN].label == xLabel){
							xChartValueData = pChartData.dom.childrenData[xN];
							break;
						}
					}

					//Cria componente chartValueData
					if (xChartValueData == null){
						var xChartValueData = dbsfaces.chartX.pvInitializeChartValuesCreateData(pChartData, pLabel, pLabelGroupIndex);
						//Adiciona valor normalizado
						pChartData.dom.childrenData.push(xChartValueData);
					}
					//Salva valor original vinculado a este chartValue
					xChartValueData.originalValues.push(xOriginalValue);
					//Calcula somatório dos valores vinculados a este label
					xChartValueData.value += xOriginalValue.value;
					if (xMinChartValueData == null || xChartValueData.value < xMinChartValueData.value){
						xMinChartValueData = xChartValueData;
					}
					if (xMaxChartValueData == null || xChartValueData.value > xMaxChartValueData.value){
						xMaxChartValueData = xChartValueData;
					}
					//Relação dos chartvaluedata que compoem este valor
					xRelationalChartValueData.push(xChartValueData);
				});
				//Cria string com representação binária dos index dos chartvalues que fazem parte deste originalvalue
				//Cada byte representa um index, iniciando da direita para esquerda. Sendo assim, o byte mais a direita é o index 0.
				//O index de cada chartvalue representa a posição dentro da representação binário: ex: 0101 (os indexes 2 e 0 estão ligados)

				//Cria lista com os pares de relacionamento
				for (var xA = 0; xA < xRelationalChartValueData.length - 1; xA++){
					var xKeyA = dbsfaces.chartX.pvInitializeChartValuesAddKeyToBinaryKey("", xRelationalChartValueData[xA].key);
					for (var xB = xA + 1; xB < xRelationalChartValueData.length; xB++){
						var xKey = dbsfaces.chartX.pvInitializeChartValuesAddKeyToBinaryKey(xKeyA, xRelationalChartValueData[xB].key);
						var xRelationship = null;
						for (var xN = 0; xN < pChartData.relationships.length; xN++){
							if (pChartData.relationships[xN].key == xKey){
								xRelationship = pChartData.relationships[xN];
								break;
							}
						}
						if (xRelationship == null){
							xRelationship = {
											key: xKey, 
											total:0 
											};
							pChartData.relationships.push(xRelationship);
						}
						xRelationship.total += xOriginalValue.value;
					}
				}
			}
			
			//Marca o valor mínimo e máximo
			pChartData.dom.minChartValueData = xMinChartValueData;
			pChartData.dom.maxChartValueData = xMaxChartValueData;
			
			//Calcula valor médio e salva
			pChartData.medValue = (pChartData.totalValue * pChartData.labelsGroupCount) / pChartData.dom.childrenData.length;
			//Ordena por labelgroup e valor
			if (pChartData.type == "pie"){
				pChartData.dom.childrenData.sort(function(a, b){
					var x = a.labelGroupIndex - b.labelGroupIndex;
					if (x == 0){
						x = b.value - a.value;
					}
					return x;
		//		    var x = a.label.toLowerCase();
		//		    var y = b.label.toLowerCase();
		//		    if (x < y) {return -1;}
		//		    if (x > y) {return 1;}
		//		    return 0;
				});
			}

			var xTotalValue = 0;
			var xLabelGrounpIndex = 0;
			//Cria elementos do chartvalueData e configura totalizador e index 
			pChartData.dom.childrenData.forEach(function(pChartValueData, pI){
				if (xLabelGrounpIndex != pChartValueData.labelGroupIndex){
					xTotalValue = 0;
				}
				pChartValueData.index = pI;
				dbsfaces.chartX.pvInitializeChartValuesCreate(pChartData, pChartValueData);
				//Força a exibição do primeiro e último item
				if (xI == 0 || xI == pChartData.dom.childrenData.length - 1){
					pChartValueData.dom.self.addClass("-showLabel");
				}
				//Calcula somatório até este chartvalue
				if (pChartData.type == "pie"){
					xTotalValue += Math.abs(pChartValueData.value);
				}else{
					xTotalValue += pChartValueData.value;
				}
				pChartValueData.totalValue = xTotalValue;
				xLabelGrounpIndex = pChartValueData.labelGroupIndex;
			});
			pChartData.dom.minChartValueData.dom.self.addClass("-min");
			pChartData.dom.maxChartValueData.dom.self.addClass("-max");

			//Configura títulos dos labels
			pChartData.labelsGroupCaptions = new Array(pChartData.labelsGroupCount);
			if (typeof pChartData.dom.self.attr("labelsCaption") != "undefined"){
				var xLabelsCaption = pChartData.dom.self.attr("labelsCaption").split(/[;]+/);
				xLabelsCaption.forEach(function(pLabelCaption, pI){
					pChartData.labelsGroupCaptions[pI] = pLabelCaption.trim();
				});
			}
		}
	},

	pvInitializeChartValuesAddKeyToBinaryKey: function(pBinaryKey, pIndex){
		var xLengthDif = pIndex - pBinaryKey.length + 1; 
		if (xLengthDif > 0){
			pBinaryKey = "0".repeat(xLengthDif) + pBinaryKey;
		}
		var xStart = pBinaryKey.length - pIndex - 1;
		var xEnd = xStart + 1;
		pBinaryKey = pBinaryKey.substr(0, xStart) + "1" + pBinaryKey.substring(xEnd); 
		return pBinaryKey;
	},
//
//	pvInitializeChartValuesAddKeyToBinaryKey2: function(pKeyA, pKeyB){
//		var xKey1;
//		var xKey2;
//		if (pKeyA.length > pKeyB.length){
//			xKey1 = pKeyA;
//			xKey2 = pKeyB;
//		}else{
//			xKey1 = pKeyB;
//			xKey2 = pKeyA;
//		}
//		var xKey = "";
//		for (var xA = xKey1.length - 1; xA > -1; xA--){
//			if (xKey1.charAt(xA) == "1"
//			 || xKey2.charAt(xA) == "1"){
//				xKey = "1"+ xKey;
//			}else{
//				xKey = "0"+ xKey
//			}
//		}
//		return xKey;
//	},
	
	pvInitializeChartValuesCreateData: function(pChartData, pLabel, pLabelGroupIndex){
		var xChartValueData = {
			dom : {
				self : null, // o próprio chartvalue
				parent : pChartData.dom.self,  //o pai(chart)
				point : null, //elemento point
				info : null, //elemento que contém infos
				infoValues : null, //elemento que contém os elementos dos textos do info(somente usado no chartpie)
				infoLabel : null, //elemento que contém o label
				infoLabelBox : null, //elemento que contém o box do Label
				infoValue : null, //elemento que contém o value
				infoValueBox : null, //elemento que contém o box do Value
				infoPath : null, //elemento que contém o caminho do label e value até o point
				infoPerc : null, //elemento que contém o valor percentual no chartpie
				infoPercInt : null, //elemento que contém o inteiro do valor percentual no chartpie
				infoPercDec : null, //elemento que contém o decimal do valor percentual no chartpie
				infoPercBox : null //elemento que contém o box do perc
			},
			key : pChartData.dom.childrenData.length, //Chave sequencial binária
			index : null, //Index - //Chave sequencial será atribuita após o sort 
			value : 0, //somatótio dos valores que possuem o mesmo label
			label : pLabel.trim(), //Label já desmenbrado do group label
			labelGroupIndex: pLabelGroupIndex, //Index em relação ao label quando houver mais de uma
			originalValues : [], //valores originals que agrupados neste chartvalue
			x : null, //posição X no gráfico (dentro da escala)
			y : null, //posição Y no gráfico (dentro da escala)
			color: null, //Cor do valor,
			perc: null, //Percentual que valor representa sobre o total
			totalValue: 0, //Total até este chartvalue
			globalSequence: 0 //Número sequencial do item do chartValue, considerando todos os gráficos 
		}
		return xChartValueData;
	},



	pvInitializeChartValuesCreate: function(pChartData, pChartValueData){
//		var xChartValueData = dbsfaces.chartX.pvInitializeChartValuesCreateData(pChartData, pLabel, pLabelGroupIndex);
		//Cria ChartValue
		pChartValueData.dom.self = dbsfaces.svg.g(pChartData.dom.chart, "dbs_chartValueX -" + pChartData.type, null, {index: pChartValueData.index, labelGroupIndex: pChartValueData.labelGroupIndex});
		//Salva data
		pChartValueData.dom.self.data("data", pChartValueData);
		//Cria Elemento que contém infos
		pChartValueData.dom.info = dbsfaces.svg.g(pChartValueData.dom.self, "-info", null, null);
		if (pChartData.type == "line"){
			//Ponto
			pChartValueData.dom.point = dbsfaces.svg.circle(pChartValueData.dom.self, null, null, null, "-point", null, {r:".3em"}); //'r' precisa ser um atributo por problema no FIREFOX
			//Path
			pChartValueData.dom.infoPath = dbsfaces.svg.path(pChartValueData.dom.info, null, "-path", null, null);
			//LabelBox
			pChartValueData.dom.infoLabelBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-labelBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//valueBox
			pChartValueData.dom.infoValueBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-valueBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Texto do Label
			pChartValueData.dom.infoLabel = dbsfaces.svg.text(pChartValueData.dom.info, null, null, pChartValueData.label, "-label", null, null);
			//Texto do Valor
			pChartValueData.dom.infoValue = dbsfaces.svg.text(pChartValueData.dom.info, null, null, pChartValueData.value, "-value", null, null);
		}else if (pChartData.type == "bar"){
			//Box
			pChartValueData.dom.infoLabelBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-labelBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Ponto
			pChartValueData.dom.point = dbsfaces.svg.path(pChartValueData.dom.self, null, "-point", null, null);
		}else if (pChartData.type == "pie"){
			//Ponto
			pChartValueData.dom.point = dbsfaces.svg.path(pChartValueData.dom.self, null, "-point", null, {stroke:"currentColor", fill:"none"});
			//Path
			pChartValueData.dom.infoPath = dbsfaces.svg.path(pChartValueData.dom.self, null, "-path", null, null);
			//Container do value
			pChartValueData.dom.infoValues = dbsfaces.svg.g(pChartValueData.dom.info, "-values", null, null);
			//Texto do Label
			pChartValueData.dom.infoLabel = dbsfaces.svg.text(pChartValueData.dom.infoValues, "3.3em", "-.2em", pChartValueData.label, "-label", null, null);
			//Texto do Valor
			pChartValueData.dom.infoValue = dbsfaces.svg.text(pChartValueData.dom.infoValues, "3.3em", ".8em", pChartValueData.value, "-value", null, null);
			//BoxPerc
			pChartValueData.dom.infoPercBox = dbsfaces.svg.rect(pChartValueData.dom.infoValues, ".2em", "-1em", "3em", "2em", ".2em", ".2em", "-percBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Texto do Perc
			pChartValueData.dom.infoPerc = dbsfaces.svg.text(pChartValueData.dom.infoValues, ".4em", ".2em", null, "-perc", null, null);
			//Texto dos Inteiros do Perc
			pChartValueData.dom.infoPercInt = dbsfaces.svg.tspan(pChartValueData.dom.infoPerc, null, "-int", null, null);
			//Texto dos Decimais do Perc
			pChartValueData.dom.infoPercDec = dbsfaces.svg.tspan(pChartValueData.dom.infoPerc, null, "-dec", null, null);
		}
		//Captura movimento do mouse para seleciona ponto
		if (pChartData.type == "pie"){
			pChartValueData.dom.self.on("mousemove touchmove touchstart", function(e){
				xChartValueData = $(this).data("data");
				//Seleciona chartvalue encontrado
				dbsfaces.chartX.selectChartValue(pChartData, xChartValueData);
				e.stopImmediatePropagation();
				return false;
			});
		}
	},
	
	pvInitializeLayout: function(pChartData){
		if (pChartData.type == "line"){
			dbsfaces.chartX.pvInitializeLayoutChartLine(pChartData);
		}else if (pChartData.type == "pie"){
			dbsfaces.chartX.pvInitializeLayoutChartPie(pChartData);
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
		return xData;
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
		if (pChartData.type == "line"){
			pChartData.dom.self.on("mousemove touchmove touchstart", function(e){
				var xChart = $(this);
				if (xChart.hasClass("-selected")){
					//Timeout para diminuir a quantidade de chamada
					clearTimeout(pChartData.findPointTimeout);
					pChartData.findPointTimeout = setTimeout(function(){
						dbsfaces.chartX.findPoint(e, xChart.data("data"));
					},5);
				}
				e.stopImmediatePropagation();
				return false;
			});
		}
	},

	pvInitializeLayoutChartPie: function(pChartData){
		//Cria elemento que será a linha que conecta os pontos
		pChartData.dom.links = dbsfaces.svg.g(pChartData.dom.chart, "-links", null, null);
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
			var xChartValueData = pChartData.dom.childrenData[xIndex];
			var xX = Number(xChartValueData.x);
			var xY = Number(xChartValueData.y);
			//Se cursos estiver antes do ponto, seleciona o chartvalue anterior
			if (xCurrentX < xX && xIndex > 0){
				xClosestX = pChartData.dom.childrenData[xIndex - 1].x;
			//Se cursos não estiver após do ponto, seleciona o chartvalue posterior
			}else if(xCurrentX > xX && xIndex < (xTotalSegs - 1)){
				xClosestX = pChartData.dom.childrenData[xIndex + 1].x;
			}
			var xXMiddle = (Number(xClosestX) + xX) / 2;
			//Escolhe o item anterior se estiver antes do meio do caminho entre o próximo item
			if (xCurrentX < xXMiddle){
				xChartValueData = pChartData.dom.childrenData[xIndex - 1];
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
		//Remove hover anterios
		if (pOldChartValueData != null){
			if (pChartValueData != null 
			 && pOldChartValueData == pChartValueData){
				return pChartValueData;
			}else{
				pOldChartValueData.dom.self.removeClass("-hover");
				//Esconde links entre os chartvalues
				if (pChartData.type == "pie"){
					pChartData.dom.self.find("> .-chart > .-links").children().removeClass("-hover");
				}	
			}
		}
		//Ativa hover atual
		if (pChartValueData != null){
			pChartValueData.dom.self.addClass("-hover");
			//Move chartvalue para a frente de todos os outros
			dbsfaces.ui.moveToFront(pChartValueData.dom.self);
			//Exibe links entre os chartvalues
			if (pChartData.type == "pie"){
				var xLinks = pChartData.dom.self.find("> .-chart > .-links > [a='" + pChartValueData.key + "']");
				xLinks.addClass("-hover");
				xLinks.svgAttr("stroke", pChartValueData.dom.self.css("color"));
				xLinks = pChartData.dom.self.find("> .-chart > .-links > [b='" + pChartValueData.key + "']");
				xLinks.addClass("-hover");
				xLinks.svgAttr("stroke", pChartValueData.dom.self.css("color"));
			}
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
		var xLeftValue = pChartData.dom.leftDeltaHandleData.dom.chartValueData.value;
		var xRightValue = pChartData.dom.rightDeltaHandleData.dom.chartValueData.value;
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

