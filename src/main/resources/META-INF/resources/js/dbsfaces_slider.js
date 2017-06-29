dbs_slider = function(pId, pValuesList, pLabelsList, pMinValue, pMaxValue) {
	dbsfaces.slider.initialize($(pId), pValuesList, pLabelsList, pMinValue, pMaxValue);

	$(window).resize(function(e){
		dbsfaces.slider.resize($(pId).data("data"));
	});
	
	if ($(pId).hasClass("-readOnly")){return;}
	if ($(pId).hasClass("-disabled")){return;}
	
	$(pId + " > .-container").on("mousedown touchstart", function(e){
		dbsfaces.slider.jump($(pId).data("data"), e);
		dbsfaces.slider.handleMoveStart($(pId).data("data"), e);
	});
	$(pId + " > .-container").on("mouseup touchend", function(e){
		dbsfaces.slider.handleMoveStop($(pId).data("data"), e);
	});
	$(pId + " > .-container").on("mouseleave", function(e){
		dbsfaces.slider.handleMoveStop($(pId).data("data"), e);
	});
	$(pId + " > .-container").on("mousemove touchmove", function(e){
		if ($(pId).data("data").startPos == null){return false;}
		if (e.originalEvent.type == "mousemove" 
		 && e.which == 0){
			dbsfaces.slider.handleMoveStop($(pId).data("data"), e);
			return;
		}
		dbsfaces.slider.handleMove($(pId).data("data"), e);
	});	
	$(pId).on("mouseleave", function(e){
		dbsfaces.slider.handleMoveStop($(pId).data("data"), e);
	});
	$(pId + " > .-container > .-content > .-handle").on("mousedown touchstart", function(e){
		var xSliderData = $(pId).data("data");
		if (xSliderData.type != "r"){return;}
		var xHandle = $(this);
		if (xHandle.hasClass("-begin")){
			dbsfaces.slider.pvSetCurrentHandle(xSliderData, "b");
		}else if (xHandle.hasClass("-end")){
			dbsfaces.slider.pvSetCurrentHandle(xSliderData, "e");
		}
	});
}

dbsfaces.slider = {
	initialize: function(pSlider, pValuesList, pLabelsList, pMinValue, pMaxValue){
		var xSliderData= dbsfaces.slider.pvInitializeData(pSlider, pValuesList, pLabelsList, pMinValue, pMaxValue);
		dbsfaces.slider.pvInitializeCreatePoints(xSliderData);
		dbsfaces.slider.pvInitializeLayout(xSliderData);
	},

	pvInitializeData: function(pSlider, pValuesList, pLabelsList, pMinValue, pMaxValue){
		var xData = {
			dom : {
				self: pSlider, //O próprio slider
				container : pSlider.children(".-container"), //Elemento que contém o container
				content : null, //Elemento dentro do container
				input : null, //Elemento input
				inputBegin : null, //Elemento input
				inputEnd : null, //Elemento input
				slider : null, //Elemento do slider
				sliderValue : null, //Elemento do valor atual do slider
				handle : null, //Puxador do slider
				handleLabel : null, //label do valor selecionado no puxador
				handleBegin : null, //Puxador do slider
				handleBeginLabel : null, //label do valor selecionado no puxador
				handleEnd : null, //Puxador do slider
				handleEndLabel : null, //label do valor selecionado no puxador
				points : null, //Elemento que contém os pontos
				point : null, //Elemento que contém um ponto
				label : null//Elemento que contém o label do ponto
			},
			type : pSlider.attr("type"), //Tipo do slider v,o,s,r
			dp : parseInt(pSlider.attr("dp")), //Quantidade de casas decimais(decimal points)
			value: null, //Valor atual
			valueBegin: null, //Valor atual máximo - Slider type = "r" range
			valueEnd: null, //Valor atual mínimo - Slider type = "r" range
			orientation : (pSlider.hasClass("-h") ? "h" : "v"), //Orientação vertical ou horizontal
			valuesList : pValuesList, //Lista dos valores
			labelsList : pLabelsList, //Lista dos labels
			currentHandle : null, //Handle selecionado "b", "e" ou null(quando type não for "r")
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
		xData.dom.slider = xData.dom.content.children(".-slider");
		if (xData.type == "r"){
			xData.dom.inputBegin = xData.dom.container.children(".-th_input-data.-begin");
			xData.dom.inputEnd = xData.dom.container.children(".-th_input-data.-end");
			xData.dom.handleBegin = xData.dom.content.children(".-handle.-begin");
			xData.dom.handleBeginLabel = xData.dom.handleBegin.children(".-label");
			xData.dom.handleEnd = xData.dom.content.children(".-handle.-end");
			xData.dom.handleEndLabel = xData.dom.handleEnd.children(".-label");
			//Seta posição inicial
			dbsfaces.slider.pvSetCurrentHandle(xData, "b");
			dbsfaces.slider.setInputValue(xData, xData.dom.inputBegin.attr("value"));
			//Seta posição final
			dbsfaces.slider.pvSetCurrentHandle(xData, "e");
			dbsfaces.slider.setInputValue(xData, xData.dom.inputEnd.attr("value"));

			dbsfaces.slider.pvSetCurrentHandle(xData, null);
		}else{
			//Seta posicao atual
			xData.dom.input = xData.dom.container.children(".-th_input-data");
			xData.dom.handle = xData.dom.content.children(".-handle");
			xData.dom.handleLabel = xData.dom.handle.children(".-label");
			dbsfaces.slider.pvSetCurrentHandle(xData, null);
			dbsfaces.slider.setInputValue(xData, xData.dom.input.attr("value"));
		}
		xData.dom.sliderValue = xData.dom.slider.children(".-value");
		if (xData.type == "v"
		 || xData.type == "r"){
			xData.valuesListNumeric = [];
			for (var xI=0; xI < pValuesList.length; xI++){
				if (typeof(pValuesList[xI]) == "number"){
					xData.valuesListNumeric.push(pValuesList[xI]);
				}else{
//				xData.valuesListNumeric.push(parseFloat(pValuesList[xI].replace(/[^0-9]/g, '')));
					xData.valuesListNumeric.push(parseFloat(pValuesList[xI]));
				}
			}
			if (xData.valuesListNumeric.length > 0){
				xData.min = xData.valuesListNumeric[0];
				xData.max = xData.valuesListNumeric[xData.valuesListNumeric.length - 1];
			}
		}
		
		return xData;
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
		if (pSliderData.type == "r"){
			pSliderData.dom.handleBegin.css("background", xBackground);
			pSliderData.dom.handleBegin.css("color", xInverted);
			pSliderData.dom.handleEnd.css("background", xBackground);
			pSliderData.dom.handleEnd.css("color", xInverted);
		}else{
			pSliderData.dom.handle.css("background", xBackground);
			pSliderData.dom.handle.css("color", xInverted);
		}
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
				xLabel = pSliderData.labelsList[xIndex];
				if ((typeof xLabel == "undefined") || xLabel == ""){
					//Label iqual ao valor em formato númerico
					if (pSliderData.type == "v"
					 || pSliderData.type == "r"){
						//Formata número
						xLabel = dbsfaces.format.number(xValue, pSliderData.dp);
					}else{
						xLabel = xValue;
					}
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

	pvSetCurrentHandle: function(pSliderData, pHandle){
		pSliderData.currentHandle = pHandle;
		if (pHandle == "b"){
			pSliderData.dom.input = pSliderData.dom.inputBegin;
			pSliderData.dom.handle = pSliderData.dom.handleBegin;
			pSliderData.dom.handleLabel = pSliderData.dom.handleBeginLabel;
			pSliderData.value = pSliderData.valueBegin;
		}else if (pHandle == "e"){
			pSliderData.dom.input = pSliderData.dom.inputEnd;
			pSliderData.dom.handle = pSliderData.dom.handleEnd;
			pSliderData.dom.handleLabel = pSliderData.dom.handleEndLabel;
			pSliderData.value = pSliderData.valueEnd;
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

	handleMoveStart: function(pSliderData, e){
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
	
	handleMoveStop: function(pSliderData, e){
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


	setInputValue: function(pSliderData, pInputValue){
		if (pSliderData.dom.input == null){return;}
		var xValue = pInputValue;
		//Salva como string
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			xValue = dbsfaces.format.number(pInputValue, pSliderData.dp);
		}
		pSliderData.dom.input.attr("value", xValue);
		//Salva como float
		pSliderData.value = pInputValue;
		pSliderData.dom.self.val(pInputValue);
		if (pSliderData.currentHandle == "b"){
			pSliderData.dom.inputBegin.attr("value", xValue);
			pSliderData.valueBegin = pSliderData.value;
		}else if (pSliderData.currentHandle == "e"){
			pSliderData.dom.inputEnd.attr("value", xValue);
			pSliderData.valueEnd = pSliderData.value;
		}
	},

	//Encontra o percentual a partir do valor e seta o slider
	setValue: function(pSliderData){
		var xValue = dbsfaces.math.round(pSliderData.value, pSliderData.dp);
		var xLengthFator = 0; 
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			var xMin = pSliderData.min;
			var xMax = pSliderData.max;
//			xLengthFator = dbsfaces.math.round(parseFloat(xValue.replace(/[^0-9]/g, '')), 10);
			xLengthFator = parseFloat(xValue);
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
//				xLengthFator = pSliderData.segmentPercFator * ((xLengthFator / (xMax - xMin)) - xMin);
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

	pvSetValuePerc: function(pSliderData, pLengthFator){
		if (pSliderData.dom.input == null){return;}
		pLengthFator = parseFloat(pLengthFator);
		if (pLengthFator > 1){
			pLengthFator = 1;
		}else if(pLengthFator < 0){
			pLengthFator = 0;
		}
		var xInputValue;
		var xI = null;
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			var xMax;
			var xMin;
			var xValuePercFator = pLengthFator;
			//Calcula novo percentual relativo considerando o intervalo do segmento
			if (pSliderData.valuesListNumeric.length > 0){
//				xI = dbsfaces.math.trunc(((pSliderData.valuesListNumeric.length - 1) * (pLengthFator - 0.01)), 0);
				xI = dbsfaces.math.trunc(((pSliderData.valuesListNumeric.length - 1) * pLengthFator), 0);
				xValuePercFator = pLengthFator - (pSliderData.segmentPercFator * xI);
				xValuePercFator /= pSliderData.segmentPercFator;
//				xValuePercFator = pLengthFator - (pSliderData.segmentPercFator * (xI + 1));
				if (xI == pSliderData.valuesListNumeric.length - 1){
					xMin = parseFloat(pSliderData.valuesListNumeric[xI]);
					xMax = xMin;
				}else{
					xMin = parseFloat(pSliderData.valuesListNumeric[xI]);
					xMax = parseFloat(pSliderData.valuesListNumeric[xI + 1]);
				}
			}else{
				xMin = pSliderData.min;
				xMax = pSliderData.max;
			}
			xInputValue = ((xMax - xMin) * xValuePercFator) + xMin;
			xInputValue = dbsfaces.math.round(xInputValue, pSliderData.dp);
		}else{
			//Encontra o valor da lista mais próximo ao percentual
			xI = dbsfaces.math.trunc(((pSliderData.valuesList.length - 1) * pLengthFator), 0);
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
		//Valor para ser capturado pelo pseudoselector :before:content
		pSliderData.dom.handleLabel.text(pSliderData.dom.input.attr("value"));

		if (pSliderData.orientation == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(pSliderData, xValuePerc);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(pSliderData, xValuePerc);
		}


		//Configura steps anteriores
		dbsfaces.slider.pvHideLabels(pSliderData);

		//Dispara que valor foi alterado
		clearTimeout(pSliderData.timeout);
		pSliderData.timeout = setTimeout(function(){
									pSliderData.dom.self.trigger("change", [{value:pSliderData.dom.input.data("value"), fator:pSliderData.lengthFator}]);
								},0);
	},

	
	pvEncodeValueHorizontal: function(pSliderData, pValuePerc){
		if (pSliderData.type == "r"){
			if (pValuePerc < parseFloat(pSliderData.dom.handleBegin[0].style.left)){
				dbsfaces.slider.pvSetCurrentHandle(pSliderData, "b");
			}else if (pValuePerc > parseFloat(pSliderData.dom.handleEnd[0].style.left)){
				dbsfaces.slider.pvSetCurrentHandle(pSliderData, "e");
			}
			if (pSliderData.currentHandle == "b"){
				xWidth = parseFloat(pSliderData.dom.handleEnd[0].style.left) - pValuePerc;
				pSliderData.dom.sliderValue.css("left", pValuePerc + "%");
				pSliderData.dom.sliderValue.css("width", xWidth + "%");
			}else{
				xWidth = pValuePerc - parseFloat(pSliderData.dom.handleBegin[0].style.left);
				pSliderData.dom.sliderValue.css("width", xWidth + "%");
			}
		}else{
			pSliderData.dom.sliderValue.css("width", pValuePerc + "%");
		}
		pSliderData.dom.handle.css("left", pValuePerc + "%");
		var xCenter = (pSliderData.dom.handleLabel[0].getBoundingClientRect().width / 2);
		var xLeft;
		var xR = (pSliderData.dom.handle[0].getBoundingClientRect().left + xCenter) -
		   	     (pSliderData.dom.slider[0].getBoundingClientRect().left + pSliderData.dom.slider[0].getBoundingClientRect().width);
		var xL = (pSliderData.dom.handle[0].getBoundingClientRect().left - xCenter) -
  	     		 pSliderData.dom.slider[0].getBoundingClientRect().left;
		if (xR > 0){
			xLeft = -xCenter - xR;
		}else if (xL < 0){
			xLeft = -xCenter - xL;
		}else{
			xLeft = -xCenter;
		}
		pSliderData.dom.handleLabel.css("left", xLeft);
	},

	pvEncodeValueVertical: function(pSliderData, pValuePerc){
		pSliderData.dom.sliderValue.css("height", pValuePerc + "%");
		pSliderData.dom.handle.css("top", 100 - pValuePerc + "%");
		var xCenter = (pSliderData.dom.handleLabel[0].getBoundingClientRect().height / 2);
		var xTop;
		var xT = (pSliderData.dom.handle[0].getBoundingClientRect().top + xCenter) -
		   	     (pSliderData.dom.slider[0].getBoundingClientRect().top + pSliderData.dom.slider[0].getBoundingClientRect().height);
		var xB = (pSliderData.dom.handle[0].getBoundingClientRect().top - xCenter) -
  	     		 pSliderData.dom.slider[0].getBoundingClientRect().top;
		if (xT > 0){
			xTop = -xCenter - xT;
		}else if (xB < 0){
			xTop = -xCenter - xB;
		}else{
			xTop = -xCenter;
		}
		pSliderData.dom.handleLabel.css("top", xTop);
	},
	
	pvHideLabels: function(pSliderData){
		var xCur;
		if (pSliderData.orientation == "h"){
			xCur = parseFloat(pSliderData.dom.handle.css("left"));
//			xCur = pSliderData.dom.handle[0].getBoundingClientRect().left;
		}else{
			xCur = parseFloat(pSliderData.dom.handle.css("top"));
//			xCur = pSliderData.dom.handle[0].getBoundingClientRect().top;
		}
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
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
