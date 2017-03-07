dbs_slider = function(pId, pValuesList, pMinValue, pMaxValue) {
	dbsfaces.slider.initialize($(pId), pValuesList, pMinValue, pMaxValue);

	$(window).resize(function(e){
		dbsfaces.slider.resize($(pId));
	});
	
	if ($(pId).hasClass("-readOnly")){return;}
	if ($(pId).hasClass("-disabled")){return;}
	
	$(pId + " > .-container").on("mousedown touchstart", function(e){
		dbsfaces.slider.jump($(pId), e);
		dbsfaces.slider.handleStart($(pId), e);
	});
	$(pId + " > .-container").on("mouseup touchend", function(e){
		dbsfaces.slider.handleStop($(pId), e);
	});
	$(pId + " > .-container").on("mouseleave", function(e){
		dbsfaces.slider.handleStop($(pId), e);
	});
	$(pId + " > .-container").on("mousemove touchmove", function(e){
		if ($(pId).data("dif") == null){return false;}
		if (e.originalEvent.type == "mousemove" 
		 && e.which == 0){
			dbsfaces.slider.handleStop($(pId), e);
			return;
		}
		dbsfaces.slider.handleMove($(pId), e);
	});	
	$(pId).on("mouseleave", function(e){
		dbsfaces.slider.handleStop($(pId), e);
	});
}

dbsfaces.slider = {
	initialize: function(pSlider, pValuesList, pMinValue, pMaxValue){
		dbsfaces.slider.pvInitializeData(pSlider, pValuesList, pMinValue, pMaxValue);
		dbsfaces.slider.pvInitializeCreatePoints(pSlider, pValuesList, pMinValue, pMaxValue);
		dbsfaces.slider.pvInitializeLayout(pSlider);
	},

	
	pvInitializeData: function(pSlider, pValuesList, pMinValue, pMaxValue){
		pSlider.data("type", pSlider.attr("type"));
		pSlider.data("dp", parseInt(pSlider.attr("dp")));
		pSlider.data("orientation", (pSlider.hasClass("-h") ? "h" : "v"));
		pSlider.data("listvalues", pValuesList);
		pSlider.data("min", parseFloat(pMinValue));
		pSlider.data("max", parseFloat(pMaxValue));
		pSlider.data("container", pSlider.children(".-container"));
		pSlider.data("content", pSlider.data("container").children(".-content"));
		pSlider.data("input", pSlider.data("container").children(".-th_input-data"));
		pSlider.data("slider", pSlider.data("content").children(".-slider"));
		pSlider.data("sliderValue", pSlider.data("slider").children(".-value"));
		pSlider.data("handle", pSlider.data("content").children(".-handle"));
		pSlider.data("ani", (pSlider.hasClass("-ani") ? true: false));
		pSlider.data("segmentpercfator", (1 / (pSlider.data("listvalues").length - 1)));
		pSlider.data("dif", null);
		if (pSlider.data("type")== "v"){
			var xValuesListNumeric = [];
			for (var xI=0; xI < pValuesList.length; xI++){
				if (typeof(pValuesList[xI]) == "number"){
					xValuesListNumeric.push(pValuesList[xI]);
				}else{
					xValuesListNumeric.push(parseFloat(pValuesList[xI].replace(/[^0-9]/g, '')));
				}
			}
			pSlider.data("listvaluesnumeric", xValuesListNumeric);
		}

	},

	pvInitializeLayout: function(pSlider){
		dbsfaces.slider.pvInitializeLayoutHorizontalVertical(pSlider);
		dbsfaces.slider.pvInitializeLayoutPoints(pSlider);
		dbsfaces.slider.resize(pSlider);
		setTimeout(function(e){
			pSlider.removeClass("-hide");
		},0);
	},
	
	pvInitializeLayoutHorizontalVertical: function(pSlider){
		var xColor = tinycolor(pSlider.css("color"));
		var xColor2 = tinycolor(pSlider.css("color"));
		var xInverted = tinycolor(pSlider.css("color")).invertLightness().setAlpha(1);
		//Slider
		xColor2.setAlpha(.3);
		pSlider.data("slider").css("background", xColor2);
		
		//Slider value line
		var xBackground;
		xColor2.setAlpha(.70);
		xBackground = "linear-gradient(135deg," + xColor2 + " 0%, " + xColor + " 100%)";
		pSlider.data("sliderValue").css("background", xBackground);
		
		//Handle
		xColor2.setAlpha(1);
		if (xColor.isDark()){
			xColor2.lighten(10);
			xBackground = "linear-gradient(135deg," + xColor2 + " 0%, " + xColor + " 100%)";
		}else{
			xColor2.darken(10);
			xBackground = "linear-gradient(135deg," + xColor + " 0%, " + xColor2 + " 100%)";
		}
		pSlider.data("handle").css("background", xBackground);
		pSlider.data("handle").css("color", xInverted);
	},
	
	pvInitializeCreatePoints: function(pSlider, pValuesList, pMinValue, pMaxValue){
		var xContent = pSlider.data("content");
		var xType = pSlider.data("type");
		var xOrientation = pSlider.data("orientation");
		//Apaga os pontos anteriores se já existirem
		xContent.children(".-points").remove();
		//Cria points
		var xPoints = $(document.createElement('div')).addClass("-points");
		xContent.append(xPoints);
		//Cria point e label
		if (pValuesList.length  > 0){
			var xValue = "";
			var xLabel = "";
			var xClass = "";
			for (var xI = 0; xI < pValuesList.length; xI++){
				//Configura alinhamento
				if (xI == 0){
					xClass = " -first";
				}else if (xI == pValuesList.length - 1){
					xClass = " -last";
				}else{
					xClass = "";
				}
				var xIndex = xI;
				if (xOrientation == "v"){
					//Encode a partir do último pois a ordem do slider cresce de baixo para cima
					xIndex = pValuesList.length - xI -1;
				}
				xValue = pValuesList[xIndex];
				xLabel = xValue;
				if (xType == "v"){
					//Formata número
					xLabel = dbsfaces.format.number(xValue, pSlider.data("dp"));
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
		pSlider.data("points", pSlider.data("content").children(".-points"));
		pSlider.data("point", pSlider.data("points").children(".-point"));
		pSlider.data("label", pSlider.data("points").children(".-label"));
		if (xType == "v"){
			//Força que valor seja exatamente o valor do label selecionado
			pSlider.data("label").on("mousedown touchstart", function(e){
				dbsfaces.slider.setInputValue(pSlider, $(this).attr("v"));
				dbsfaces.slider.setValue(pSlider);
				e.stopImmediatePropagation();
				e.preventDefault();
			});
		}

	},

	pvInitializeCreatePointElement:function(pPoints){
		var xPointElement = $(document.createElement('div')).addClass("-point");
		pPoints.append(xPointElement);
	},
	
	pvInitializeLayoutPoints: function(pSlider){
		if (pSlider.data("points").length > 0){
			pSlider.addClass("-showValuesList");
		}
		var xValuePerc;
		var xOrientation = pSlider.data("orientation");
		var xType = pSlider.data("type");
		var xPoint = pSlider.data("point");
		//Point
		for (var xI=0; xI < xPoint.length; xI++){
			xValuePerc = (xI / (xPoint.length - 1)) * 100;
			if (xOrientation == "h"){
				$(xPoint[xI]).css("left", xValuePerc + "%");
			}else{
				$(xPoint[xI]).css("top", xValuePerc + "%");
			}
		}
		//Label
		var xLabel = pSlider.data("label");
		for (var xI=0; xI < xLabel.length; xI++){
			//Texto
			$(xLabel[xI]).text($(xLabel[xI]).attr("l"));
			//Posição
			xValuePerc = (xI / (xLabel.length - 1)) * 100;
			if (xOrientation == "h"){
				$(xLabel[xI]).css("left", xValuePerc + "%");
			}else{
				$(xLabel[xI]).css("top", xValuePerc + "%");
			}
			$(xLabel[xI]).data("perc", xValuePerc / 100);
		}
	},


	jump: function(pSlider, e){
		pSlider.addClass("-selected");
		//Calcula fator em relação as coordenada do click
		var xValuePercFator = 0;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		if (pSlider.data("orientation") == "h"){
			xValuePercFator = (xXY.x - pSlider.data("content")[0].getBoundingClientRect().left) / pSlider.data("length");
		}else{
			xValuePercFator = 1 - ((xXY.y - pSlider.data("content")[0].getBoundingClientRect().top) / pSlider.data("length"));
		}
		dbsfaces.slider.pvSetValuePerc(pSlider, xValuePercFator);

		e.stopImmediatePropagation();
		e.preventDefault();
	},

	handleStart: function(pSlider, e){
		pSlider.addClass("-selected");
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		if (pSlider.data("orientation") == "h"){
			pSlider.data("dif", xXY.x);
		}else{
			pSlider.data("dif", xXY.y);
		}
		pSlider.data("pospx", pSlider.data("length") * pSlider.data("perc"));
		
		e.stopImmediatePropagation();
		e.preventDefault();
	},
	
	handleStop: function(pSlider, e){
		pSlider.data("dif", null);
		pSlider.removeClass("-selected");
		e.stopImmediatePropagation();
		e.preventDefault();
	},
	
	handleMove: function(pSlider, e){
		if (pSlider.data("dif") == null){return;}
		var xDif = pSlider.data("pospx");
		var xValuePercFator = 0;
		var xXY = dbsfaces.ui.pointerEventToXY(e);
		if (pSlider.data("orientation") == "h"){
			xDif -= pSlider.data("dif") - xXY.x;
		}else{
			xDif -= xXY.y - pSlider.data("dif");
		}
		if (xDif < 0){
			xDif = 0;
		}else if (xDif > pSlider.data("length")){
			xDif = pSlider.data("length");
		}
		xValuePercFator = xDif / pSlider.data("length");
		dbsfaces.slider.pvSetValuePerc(pSlider, xValuePercFator);
		e.stopImmediatePropagation();
		e.preventDefault();
	},

	resize: function(pSlider){
		//Atualiza dimensão
		if (pSlider.data("orientation") == "h"){
			pSlider.data("length", pSlider.data("content")[0].getBoundingClientRect().width);
		}else{
			pSlider.data("length", pSlider.data("content")[0].getBoundingClientRect().height);
		}
		dbsfaces.slider.setValue(pSlider);
	},


	setValue: function(pSlider){
//		var xValue = pSlider.data("input")[0].value
		var xValue = pSlider.data("input").attr("value");
		var xValuePercFator = 0; 
		if (pSlider.data("type") == "v"){
			var xMin = pSlider.data("min");
			var xMax = pSlider.data("max");
			var xSegmentPercFator = pSlider.data("segmentpercfator");
			var xValuesListNumeric = pSlider.data("listvaluesnumeric");
			
			xValuePercFator = dbsfaces.math.round(parseFloat(xValue.replace(/[^0-9]/g, '')), 10);
			//Procura qual o item da lista foi selecionado
			if (xValuesListNumeric.length > 0){
				//Verifica se valor ultrapassou os limites
				if (xValuePercFator < xValuesListNumeric[0]){
					xValuePercFator = xValuesListNumeric[0];
				}else if(xValuePercFator > xValuesListNumeric[xValuesListNumeric.length - 1]){
					xValuePercFator = xValuesListNumeric[xValuesListNumeric.length - 1];
				}
				//Procura item na lista
				for (var xI=0; xI < xValuesListNumeric.length; xI++){
					if (xValuesListNumeric[xI] > xValuePercFator){
						 xMax = xValuesListNumeric[xI];
						 xMin = xValuesListNumeric[xI -1];
						 break;
					}
				}
				//Calcula fator
				xValuePercFator = xSegmentPercFator * ((xValuePercFator - xMin) / (xMax - xMin));
				xValuePercFator += (xSegmentPercFator * (xI - 1));
			}else{
				//Calcula fator
				xValuePercFator = (xValuePercFator - xMin) / (xMax - xMin); 
			}
		}else{
			xValue = xValue.trim().toLowerCase();
			var xValuesList = pSlider.data("listvalues");
			//Procura qual o item da lista foi selecionado
			for (var xI=0; xI < xValuesList.length; xI++){
				if (xValuesList[xI].toLowerCase() == xValue){
					 xValuePercFator = xI / (xValuesList.length - 1);
					 break;
				}
			}
		}
		dbsfaces.slider.pvSetValuePerc(pSlider, xValuePercFator);
	},

	setInputValue(pSlider, pInputValue){
		//Salva como string
		pSlider.data("input").attr("value", dbsfaces.format.number(pInputValue, pSlider.data("dp")));
		//Salva como float
		pSlider.data("input").data("value", pInputValue);

	},

	pvSetValuePerc: function(pSlider, pValuePercFator){
		pValuePercFator = dbsfaces.math.round(parseFloat(pValuePercFator), 10);
		if (pValuePercFator > 1){
			pValuePercFator = 1;
		}else if(pValuePercFator < 0){
			pValuePercFator = 0;
		}
		var xValuePerc = pValuePercFator * 100;
		var xInputValue;
		var xValuesList = pSlider.data("listvalues");
		var xI = null;
		if (pSlider.data("type") == "v"){
			var xValuesListNumeric =pSlider.data("listvaluesnumeric");
			var xMax;
			var xMin;
			var xValuePercFator = pValuePercFator;
			var xSegmentPercFator = pSlider.data("segmentpercfator");
			//Calcula novo percentual relativo considerando o intervalo do segmento
			if (xValuesListNumeric.length > 0){
				xI = dbsfaces.math.trunc(((xValuesListNumeric.length - 1) * (pValuePercFator - 0.01)), 0);
				xValuePercFator = pValuePercFator - (xSegmentPercFator * xI);
				xValuePercFator /= xSegmentPercFator;
				xMin = parseFloat(xValuesListNumeric[xI]);
				xMax = parseFloat(xValuesListNumeric[xI + 1]);
			}else{
				xMin = pSlider.data("min");
				xMax = pSlider.data("max");
			}
			xInputValue = ((xMax - xMin) * xValuePercFator) + xMin;
			xInputValue = dbsfaces.math.round(xInputValue, pSlider.data("dp"));
		}else{
			//Encontra o valor da lista mais próximo ao percentual
			xI = dbsfaces.math.round(((xValuesList.length - 1) * pValuePercFator), 0);
			xInputValue = xValuesList[xI];
			pValuePercFator = xI / (xValuesList.length - 1);
			xValuePerc = pValuePercFator * 100;
		}
		//Salva inputValue
		dbsfaces.slider.setInputValue(pSlider, xInputValue);
		//Salva percentual relativo a coordenada
		pSlider.data("perc", pValuePercFator);
		dbsfaces.slider.pvEncodeValue(pSlider);
	},
	
	pvEncodeValue: function(pSlider){
		var xOrientation = pSlider.data("orientation");
		var xSliderValue = pSlider.data("sliderValue");
		var xHandle = pSlider.data("handle");
		var xValuePerc = pSlider.data("perc") * 100;
		if (xOrientation == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(xSliderValue, xHandle, xValuePerc);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(xSliderValue, xHandle, xValuePerc);
		}

		//Valor para ser capturado pelo pseudoselector :before:content
		xHandle.attr("v", pSlider.data("input").attr("value"));

		//Configura steps anteriores
		dbsfaces.slider.pvHideLabels(pSlider);

		//Dispara que valor foi alterado
		clearTimeout(pSlider.data("timeout"));
		pSlider.data("timeout", setTimeout(function(){
			pSlider.trigger("change");
		},0));
	},

	
	pvEncodeValueHorizontal: function(pSliderValue, pHandle, pValuePerc){
		pSliderValue.css("width", pValuePerc + "%");
		pHandle.css("left", pValuePerc + "%");
	},

	pvEncodeValueVertical: function(pSliderValue, pHandle, pValuePerc){
		pSliderValue.css("height", pValuePerc + "%");
		pHandle.css("top", 100 - pValuePerc + "%");
	},
	
	pvHideLabels: function(pSlider){
		//Label
		var xCurrentPerc = pSlider.data("perc");
		var xLabel = pSlider.data("label");
		var xType = pSlider.data("type");
		var xOrientation = pSlider.data("orientation");
		var xLabelPerc;
		var xCur;
		if (xOrientation == "h"){
			xCur = parseFloat(pSlider.data("handle").css("left"));
		}else{
			xCur = parseFloat(pSlider.data("handle").css("top"));
		}
		if (xType == "v"){
			for (var xI=0; xI < xLabel.length; xI++){
				var xLabelX = $(xLabel[xI]);
				var xSize;
				var xMin;
				var xMax;
				if (xOrientation == "h"){
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
