dbs_slider = function(pId, pValuesList, pLabelsList, pMinValue, pMaxValue, pLocale) {
	dbsfaces.setLocale(pLocale);

	var xSliderData = dbsfaces.slider.initialize($(pId), pValuesList, pLabelsList, pMinValue, pMaxValue);

	$(window).resize(function(e){
		dbsfaces.slider.resize($(pId).data("data"));
	});
	
	if (xSliderData.dom.self.hasClass("-readOnly")){return;}
	if (xSliderData.dom.self.hasClass("-disabled")){return;}
	
	xSliderData.dom.slider.on("mousedown touchstart", function(e){
		dbsfaces.slider.jump(xSliderData, e);
		dbsfaces.slider.handleMoveStart(xSliderData, e);
		$(document.activeElement).blur();
		e.stopImmediatePropagation();
		return false;
	});
	xSliderData.dom.slider.on("mouseup touchend", function(e){
		dbsfaces.slider.handleMoveStop(xSliderData, e);
		e.stopImmediatePropagation();
		return false;
	});
	xSliderData.dom.slider.on("mouseleave", function(e){
		dbsfaces.slider.handleMoveStop(xSliderData, e);
		e.stopImmediatePropagation();
		return false;
	});
	xSliderData.dom.slider.on("mousemove touchmove", function(e){
		if (xSliderData.startPos == null){return false;}
		if (e.originalEvent.type == "mousemove" 
		 && e.which == 0){
			dbsfaces.slider.handleMoveStop(xSliderData, e);
			e.stopImmediatePropagation();
			return false;
		}
		dbsfaces.slider.handleMove(xSliderData, e);
		e.stopImmediatePropagation();
		return false;
	});	
	xSliderData.dom.self.on("mouseleave", function(e){
		dbsfaces.slider.handleMoveStop(xSliderData, e);
		e.stopImmediatePropagation();
		return false;
	});
	xSliderData.dom.label.on("mousedown touchstart", function(e){
		dbsfaces.slider.jump(xSliderData, e);
		e.stopImmediatePropagation();
		return false;
	});
	xSliderData.dom.point.on("mousedown touchstart", function(e){
		dbsfaces.slider.jump(xSliderData, e);
		e.stopImmediatePropagation();
		return false;
	});
	if (xSliderData.dom.inputBegin!=null){
		xSliderData.dom.inputBegin.on("keydown", function(e){
			dbsfaces.slider.setCurrentHandle(xSliderData, "b");
			dbsfaces.slider.setValue(xSliderData.dom.self, this.value);
		});
		xSliderData.dom.inputBegin.on("blur", function(e){
			var xBeginValue = dbsfaces.number.parseFloat(xSliderData.dom.inputBegin[0].value);
			var xEndValue = dbsfaces.number.parseFloat(xSliderData.dom.inputEnd[0].value);
			if (xBeginValue > xEndValue){
				dbsfaces.slider.setCurrentHandle(xSliderData, "b");
				dbsfaces.slider.setValue(xSliderData.dom.self, xEndValue);
			}
			dbsfaces.slider.setEditing(xSliderData, false);
		});
		xSliderData.dom.handleBeginLabel.on("mousedown touchstart", function(e){
			dbsfaces.slider.setCurrentHandle(xSliderData, "b");
			dbsfaces.slider.setEditing(xSliderData, true, xSliderData.dom.inputBegin);
			e.stopImmediatePropagation();
			return false;
		});
	}
	if (xSliderData.dom.inputEnd!=null){
		xSliderData.dom.inputEnd.on("keydown", function(e){
			dbsfaces.slider.setCurrentHandle(xSliderData, "e");
			dbsfaces.slider.setValue(xSliderData.dom.self, this.value);
		});
		xSliderData.dom.inputEnd.on("blur", function(e){
			var xBeginValue = dbsfaces.number.parseFloat(xSliderData.dom.inputBegin[0].value);
			var xEndValue = dbsfaces.number.parseFloat(xSliderData.dom.inputEnd[0].value);
			if (xEndValue < xBeginValue){
				dbsfaces.slider.setCurrentHandle(xSliderData, "e");
				dbsfaces.slider.setValue(xSliderData.dom.self, xBeginValue);
			}
			dbsfaces.slider.setEditing(xSliderData, false);
		});
		xSliderData.dom.handleEndLabel.on("mousedown touchstart", function(e){
			dbsfaces.slider.setCurrentHandle(xSliderData, "e");
			dbsfaces.slider.setEditing(xSliderData, true, xSliderData.dom.inputEnd);
			e.stopImmediatePropagation();
			return false;
		});
	}
	xSliderData.dom.inputs.on("change", function(e){
		e.stopImmediatePropagation();
		return false;
	});
	if (xSliderData.dom.inputBegin == null && xSliderData.dom.inputEnd == null){
		xSliderData.dom.input.on("keydown", function(e){
			dbsfaces.slider.setCurrentHandle(xSliderData, null);
			dbsfaces.slider.setValue(xSliderData.dom.self, this.value);
		});		
		xSliderData.dom.input.on("blur", function(e){
			dbsfaces.slider.setEditing(xSliderData, false);
		});
		xSliderData.dom.handleLabel.on("mousedown touchstart", function(e){
			dbsfaces.slider.setCurrentHandle(xSliderData, null);
			dbsfaces.slider.setEditing(xSliderData, true, xSliderData.dom.input);
			e.stopImmediatePropagation();
			return false;
		});
	}
}

dbsfaces.slider = {
	initialize: function(pSlider, pValuesList, pLabelsList, pMinValue, pMaxValue){
		var xSliderData= dbsfaces.slider.pvInitializeData(pSlider, pValuesList, pLabelsList, pMinValue, pMaxValue);
		dbsfaces.slider.pvInitializeCreatePoints(xSliderData);
		dbsfaces.slider.pvInitializeLayout(xSliderData);
		return xSliderData;
	},

	pvInitializeData: function(pSlider, pValuesList, pLabelsList, pMinValue, pMaxValue){
		var xData = {
			dom : {
				self: pSlider, //O próprio slider
				container : pSlider.children(".-container"), //Elemento que contém o container
				content : null, //Elemento dentro do container
				inputs : null, //Elementos inputs
				input : null, //Elemento input
				inputBegin : null, //Elemento input
				inputEnd : null, //Elemento input
				sub_container : null, //Elemento que agrupo as informações do slider
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
			orientation : (pSlider.hasClass("-h") ? "h" : "v"), //Orientação vertical ou horizontal
			dp : parseInt(pSlider.attr("dp")), //Quantidade de casas decimais(decimal points)
			invert : (pSlider.hasClass("-i") ? true : false), //Se inverte a posição para exibição da lista dos valores
			valuesList : pValuesList, //Lista dos valores
			labelsList : pLabelsList, //Lista dos labels
			value: null, //Valor atual
			valueBegin: null, //Valor atual máximo - Slider type = "r" range
			valueEnd: null, //Valor atual mínimo - Slider type = "r" range
			lengthFator: null, //Tamanho/posição atual em fator
			lengthFatorBegin: null, //Tamanho/posição atual em fator do begin
			lengthFatorEnd: null, //Tamanho/posição atual em fator do end
			lengthFatorZero: 0,//Tamanho/posição em fator do valor zero
			changeValue: null, //Tamanho/posição quando foi disparado o último change
			changeValueBegin: null, //Tamanho/posição quando foi disparado o último change
			changeValueEnd: null, //Tamanho/posição quando foi disparado o último change
			changeLengthFator: null, //Tamanho/posição quando foi disparado o último change
			currentHandle : null, //Handle selecionado "b", "e" ou null(quando type não for "r")
			min : dbsfaces.number.parseFloat(pMinValue),  //Valor mínimo
			max : dbsfaces.number.parseFloat(pMaxValue), //Valor máximo
			ani : (pSlider.hasClass("-ani") ? true: false), //Se há animação
			segmentFator : null, //Fator de cada item da lista
			length: null, //largura ou altura total do gráfico em px
			lengthPos : null, //Posição atual em relação a length(ver atributo acima)
			startPos: null, //Posição em relação a tela quando iniciado o movimento
			timeout: null, //Timeout para disparar evento de Change quando valor alterado
			valuesListNumeric: null, //lista dos valores convertida para valor númerico
			resizeTimeout: null,
			switched: false,
			color: pSlider.css("color"),
			gradientOrientation: (pSlider.hasClass("-h") ? "to right" : "to top")
		}
		pSlider.data("data", xData);
		xData.dom.content = xData.dom.container.children(".-content");
		xData.dom.sub_container = xData.dom.content.children(".-sub_container");
		xData.dom.slider = xData.dom.sub_container.children(".-slider");
		xData.dom.inputs = xData.dom.sub_container.find(".-th_input-data");
		if (xData.type == "r"){
			xData.dom.inputBegin = xData.dom.sub_container.find(".-begin .-th_input-data");
			xData.dom.inputEnd = xData.dom.sub_container.find(".-end .-th_input-data");
			xData.dom.handleBegin = xData.dom.sub_container.children(".-handle.-begin");
			xData.dom.handleBeginLabel = xData.dom.handleBegin.children(".-label");
			xData.dom.handleEnd = xData.dom.sub_container.children(".-handle.-end");
			xData.dom.handleEndLabel = xData.dom.handleEnd.children(".-label");
			//Seta posição inicial
			dbsfaces.slider.setCurrentHandle(xData, "b");
			dbsfaces.slider.pvSetInputValue(xData, xData.dom.inputBegin[0].value);
			//Seta posição final
			dbsfaces.slider.setCurrentHandle(xData, "e");
			dbsfaces.slider.pvSetInputValue(xData, xData.dom.inputEnd[0].value);
			xData.dom.inputEnd.attr("minValue", xData.min);
			xData.dom.inputEnd.attr("maxValue", xData.max);
			xData.dom.inputBegin.attr("minValue", xData.min);
			xData.dom.inputBegin.attr("maxValue", xData.max);
		}else{
			//Seta posicao atual
			xData.dom.input = xData.dom.sub_container.find(".-th_input-data");
			xData.dom.handle = xData.dom.sub_container.children(".-handle");
			xData.dom.handleLabel = xData.dom.handle.children(".-label");
			dbsfaces.slider.setCurrentHandle(xData, null);
			dbsfaces.slider.pvSetInputValue(xData, xData.dom.input[0].value);
			xData.dom.input.attr("minValue", xData.min);
			xData.dom.input.attr("maxValue", xData.max);
		}
		xData.dom.sliderValue = xData.dom.slider.children(".-value");
		if (xData.type == "v"
		 || xData.type == "r"){
			xData.valuesListNumeric = [];
			xData.segmentFator = 1 / (pValuesList.length - 1); //Fator de cada item da lista
			for (var xI=0; xI < pValuesList.length; xI++){
				if (typeof(pValuesList[xI]) == "number"){
					xData.valuesListNumeric.push(pValuesList[xI]);
				}else{
//				xData.valuesListNumeric.push(parseFloat(pValuesList[xI].replace(/[^0-9]/g, '')));
					xData.valuesListNumeric.push(dbsfaces.number.parseFloat(pValuesList[xI]));
				}
			}
			if (xData.valuesListNumeric.length > 0){
				xData.min = xData.valuesListNumeric[0];
				xData.max = xData.valuesListNumeric[xData.valuesListNumeric.length - 1];
			}
		}else{
			xData.segmentFator = 1 / pValuesList.length;
		}
		if (xData.min < 0){
			xData.lengthFatorZero = dbsfaces.slider.pvGetLengthFatorFromValue(xData, "0");
		}

		return xData;
	},
	

	pvInitializeLayout: function(pSliderData){
		//Timeout para dar tempo de saber a dimensão do componente pai
		clearTimeout(pSliderData.resizeTimeout);
		pSliderData.resizeTimeout = setTimeout(function(e){
			dbsfaces.slider.pvInitializeLayoutHorizontalVertical(pSliderData);
			dbsfaces.slider.pvInitializeLayoutPoints(pSliderData);
			dbsfaces.slider.resize(pSliderData);
			pSliderData.dom.self.removeClass("-hide");
		},0);
	},
	
	pvInitializeLayoutHorizontalVertical: function(pSliderData){
		var xColor = tinycolor(pSliderData.color);
		var xColor2 = tinycolor(pSliderData.color);
		//Slider
		var xSliderColor;
		xColor.setAlpha(.2);
		xColor2.setAlpha(.1);
		if (pSliderData.lengthFatorZero == 0){
			xSliderColor = "linear-gradient(" + pSliderData.gradientOrientation + "," + xColor2 + " 0%, " + xColor + " 100%)";
		}else{
			var xPercZero = pSliderData.lengthFatorZero * 100;
			xSliderColor = "linear-gradient(" + pSliderData.gradientOrientation + ",rgba(255,0,0,.2) 0%, " + xColor2 + " " + xPercZero + "%, " + xColor + " 100%)";
		}
		pSliderData.dom.slider.css("background", xSliderColor);

		//Inputs
		pSliderData.dom.inputs.addClass("-th_bc");
	},

	pvSetSliderColor: function(pSliderData){
		var xColor = tinycolor(pSliderData.color);
		var xColor2 = tinycolor(pSliderData.color);
		var xPercEnd = (1 / pSliderData.lengthFator) * 100;
		
		//Slider value line
		var xBackground;
		xColor.setAlpha(.9);
		xColor2.setAlpha(.5);
		if (pSliderData.lengthFatorZero == 0){
			xBackground = "linear-gradient(" + pSliderData.gradientOrientation + "," + xColor2 + " 0%, " + xColor + " " + xPercEnd + "%)";
		}else{
			var xPercZero = (pSliderData.lengthFatorZero / pSliderData.lengthFator) * 100;
//			xBackground = "linear-gradient(" + pSliderData.gradientOrientation + "," + xColor + " 0%, " + xColor2 + " " + xPercZero + "%, " + xColor + " " + xPercEnd + "%)";
			xBackground = "linear-gradient(" + pSliderData.gradientOrientation + ",rgba(255,0,0,.5)0%, " + xColor2 + " " + xPercZero + "%, " + xColor + " " + xPercEnd + "%)";
		}
		pSliderData.dom.sliderValue.css("background", xBackground);
	},
	

	pvInitializeCreatePoints: function(pSliderData){
		//Apaga os pontos anteriores se já existirem
		pSliderData.dom.content.children(".-points").remove();
		//Cria points
		var xPoints = $(document.createElement('div')).addClass("-points");
//		pSliderData.dom.sub_container.append(xPoints);
		if (pSliderData.type == "r"){
			xPoints.insertBefore(pSliderData.dom.handleBegin);
		}else{
			xPoints.insertBefore(pSliderData.dom.handle);
		}
		//Cria point e label
		if (pSliderData.valuesList.length > 0){
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
				dbsfaces.slider.pvInitializeCreatePointElement(xPoints, xClass);
				//Label
				var xLabelElement = $(document.createElement('div')).addClass("-label" + xClass).attr("v", xValue).attr("l", xLabel);
				xPoints.append(xLabelElement);
			}
		}else{
			//Point
			dbsfaces.slider.pvInitializeCreatePointElement(xPoints, "");
			dbsfaces.slider.pvInitializeCreatePointElement(xPoints, "");
		}
		pSliderData.dom.points = pSliderData.dom.sub_container.children(".-points");
		pSliderData.dom.point = pSliderData.dom.points.children(".-point");
		pSliderData.dom.label = pSliderData.dom.points.children(".-label");
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			//Força que valor seja exatamente o valor do label selecionado
			pSliderData.dom.label.on("mousedown touchstart", function(e){
				dbsfaces.slider.pvSetInputValue(pSliderData, $(this).attr("v"));
				dbsfaces.slider.setValue(pSliderData.dom.self, pSliderData.value);
				e.stopImmediatePropagation();
				e.preventDefault();
			});
		}
	},

	pvInitializeCreatePointElement:function(pPoints, pClass){
		var xPointElement = $(document.createElement('div')).addClass("-point" + pClass);
		pPoints.append(xPointElement);
	},
	

	pvInitializeLayoutPoints: function(pSliderData){
		if (pSliderData.dom.points.length > 0){
			pSliderData.dom.self.addClass("-showValuesList");
		}
		var xValuePerc;
		var xFator = pSliderData.segmentFator * 100; //Percentual que cada ponto representa
		//Point
		for (var xI=0; xI < pSliderData.dom.point.length; xI++){
			xValuePerc = xFator * xI;
			if (pSliderData.orientation == "h"){
				$(pSliderData.dom.point[xI]).css("left", xValuePerc + "%");
			}else{
				$(pSliderData.dom.point[xI]).css("top", xValuePerc + "%");
			}
		}
		//Label
		var xBase = 0;
		if (pSliderData.type == "s"){
			//centraliza. Posiciona no centro da celula 
			xBase += (xFator / 2);
		}
		for (var xI=0; xI < pSliderData.dom.label.length; xI++){
			//Texto
			$(pSliderData.dom.label[xI]).text($(pSliderData.dom.label[xI]).attr("l"));
			//Posição
			xValuePerc = xBase + (xFator * xI);
			if (pSliderData.orientation == "h"){
				$(pSliderData.dom.label[xI]).css("left", xValuePerc + "%");
			}else{
				$(pSliderData.dom.label[xI]).css("top", xValuePerc + "%");
			}
		}
	},

	resize: function(pSliderData){
		dbsfaces.slider.pvSetLength(pSliderData);

		if (pSliderData.type == "r"){
			dbsfaces.slider.setCurrentHandle(pSliderData, "b");
			dbsfaces.slider.setValue(pSliderData.dom.self, pSliderData.value);
			dbsfaces.slider.setCurrentHandle(pSliderData, "e");
			dbsfaces.slider.setValue(pSliderData.dom.self, pSliderData.value);
		}else{
			dbsfaces.slider.setValue(pSliderData.dom.self, pSliderData.value);
		}
	},

	jump: function(pSliderData, e){
		dbsfaces.slider.pvSetLength(pSliderData);

		//Calcula fator em relação as coordenada do click
		var xLengthFator = 0;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		pSliderData.dom.self.addClass("-selected");
		if (pSliderData.orientation == "h"){
			xLengthFator = (xXY.x - pSliderData.dom.sub_container[0].getBoundingClientRect().left) / pSliderData.length;
		}else{
			xLengthFator = 1 - ((xXY.y - pSliderData.dom.sub_container[0].getBoundingClientRect().top) / pSliderData.length);
		}
		//Seleciona o handle esta mais perto
		if (pSliderData.type == "r"){
			dbsfaces.slider.pvFindHandle(pSliderData, xLengthFator);
		}
		//Posiciona o slider
		dbsfaces.slider.pvSetValuePerc(pSliderData, xLengthFator);

		e.stopImmediatePropagation();
		e.preventDefault();
	},
	
	handleMoveStart: function(pSliderData, e){
		dbsfaces.slider.pvSetLength(pSliderData);;
		pSliderData.dom.self.addClass("-selected");
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		//Sala posição atual para calcular a diferença posteriormente
		if (pSliderData.orientation == "h"){
			pSliderData.startPos = xXY.x;
		}else{
			pSliderData.startPos = xXY.y;
		}
		if (pSliderData.type == "s"){
			//considera a posição inicial como do item anterior
			var xI = dbsfaces.math.trunc(((pSliderData.valuesList.length) * pSliderData.lengthFator), 0) - 1;
			//Posição atual em pontos (Percentual(lengthFator) do tamanho total(length))
			pSliderData.lengthPos = pSliderData.length * (xI / pSliderData.valuesList.length);
		}else{
			//Posição atual em pontos (Percentual(lengthFator) do tamanho total(length))
			pSliderData.lengthPos = pSliderData.length * pSliderData.lengthFator;
		}
		
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
		var xLengthPosNew = pSliderData.lengthPos;
		var xLengthFator = 0;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		if (pSliderData.orientation == "h"){
			xLengthPosNew -= pSliderData.startPos - xXY.x;
		}else{
			xLengthPosNew -= xXY.y - pSliderData.startPos;
		}
		if (xLengthPosNew < 0){
			xLengthPosNew = 0;
		}else if (xLengthPosNew > pSliderData.length){
			xLengthPosNew = pSliderData.length;
		}
		xLengthFator = xLengthPosNew / pSliderData.length;
		dbsfaces.slider.pvSetValuePerc(pSliderData, xLengthFator);
		e.stopImmediatePropagation();
		e.preventDefault();
	},

	setEditing: function(pSliderData, pEditing, pInputData){
		if (pEditing){
			pSliderData.dom.self.addClass("-editing");
			pInputData.focus();
		}else{
			pSliderData.dom.self.removeClass("-editing");
		}
	},
	
	setCurrentHandle: function(pSliderData, pHandle){
		pSliderData.currentHandle = pHandle;
		if (pHandle == "b"){
			pSliderData.dom.input = pSliderData.dom.inputBegin;
			pSliderData.dom.handle = pSliderData.dom.handleBegin;
			pSliderData.dom.handleLabel = pSliderData.dom.handleBeginLabel;
			pSliderData.dom.self.removeClass("-end").addClass("-begin");
			pSliderData.value = pSliderData.valueBegin;
			pSliderData.lengthFator = pSliderData.lengthFatorBegin;
			pSliderData.changeValue = pSliderData.changeValueBegin;
		}else if (pHandle == "e"){
			pSliderData.dom.input = pSliderData.dom.inputEnd;
			pSliderData.dom.handle = pSliderData.dom.handleEnd;
			pSliderData.dom.handleLabel = pSliderData.dom.handleEndLabel;
			pSliderData.dom.self.removeClass("-begin").addClass("-end");
			pSliderData.value = pSliderData.valueEnd;
			pSliderData.lengthFator = pSliderData.lengthFatorEnd;
			pSliderData.changeValue = pSliderData.changeValueEnd;
		}
	},
	
	pvFindHandle: function(pSliderData, pLengthFator){
		var xDistBegin = Math.abs(pLengthFator - pSliderData.lengthFatorBegin);
		var xDistEnd = Math.abs(pLengthFator - pSliderData.lengthFatorEnd);
		//Escolhe qual o handle mais próximo
		//Se for handle do inicio
		if (xDistBegin < xDistEnd){
			//Mas o handle ativo não é do inicio...
			if (pSliderData.currentHandle != "b"){
				//Ativa handle do inicio
				dbsfaces.slider.setCurrentHandle(pSliderData, "b");
			}
		}else{
			//Mas o handle ativo não é do fimn...
			if (pSliderData.currentHandle != "e"){
				//Ativa handle do fim
				dbsfaces.slider.setCurrentHandle(pSliderData, "e");
			}
		}
	},


	pvSetLength:  function(pSliderData){
		//Atualiza dimensão
		var xRect = dbsfaces.ui.getRect(pSliderData.dom.sub_container);
		if (pSliderData.orientation == "h"){
			pSliderData.length = xRect.width;
		}else{
			pSliderData.length = xRect.height;
		}
	},

	//Encontra o percentual a partir do valor e seta o slider
	setValue: function(pSlider, pValue){
		var xSliderData = pSlider.data("data");
		if ((typeof pValue == "undefined") || pValue.length == 0){return;}
		dbsfaces.slider.pvSetValuePerc(xSliderData, dbsfaces.slider.pvGetLengthFatorFromValue(xSliderData, pValue), true);
	},
	
	pvGetLengthFatorFromValue: function(pSliderData, pValue){
		pSliderData.value = dbsfaces.number.parseFloat(pValue);
		var xValue;
		var xLengthFator = 0;
		 
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			var xMin = pSliderData.min;
			var xMax = pSliderData.max;
			xValue = dbsfaces.math.round(pSliderData.value, pSliderData.dp);
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
				xLengthFator = pSliderData.segmentFator * ((xLengthFator - xMin) / (xMax - xMin));
				xLengthFator += (pSliderData.segmentFator * (xI - 1));
			}else{
				//Calcula fator
				xLengthFator = (xLengthFator - xMin) / (xMax - xMin); 
			}
		}else{
			xValue = pValue.trim().toLowerCase();
			//Procura qual o item da lista foi selecionado
			for (var xI=0; xI < pSliderData.valuesList.length; xI++){
				if (pSliderData.valuesList[xI].toLowerCase() == xValue){
					 xLengthFator = xI / pSliderData.valuesList.length;
					 break;
				}
			}
		}
		return xLengthFator;
	},
	
	
	pvSetValuePerc: function(pSliderData, pLengthFator, pForceValue){
		if (pSliderData.dom.input == null){return;}
		pLengthFator = parseFloat(pLengthFator);
		var xInputValue;
		var xI = null;
		if (pLengthFator > 1){
			pLengthFator = 1;
		}else if(pLengthFator < 0){
			pLengthFator = 0;
		}
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			var xMax;
			var xMin;
			var xValuePercFator = pLengthFator;
			//Calcula novo percentual relativo considerando o intervalo do segmento
			if (pSliderData.valuesListNumeric.length > 0){
				xI = dbsfaces.math.trunc(((pSliderData.valuesListNumeric.length - 1) * pLengthFator), 0);
				xValuePercFator = pLengthFator - (pSliderData.segmentFator * xI);
				xValuePercFator /= pSliderData.segmentFator;
				if (xI == pSliderData.valuesListNumeric.length - 1){
					xMin = dbsfaces.number.parseFloat(pSliderData.valuesListNumeric[xI]);
					xMax = xMin;
				}else{
					xMin = dbsfaces.number.parseFloat(pSliderData.valuesListNumeric[xI]);
					xMax = dbsfaces.number.parseFloat(pSliderData.valuesListNumeric[xI + 1]);
				}
			}else{
				xMin = pSliderData.min;
				xMax = pSliderData.max;
			}
			xInputValue = ((xMax - xMin) * xValuePercFator) + xMin;
			xInputValue = dbsfaces.math.round(xInputValue, pSliderData.dp);
			if (typeof pForceValue == "undefined"){
				//Força valor considerar somente os dois primeiros números relevantes
				var xSign = Math.sign(xInputValue) || 1;
				var xOnlyNumbers = dbsfaces.format.number(xInputValue, pSliderData.dp).replace(/[^\d]/g, '');
				var xTruncSize = xOnlyNumbers.length - 2;
				if (xTruncSize > 0){
					xTruncSize = Math.pow(10, xTruncSize);
					xOnlyNumbers = dbsfaces.number.parseFloat(xOnlyNumbers);
					xInputValue = (dbsfaces.math.trunc(xOnlyNumbers / xTruncSize, 0) * xTruncSize) / Math.pow(10, pSliderData.dp) * xSign;
				}
			}
		}else{
			if (pLengthFator > 0){
				//Encontra o valor da lista mais próximo ao percentual
				xI = dbsfaces.math.trunc(((pSliderData.valuesList.length) * pLengthFator), 0) + 1;
				pLengthFator = xI / pSliderData.valuesList.length;
				xInputValue = pSliderData.valuesList[xI - 1];
			}
		}
		//Salva percentual relativo a length(coordenada)
		pSliderData.lengthFator = pLengthFator;
		//Salva inputValue
		dbsfaces.slider.pvSetInputValue(pSliderData, xInputValue);
		dbsfaces.slider.pvEncodeValue(pSliderData);
		//Se houve inversão do handle
		if (pSliderData.switched){
			pSliderData.switched = false;
			//Força noveo encode do valor do handle anterior para que seja reposicionado
			var xCurrentHandle = pSliderData.currentHandle;
			if (pSliderData.currentHandle == "b"){
				dbsfaces.slider.setCurrentHandle(pSliderData, "e");
			}else{
				dbsfaces.slider.setCurrentHandle(pSliderData, "b");
			}
			dbsfaces.slider.pvEncodeValue(pSliderData);
			dbsfaces.slider.setCurrentHandle(pSliderData, xCurrentHandle);
		}
	},
	
	pvSetInputValue: function(pSliderData, pInputValue){
		if (pSliderData.dom.input == null){return;}
		var xFormattedValue = pInputValue;
		//Salva como string
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			xFormattedValue = dbsfaces.format.number(pInputValue, pSliderData.dp);
		}
		//Salva como float
		pSliderData.value = pInputValue;

		pSliderData.dom.self.val(pInputValue);
		if (pSliderData.currentHandle == "b"){
			pSliderData.dom.inputBegin[0].value = xFormattedValue;
			pSliderData.dom.inputBegin.attr("value", xFormattedValue);
			pSliderData.valueBegin = pSliderData.value;
			pSliderData.lengthFatorBegin = pSliderData.lengthFator;
		}else if (pSliderData.currentHandle == "e"){
			pSliderData.dom.inputEnd[0].value = xFormattedValue;
			pSliderData.dom.inputEnd.attr("value", xFormattedValue);
			pSliderData.valueEnd = pSliderData.value;
			pSliderData.lengthFatorEnd = pSliderData.lengthFator;
		}else{
			pSliderData.dom.input[0].value = xFormattedValue;
			pSliderData.dom.input.attr("value", xFormattedValue);
		}
	},

	pvEncodeValue: function(pSliderData){
		var xSliderValue = pSliderData.dom.sliderValue;
		var xValuePerc = pSliderData.lengthFator * 100;
		if (pSliderData.type == "s"
		 && pSliderData.lengthFator == 0){
			pSliderData.dom.handleLabel.text("");
		}else{
			//Valor para ser capturado pelo pseudoselector :before:content
			pSliderData.dom.handleLabel.text(pSliderData.dom.input.attr("value"));
		}
		
		
		if (pSliderData.orientation == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(pSliderData, xValuePerc);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(pSliderData, xValuePerc);
		}

		//Configura exibição ou não dos labels
		dbsfaces.slider.pvHideLabels(pSliderData);

		//Verifica se há necessidade de disparar o change
		if (pSliderData.changeValue != pSliderData.value
		 || pSliderData.changeValueBegin != pSliderData.valueBegin
		 || pSliderData.changeCalueEnd != pSliderData.valueEnd
		 || pSliderData.changeLengthFator != pSliderData.lengthFator){
			pSliderData.changeValue = pSliderData.value;
			pSliderData.changeValueBegin = pSliderData.valueBegin;
			pSliderData.changeValueEnd = pSliderData.valueEnd;
			pSliderData.changeLengthFator = pSliderData.lengthFator;
			//Dispara que valor foi alterado
//			clearTimeout(pSliderData.timeout);
//			pSliderData.timeout = setTimeout(function(){
				pSliderData.dom.self.trigger("change", [{value:pSliderData.value, valueBegin:pSliderData.valueBegin, valueEnd:pSliderData.valueEnd, fator:pSliderData.lengthFator}]);
//			},0);
				dbsfaces.slider.pvSetSliderColor(pSliderData);
		}
	},

	
	pvEncodeValueHorizontal: function(pSliderData, pValuePerc){
		if (pSliderData.type == "r"){
			//Inverte handle . Quem era o menor passa a ser maior o vice-versa
			if ((pSliderData.currentHandle == "e" && pValuePerc < parseFloat(pSliderData.dom.handleBegin[0].style.left))
  		     || (pSliderData.currentHandle == "b" && pValuePerc > parseFloat(pSliderData.dom.handleEnd[0].style.left))){
				if (!pSliderData.dom.self.hasClass("-editing")){
					dbsfaces.slider.pvHandleSwitch(pSliderData);
				}else{
					return;
				}
			}
		}
		//Posição do handle
		pSliderData.dom.handle.css("left", pValuePerc + "%");
		var xAlignShift = 0;
		if (pSliderData.type == "v"
		 || pSliderData.type == "s"){
			xAlignShift = (pSliderData.dom.handleLabel[0].getBoundingClientRect().width / 2);
			if (pSliderData.type == "s"){
				xAlignShift += (pSliderData.dom.slider[0].getBoundingClientRect().width * pSliderData.segmentFator) / 2;
			}
			//Center handle label
			var xLeft = -xAlignShift;
			
			//Limita pontos extremos para dentro da áreas
			if (pSliderData.type == "v"){
				var xR = (pSliderData.dom.handle[0].getBoundingClientRect().left + xAlignShift) -
						 (pSliderData.dom.slider[0].getBoundingClientRect().left + pSliderData.dom.slider[0].getBoundingClientRect().width);
				var xL = (pSliderData.dom.handle[0].getBoundingClientRect().left - xAlignShift) -
						  pSliderData.dom.slider[0].getBoundingClientRect().left;
				if (xR > 0){
					xLeft -= xR;
				}else if (xL < 0){
					xLeft -= xL;
				}
			}
			pSliderData.dom.handleLabel.css("left", xLeft);
		}else{
			if (pSliderData.currentHandle == "b"){
				xAlignShift = pSliderData.dom.handleLabel[0].getBoundingClientRect().width;
				var xL = (pSliderData.dom.handle[0].getBoundingClientRect().left - xAlignShift) -
				  		  pSliderData.dom.slider[0].getBoundingClientRect().left;
				if (xL < 0){
					xAlignShift += xL;
				}
				xAlignShift = -xAlignShift;
			}else{
				xAlignShift = 0
				var xR = (pSliderData.dom.handle[0].getBoundingClientRect().left + pSliderData.dom.handleLabel[0].getBoundingClientRect().width) -
				 		 (pSliderData.dom.slider[0].getBoundingClientRect().left + pSliderData.dom.slider[0].getBoundingClientRect().width);
				if (xR > 0){
					xAlignShift -= xR;
				}
//				xAlignShift = -xAlignShift;
			}
			//Limita pontos extremos para dentro da áreas
			pSliderData.dom.handleLabel.css("left", xAlignShift);
		}
		//termometro
		if (pSliderData.type == "r"){
			var xBegin = parseFloat(pSliderData.dom.handleBegin[0].style.left);
			var xEnd = parseFloat(pSliderData.dom.handleEnd[0].style.left);
			pSliderData.dom.sliderValue.css("left", xBegin + "%");
			pSliderData.dom.sliderValue.css("width", (xEnd - xBegin) + "%");
		}else{
			pSliderData.dom.sliderValue.css("width", pValuePerc + "%");
		}
	},

	pvEncodeValueVertical: function(pSliderData, pValuePerc){
		if (pSliderData.type == "r"){
			//Inverte handle . Quem era o menor passa a ser maior o vice-versa
			if ((pSliderData.currentHandle == "e" && (100 - pValuePerc) > parseFloat(pSliderData.dom.handleBegin[0].style.top))
  		     || (pSliderData.currentHandle == "b" && (100 - pValuePerc) < parseFloat(pSliderData.dom.handleEnd[0].style.top))){
				if (!pSliderData.dom.self.hasClass("-editing")){
					dbsfaces.slider.pvHandleSwitch(pSliderData);
				}
			}
		}
		//Handle
		pSliderData.dom.handle.css("top", 100 - pValuePerc + "%");
		
		var xCenter;
		xCenter = (pSliderData.dom.handleLabel[0].getBoundingClientRect().height / 2);
		if (pSliderData.type == "s"){
			xCenter -= (pSliderData.dom.slider[0].getBoundingClientRect().height * pSliderData.segmentFator) / 2;
		}
		//Center handle label
		var xTop;
		var xT = (pSliderData.dom.handle[0].getBoundingClientRect().top + xCenter) -
		   	     (pSliderData.dom.slider[0].getBoundingClientRect().top + pSliderData.dom.slider[0].getBoundingClientRect().height);
		var xB = (pSliderData.dom.handle[0].getBoundingClientRect().top - xCenter) -
  	     		 pSliderData.dom.slider[0].getBoundingClientRect().top;
		xTop = -xCenter;
		//Limita pontos dos extremos para dentro dos limites do slider
		if (pSliderData.type == "v"
		 || pSliderData.type == "r"){
			if (xT > 0){
				xTop = -xCenter - xT;
			}else if (xB < 0){
				xTop = -xCenter - xB;
			}
		}
		//Handle Label
		pSliderData.dom.handleLabel.css("top", xTop);
		
		//Preenchimento 
		if (pSliderData.type == "r"){
			var xBegin = parseFloat(pSliderData.dom.handleBegin[0].style.top);
			var xEnd = parseFloat(pSliderData.dom.handleEnd[0].style.top);
			pSliderData.dom.sliderValue.css("height", (xBegin - xEnd) + "%");
			pSliderData.dom.sliderValue.css("top", xEnd + "%");
		}else{
			pSliderData.dom.sliderValue.css("height", pValuePerc + "%");
		}
	},
	
	pvHandleSwitch: function(pSliderData){
		var xTmp = pSliderData.dom.handleBegin;
		pSliderData.dom.handleBegin = pSliderData.dom.handleEnd;
		pSliderData.dom.handleEnd = xTmp;
		
		xTmp = pSliderData.dom.handleBeginLabel;
		pSliderData.dom.handleBeginLabel = pSliderData.dom.handleEndLabel;
		pSliderData.dom.handleEndLabel = xTmp;

		xTmp = pSliderData.dom.inputBegin;
		pSliderData.dom.inputBegin = pSliderData.dom.inputEnd;
		pSliderData.dom.inputEnd = xTmp;

		xTmp = pSliderData.valueBegin;
		pSliderData.valueBegin = pSliderData.valueEnd;
		pSliderData.valueEnd = xTmp;

		xTmp = pSliderData.lengthFatorBegin;
		pSliderData.lengthFatorBegin = pSliderData.lengthFatorEnd;
		pSliderData.lengthFatorEnd = xTmp;

		xTmp = pSliderData.changeValueBegin;
		pSliderData.changeValueBegin = pSliderData.changeValueEnd;
		pSliderData.changeValueEnd = xTmp;

		pSliderData.dom.handleBegin.removeClass("-end").addClass("-begin");
		pSliderData.dom.handleEnd.removeClass("-begin").addClass("-end");
		
//		bbb
//		dbsfaces.slider.setCurrentHandle(pSliderData, pSliderData.currentHandle);
//		dbsfaces.slider.pvEncodeValue(pSliderData);
//		dbsfaces.slider.pvSetInputValue(pSliderData, pSliderData.dom.inputBegin[0].value);
		pSliderData.switched = true;
		if (pSliderData.currentHandle == "b"){
			dbsfaces.slider.setCurrentHandle(pSliderData, "e");
		}else{
			dbsfaces.slider.setCurrentHandle(pSliderData, "b");
		}
	},
	
	pvHideLabels: function(pSliderData){
		var xHandleMin, xHandleMax;
		var xHandleLabelRect = dbsfaces.ui.getRect(pSliderData.dom.handleLabel);
		if (pSliderData.orientation == "h"){
			xHandleMin = xHandleLabelRect.left;
			xHandleMax = xHandleMin + xHandleLabelRect.width;
		}else{
			xHandleMin = xHandleLabelRect.top;
			xHandleMax = xHandleMin + xHandleLabelRect.height;
		}
		//Aumenta os limites para esconder os outros labels antes mesmo do handle sobrepo-los.
		xHandleMin *= .99;
		xHandleMax *= 1.01;

		for (var xI=0; xI < pSliderData.dom.label.length; xI++){
			var xLabel = $(pSliderData.dom.label[xI]);
			var xLabelRect = dbsfaces.ui.getRect(xLabel);
			var xSize;
			var xMin;
			var xMax;
			if (pSliderData.orientation == "h"){
				xSize = xLabelRect.width;
				xMin = xLabelRect.left;
			}else{
				xSize = xLabelRect.height;
				xMin = xLabelRect.top;
			}
			xMax = xMin + xSize;
			dbsfaces.slider.pvHideLabel(pSliderData, xLabel, !(xHandleMax < xMin || xHandleMin > xMax));
		}
	},
	
	pvHideLabel: function(pSliderData, pLabel, pHide){
		var xOpacity = "";
		//Esconde label
		if (pHide){
			xOpacity = 0;
		}
		if (pSliderData.currentHandle != null){
			var xLabelOwner = pLabel.data("owner");
			if (typeof xLabelOwner == "undefined"){
				xLabelOwner = {b:false, e:false};
			}
			if (pHide){
				//Marcar qual o handle foi resposavel por esconder
				if (pSliderData.currentHandle == "b"){
					xLabelOwner.b = true;	
				}else{
					xLabelOwner.e = true;	
				}
			}else{
				//Marcar qual o handle não é mais responsável por esconder
				if (pSliderData.currentHandle == "b"){
					xLabelOwner.b = false;	
				}else{
					xLabelOwner.e = false;	
				}
				//Ignora exibição se houver algum responsável por esconder
				if (xLabelOwner.b || xLabelOwner.e){return;}
			}
			pLabel.data("owner", xLabelOwner);
		}
		pLabel.css("opacity", xOpacity);
	}
}
