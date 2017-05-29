dbs_slider = function(pId, pValuesList, pMinValue, pMaxValue) {
	dbsfaces.slider.initialize($(pId), pValuesList, pMinValue, pMaxValue);

	$(window).resize(function(e){
		dbsfaces.slider.resize($(pId).data("data"));
	});
	
	if ($(pId).hasClass("-readOnly")){return;}
	if ($(pId).hasClass("-disabled")){return;}
	
	$(pId + " > .-container").on("mousedown touchstart", function(e){
		dbsfaces.slider.jump($(pId).data("data"), e);
		dbsfaces.slider.handleStart($(pId).data("data"), e);
	});
	$(pId + " > .-container").on("mouseup touchend", function(e){
		dbsfaces.slider.handleStop($(pId).data("data"), e);
	});
	$(pId + " > .-container").on("mouseleave", function(e){
		dbsfaces.slider.handleStop($(pId).data("data"), e);
	});
	$(pId + " > .-container").on("mousemove touchmove", function(e){
		if ($(pId).data("data").startPos == null){return false;}
		if (e.originalEvent.type == "mousemove" 
		 && e.which == 0){
			dbsfaces.slider.handleStop($(pId).data("data"), e);
			return;
		}
		dbsfaces.slider.handleMove($(pId).data("data"), e);
	});	
	$(pId).on("mouseleave", function(e){
		dbsfaces.slider.handleStop($(pId).data("data"), e);
	});
}

dbsfaces.slider = {
	initialize: function(pSlider, pValuesList, pMinValue, pMaxValue){
		dbsfaces.slider.pvInitializeData(pSlider, pValuesList, pMinValue, pMaxValue);
		dbsfaces.slider.pvInitializeCreatePoints(pSlider.data("data"));
		dbsfaces.slider.pvInitializeLayout(pSlider.data("data"));
	},

	
	pvInitializeData: function(pSlider, pValuesList, pMinValue, pMaxValue){
		var xData = {
			dom : {
				self: pSlider, //O próprio slider
				container : pSlider.children(".-container"), //Elemento que contém o container
				content : null, //Elemento dentro do container
				input : null, //Elemento input
				slider : null, //Elemento do slider
				sliderValue : null, //Elemento do valor atual do slider
				handle : null, //Puxador do slider
				points : null, //Elemento que contém os pontos
				point : null, //Elemento que contém um ponto
				label : null//Elemento que contém o label do ponto
			},
			type : pSlider.attr("type"), //Tipo do slider v,o,s
			dp : parseInt(pSlider.attr("dp")), //Quantidade de casas decimais(decimal points)
			orientation : (pSlider.hasClass("-h") ? "h" : "v"), //Orientação vertical ou horizontal
			valuesList : pValuesList, //Lista dos valores
			min : parseFloat(pMinValue),  //Valor mínimo
			max : parseFloat(pMaxValue), //Valor máximo
			ani : (pSlider.hasClass("-ani") ? true: false), //Se há animação
			segmentPercFator : (1 / (pValuesList.length - 1)), //Fator de cada item da lista
			length: null, //largura ou altura total do gráfico em px
			lengthPos : null, //Posição atual em relação a length(ver atributo acima)
			startPos: null, //Posição em relação a tela quando iniciado o movimento
			lengthFator: null, //Tamanho/posição atual em fator
			timeout: null, //Timeout para disparar evento de Change quando valor alterado
			valuesListNumeric: null, //lista dos valores convertida para valor númerico
			resizeTimeout: null
		}
		pSlider.data("data", xData);

		xData.dom.content = xData.dom.container.children(".-content");
		xData.dom.input = xData.dom.container.children(".-th_input-data");
		xData.dom.slider = xData.dom.content.children(".-slider");
		xData.dom.handle = xData.dom.content.children(".-handle");
		xData.dom.sliderValue = xData.dom.slider.children(".-value");

		if (xData.type == "v"){
			xData.valuesListNumeric = [];
			for (var xI=0; xI < pValuesList.length; xI++){
				if (typeof(pValuesList[xI]) == "number"){
					xData.valuesListNumeric.push(pValuesList[xI]);
				}else{
					xData.valuesListNumeric.push(parseFloat(pValuesList[xI].replace(/[^0-9]/g, '')));
				}
			}
		}

	},

	pvInitializeLayout: function(pSliderData){
		dbsfaces.slider.pvInitializeLayoutHorizontalVertical(pSliderData);
		dbsfaces.slider.pvInitializeLayoutPoints(pSliderData);
		dbsfaces.slider.resize(pSliderData);
		clearTimeout(pSliderData.resizeTimeout);
		pSliderData.resizeTimeout = setTimeout(function(e){
			pSliderData.dom.self.removeClass("-hide");
		},0);
	},
	
	pvInitializeLayoutHorizontalVertical: function(pSliderData){
		var xColor = tinycolor(pSliderData.dom.self.css("color"));
		var xColor2 = tinycolor(pSliderData.dom.self.css("color"));
		var xInverted = tinycolor(pSliderData.dom.self.css("color")).invertLightness().setAlpha(1);
		//Slider
		xColor2.setAlpha(.3);
		pSliderData.dom.slider.css("background", xColor2);
		
		//Slider value line
		var xBackground;
		xColor2.setAlpha(.70);
		xBackground = "linear-gradient(135deg," + xColor2 + " 0%, " + xColor + " 100%)";
		pSliderData.dom.sliderValue.css("background", xBackground);
		
		//Handle
		xColor2.setAlpha(1);
		if (xColor.isDark()){
			xColor2.lighten(10);
			xBackground = "linear-gradient(135deg," + xColor2 + " 0%, " + xColor + " 100%)";
		}else{
			xColor2.darken(10);
			xBackground = "linear-gradient(135deg," + xColor + " 0%, " + xColor2 + " 100%)";
		}
		pSliderData.dom.handle.css("background", xBackground);
		pSliderData.dom.handle.css("color", xInverted);
	},
	
	pvInitializeCreatePoints: function(pSliderData){
		//Apaga os pontos anteriores se já existirem
		pSliderData.dom.content.children(".-points").remove();
		//Cria points
		var xPoints = $(document.createElement('div')).addClass("-points");
		pSliderData.dom.content.append(xPoints);
		//Cria point e label
		if (pSliderData.valuesList.length  > 0){
			var xValue = "";
			var xLabel = "";
			var xClass = "";
			for (var xI = 0; xI < pSliderData.valuesList.length; xI++){
				//Configura alinhamento
				if (xI == 0){
					xClass = " -first";
				}else if (xI == pSliderData.valuesList.length - 1){
					xClass = " -last";
				}else{
					xClass = "";
				}
				var xIndex;
				if (pSliderData.orientation == "v"){
					//Encode a partir do último pois a ordem do slider cresce de baixo para cima
					xIndex = pSliderData.valuesList.length - xI -1;
				}else{
					xIndex = xI;
				}
				
				xValue = pSliderData.valuesList[xIndex];
				//Label iqual ao valor em formato númerico
				if (pSliderData.type == "v"){
					//Formata número
					xLabel = dbsfaces.format.number(xValue, pSliderData.dp);
				}else{
					//Label iqual ao valor
					xLabel = xValue;
				}
				//Point
				dbsfaces.slider.pvInitializeCreatePointElement(xPoints);
				//Label
				var xLabelElement = $(document.createElement('div')).addClass("-label" + xClass).attr("v", xValue).attr("l", xLabel);
				xPoints.append(xLabelElement);
			}
		}else{
			//Point
			dbsfaces.slider.pvInitializeCreatePointElement(xPoints);
			dbsfaces.slider.pvInitializeCreatePointElement(xPoints);
		}
		pSliderData.dom.points = pSliderData.dom.content.children(".-points");
		pSliderData.dom.point = pSliderData.dom.points.children(".-point");
		pSliderData.dom.label = pSliderData.dom.points.children(".-label");
		if (pSliderData.type == "v"){
			//Força que valor seja exatamente o valor do label selecionado
			pSliderData.dom.label.on("mousedown touchstart", function(e){
				dbsfaces.slider.setInputValue(pSliderData, $(this).attr("v"));
				dbsfaces.slider.setValue(pSliderData);
				e.stopImmediatePropagation();
				e.preventDefault();
			});
		}

	},

	pvInitializeCreatePointElement:function(pPoints){
		var xPointElement = $(document.createElement('div')).addClass("-point");
		pPoints.append(xPointElement);
	},
	

	pvInitializeLayoutPoints: function(pSliderData){
		if (pSliderData.dom.points.length > 0){
			pSliderData.dom.self.addClass("-showValuesList");
		}
		//Point
		var xValuePerc;
		for (var xI=0; xI < pSliderData.dom.point.length; xI++){
			xValuePerc = (xI / (pSliderData.dom.point.length - 1)) * 100;
			if (pSliderData.orientation == "h"){
				$(pSliderData.dom.point[xI]).css("left", xValuePerc + "%");
			}else{
				$(pSliderData.dom.point[xI]).css("top", xValuePerc + "%");
			}
		}
		//Label
		for (var xI=0; xI < pSliderData.dom.label.length; xI++){
			//Texto
			$(pSliderData.dom.label[xI]).text($(pSliderData.dom.label[xI]).attr("l"));
			//Posição
			xValuePerc = (xI / (pSliderData.dom.label.length - 1)) * 100;
			if (pSliderData.orientation == "h"){
				$(pSliderData.dom.label[xI]).css("left", xValuePerc + "%");
			}else{
				$(pSliderData.dom.label[xI]).css("top", xValuePerc + "%");
			}
//			$(pSliderData.label[xI]).data("perc", xValuePerc / 100);
		}
	},


	jump: function(pSliderData, e){
		pSliderData.dom.self.addClass("-selected");
		//Calcula fator em relação as coordenada do click
		var xLengthFator = 0;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		if (pSliderData.orientation == "h"){
			xLengthFator = (xXY.x - pSliderData.dom.content[0].getBoundingClientRect().left) / pSliderData.length;
		}else{
			xLengthFator = 1 - ((xXY.y - pSliderData.dom.content[0].getBoundingClientRect().top) / pSliderData.length);
		}
		dbsfaces.slider.pvSetValuePerc(pSliderData, xLengthFator);

		e.stopImmediatePropagation();
		e.preventDefault();
	},

	handleStart: function(pSliderData, e){
		pSliderData.dom.self.addClass("-selected");
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		//Sala posição atual para calcular a diferença posteriormente
		if (pSliderData.orientation == "h"){
			pSliderData.startPos = xXY.x;
		}else{
			pSliderData.startPos = xXY.y;
		}

		pSliderData.lengthPos = pSliderData.length * pSliderData.lengthFator;
		
		e.stopImmediatePropagation();
		e.preventDefault();
	},
	
	handleStop: function(pSliderData, e){
		pSliderData.startPos = null;
		pSliderData.dom.self.removeClass("-selected");
		e.stopImmediatePropagation();
		e.preventDefault();
	},
	
	handleMove: function(pSliderData, e){
		if (pSliderData.startPos == null){return;}
		var xDif = pSliderData.lengthPos;
		var xLengthFator = 0;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		if (pSliderData.orientation == "h"){
			xDif -= pSliderData.startPos - xXY.x;
		}else{
			xDif -= xXY.y - pSliderData.startPos;
		}
		if (xDif < 0){
			xDif = 0;
		}else if (xDif > pSliderData.length){
			xDif = pSliderData.length;
		}
		xLengthFator = xDif / pSliderData.length;
		dbsfaces.slider.pvSetValuePerc(pSliderData, xLengthFator);
		e.stopImmediatePropagation();
		e.preventDefault();
	},

	resize: function(pSliderData){
		//Atualiza dimensão
		if (pSliderData.orientation == "h"){
			pSliderData.length = pSliderData.dom.content[0].getBoundingClientRect().width;
		}else{
			pSliderData.length = pSliderData.dom.content[0].getBoundingClientRect().height;
		}
		dbsfaces.slider.setValue(pSliderData);
	},


	//Encontra o percentual a partir do valor e seta o slider
	setValue: function(pSliderData){
		var xValue = pSliderData.dom.input.attr("value");
		var xLengthFator = 0; 
		if (pSliderData.type == "v"){
			var xMin = pSliderData.min;
			var xMax = pSliderData.max;
			xLengthFator = dbsfaces.math.round(parseFloat(xValue.replace(/[^0-9]/g, '')), 10);
			//Procura qual o item da lista foi selecionado
			if (pSliderData.valuesListNumeric.length > 0){
				//Verifica se valor ultrapassou os limites
				if (xLengthFator < pSliderData.valuesListNumeric[0]){
					xLengthFator = pSliderData.valuesListNumeric[0];
				}else if(xLengthFator > pSliderData.valuesListNumeric[pSliderData.valuesListNumeric.length - 1]){
					xLengthFator = pSliderData.valuesListNumeric[pSliderData.valuesListNumeric.length - 1];
				}
				//Procura item na lista
				for (var xI=0; xI < pSliderData.valuesListNumeric.length; xI++){
					if (pSliderData.valuesListNumeric[xI] > xLengthFator){
						xMax = pSliderData.valuesListNumeric[xI];
						xMin = pSliderData.valuesListNumeric[xI -1];
						break;
					}
				}
				//Calcula fator
				xLengthFator = pSliderData.segmentPercFator * ((xLengthFator - xMin) / (xMax - xMin));
				xLengthFator += (pSliderData.segmentPercFator * (xI - 1));
			}else{
				//Calcula fator
				xLengthFator = (xLengthFator - xMin) / (xMax - xMin); 
			}
		}else{
			xValue = xValue.trim().toLowerCase();
			//Procura qual o item da lista foi selecionado
			for (var xI=0; xI < pSliderData.valuesList.length; xI++){
				if (pSliderData.valuesList[xI].toLowerCase() == xValue){
					 xLengthFator = xI / (pSliderData.valuesList.length - 1);
					 break;
				}
			}
		}
		dbsfaces.slider.pvSetValuePerc(pSliderData, xLengthFator);
	},

	setInputValue: function(pSliderData, pInputValue){
		var xValue = pInputValue;
		//Salva como string
		if (pSliderData.type == "v"){
			xValue = dbsfaces.format.number(pInputValue, pSliderData.dp);
		}
		pSliderData.dom.input.attr("value", xValue);
		//Salva como float
		pSliderData.dom.input.data("value", pInputValue);
		pSliderData.dom.self.val(pInputValue);

	},

	pvSetValuePerc: function(pSliderData, pLengthFator){
		pLengthFator = dbsfaces.math.round(parseFloat(pLengthFator), 10);
		if (pLengthFator > 1){
			pLengthFator = 1;
		}else if(pLengthFator < 0){
			pLengthFator = 0;
		}
		var xInputValue;
		var xI = null;
		if (pSliderData.type == "v"){
			var xMax;
			var xMin;
			var xValuePercFator = pLengthFator;
			//Calcula novo percentual relativo considerando o intervalo do segmento
			if (pSliderData.valuesListNumeric.length > 0){
				xI = dbsfaces.math.trunc(((pSliderData.valuesListNumeric.length - 1) * (pLengthFator - 0.01)), 0);
				xValuePercFator = pLengthFator - (pSliderData.segmentPercFator * xI);
				xValuePercFator /= pSliderData.segmentPercFator;
				xMin = parseFloat(pSliderData.valuesListNumeric[xI]);
				xMax = parseFloat(pSliderData.valuesListNumeric[xI + 1]);
			}else{
				xMin = pSliderData.min;
				xMax = pSliderData.max;
			}
			xInputValue = ((xMax - xMin) * xValuePercFator) + xMin;
			xInputValue = dbsfaces.math.round(xInputValue, pSliderData.dp);
		}else{
			//Encontra o valor da lista mais próximo ao percentual
			xI = dbsfaces.math.round(((pSliderData.valuesList.length - 1) * pLengthFator), 0);
			pLengthFator = xI / (pSliderData.valuesList.length - 1);
			xInputValue = pSliderData.valuesList[xI];
		}
		//Salva inputValue
		dbsfaces.slider.setInputValue(pSliderData, xInputValue);
		//Salva percentual relativo a length(coordenada)
		pSliderData.lengthFator = pLengthFator;
		dbsfaces.slider.pvEncodeValue(pSliderData);
	},
	
	pvEncodeValue: function(pSliderData){
		var xSliderValue = pSliderData.dom.sliderValue;
		var xValuePerc = pSliderData.lengthFator * 100;
		if (pSliderData.orientation == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(pSliderData, xValuePerc);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(pSliderData, xValuePerc);
		}

		//Valor para ser capturado pelo pseudoselector :before:content
		pSliderData.dom.handle.attr("v", pSliderData.dom.input.attr("value"));

		//Configura steps anteriores
		dbsfaces.slider.pvHideLabels(pSliderData);

		//Dispara que valor foi alterado
		clearTimeout(pSliderData.timeout);
		pSliderData.timeout = setTimeout(function(){
									pSliderData.dom.self.trigger("change", [{value:pSliderData.dom.input.data("value"), fator:pSliderData.lengthFator}]);
								},0);
	},

	
	pvEncodeValueHorizontal: function(pSliderData, pValuePerc){
		pSliderData.dom.sliderValue.css("width", pValuePerc + "%");
		pSliderData.dom.handle.css("left", pValuePerc + "%");
	},

	pvEncodeValueVertical: function(pSliderData, pValuePerc){
		pSliderData.dom.sliderValue.css("height", pValuePerc + "%");
		pSliderData.dom.handle.css("top", 100 - pValuePerc + "%");
	},
	
	pvHideLabels: function(pSliderData){
		var xCur;
		if (pSliderData.orientation == "h"){
			xCur = parseFloat(pSliderData.dom.handle.css("left"));
		}else{
			xCur = parseFloat(pSliderData.dom.handle.css("top"));
		}
		if (pSliderData.type == "v"){
			for (var xI=0; xI < pSliderData.dom.label.length; xI++){
				var xLabelX = $(pSliderData.dom.label[xI]);
				var xSize;
				var xMin;
				var xMax;
				if (pSliderData.orientation == "h"){
					xSize = parseFloat(xLabelX.css("width"));
					xMin = parseFloat(xLabelX.css("left"));
				}else{
					xSize = parseFloat(xLabelX.css("height"));
					xMin = parseFloat(xLabelX.css("top"));
				}
				xMax = xMin + xSize;
				xMin -= xSize;
				if (xCur > xMin && xCur < xMax){
					xLabelX.css("opacity", 0);
				}else{
					xLabelX.css("opacity", "");
				}
			}
		}
	}
}
