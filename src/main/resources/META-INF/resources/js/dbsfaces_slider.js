dbs_slider = function(pId, pListValues, pMinValue, pMaxValue) {
	dbsfaces.slider.initialize($(pId), pListValues, pMinValue, pMaxValue);
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
	initialize: function(pSlider, pListValues, pMinValue, pMaxValue){
		dbsfaces.slider.pvInitializeData(pSlider, pListValues, pMinValue, pMaxValue);
		dbsfaces.slider.pvInitializeLayout(pSlider);
	},

	
	pvInitializeData: function(pSlider, pListValues, pMinValue, pMaxValue){
		pSlider.data("type", pSlider.attr("type"));
		pSlider.data("orientation", (pSlider.hasClass("-h") ? "h" : "v"));
		pSlider.data("listvalues", pListValues);
		pSlider.data("min", parseFloat(pMinValue));
		pSlider.data("max", parseFloat(pMaxValue));
		pSlider.data("container", pSlider.children(".-container"));
		pSlider.data("content", pSlider.data("container").children(".-content"));
		pSlider.data("input", pSlider.data("container").children(".-th_input-data"));
		pSlider.data("slider", pSlider.data("content").children(".-slider"));
		pSlider.data("handle", pSlider.data("content").children(".-handle"));
		pSlider.data("points", pSlider.data("content").children(".-points"));
		pSlider.data("point", pSlider.data("points").children(".-point"));
		pSlider.data("label", pSlider.data("points").children(".-label"));
		pSlider.data("ani", (pSlider.hasClass("-ani") ? true: false));
		pSlider.data("dif", null);

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
//		var xInverted = xColor.invertLightness().setAlpha(1);
		pSlider.data("content").css("background-color", xColor2.setAlpha(.3));
		var xBackground;
		xColor2.setAlpha(.70);
		xBackground = "linear-gradient(135deg," + xColor2 + " 0%, " + xColor + " 100%)";
		pSlider.data("slider").css("background", xBackground);
		
		xColor2.setAlpha(1);
		if (xColor.isDark()){
			xColor2.lighten(10);
			xBackground = "linear-gradient(135deg," + xColor2 + " 0%, " + xColor + " 100%)";
		}else{
			xColor2.darken(10);
			xBackground = "linear-gradient(135deg," + xColor + " 0%, " + xColor2 + " 100%)";
		}
		pSlider.data("handle").css("background", xBackground);
	},

	pvInitializeLayoutPoints: function(pSlider){
		var xPoint = pSlider.data("point");
		var xValuePerc;
		for (var xI=0; xI < xPoint.length; xI++){
			xValuePerc = (xI / (xPoint.length - 1)) * 100;
			if (pSlider.data("orientation") == "h"){
				$(xPoint[xI]).css("left", xValuePerc + "%");
			}else{
				$(xPoint[xI]).css("top", xValuePerc + "%");
			}
		}
		var xLabel = pSlider.data("label");
		for (var xI=0; xI < xLabel.length; xI++){
			xValuePerc = (xI / (xLabel.length - 1)) * 100;
			if (pSlider.data("orientation") == "h"){
				$(xLabel[xI]).css("left", xValuePerc + "%");
			}else{
				$(xLabel[xI]).css("top", xValuePerc + "%");
			}
		}
	},


	jump: function(pSlider, e){
		pSlider.addClass("-selected");
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
		if (pSlider.data("orientation") == "h"){
			pSlider.data("length", pSlider.data("content")[0].getBoundingClientRect().width);
		}else{
			pSlider.data("length", pSlider.data("content")[0].getBoundingClientRect().height);
		}
		dbsfaces.slider.pvSetValue(pSlider);
	},


	pvSetValue: function(pSlider){
//		var xValue = pSlider.data("input")[0].value
		var xValue = pSlider.data("input").attr("value");
		var xValuePercFator = 0; 
		if (pSlider.data("type") == "v"){
			xValuePercFator = dbsfaces.math.round(parseFloat(xValue),2);
			xValuePercFator = (xValuePercFator - pSlider.data("min")) / (pSlider.data("max") - pSlider.data("min")); 
		}else{
			xValue = xValue.trim().toLowerCase();
			var xListValues = pSlider.data("listvalues");
			//Procura qual o item da lista foi selecionado
			for (var xI=0; xI < xListValues.length; xI++){
				if (xListValues[xI].toLowerCase() == xValue){
					 xValuePercFator = xI / (xListValues.length - 1);
					 break;
				}
			}
		}
		dbsfaces.slider.pvSetValuePerc(pSlider, xValuePercFator);
	},


	pvSetValuePerc: function(pSlider, pValuePercFator){
		pValuePercFator = dbsfaces.math.round(parseFloat(pValuePercFator), 4);
		var xValuePerc = pValuePercFator * 100;
		if (pSlider.data("type") == "v"){
			
//			pSlider.data("input")[0].value = (pSlider.data("max") - pSlider.data("min")) * pValuePercFator + pSlider.data("min");
			pSlider.data("input").attr("value", (pSlider.data("max") - pSlider.data("min")) * pValuePercFator + pSlider.data("min"));
		}else{
			var xListValues = pSlider.data("listvalues");
			var xI = dbsfaces.math.round(((xListValues.length - 1) * pValuePercFator),0);
//			pSlider.data("input")[0].value = xListValues[xI];
			
			pSlider.data("input").attr("value", xListValues[xI]);
			pValuePercFator = xI / (xListValues.length - 1);
			xValuePerc = pValuePercFator * 100;
		}
		xInt = String(dbsfaces.math.trunc(xValuePerc, 0)); //Parte inteira
		xDec = String(dbsfaces.math.round(xValuePerc - xInt, 4)).substring(1, 4); //Parte decimal
		pSlider.data("percI", xInt);
		pSlider.data("percD", xDec);
		pSlider.data("perc", pValuePercFator);
		dbsfaces.slider.pvEncodeValue(pSlider);
	},

	pvEncodeValue: function(pSlider){
		var xOrientation = pSlider.data("orientation");
		var xContentSlider = pSlider.data("slider");
		var xHandle = pSlider.data("handle");
		var xValuePerc = pSlider.data("perc") * 100;
		if (xOrientation == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(xContentSlider, xHandle, xValuePerc);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(xContentSlider, xHandle, xValuePerc);
		}
		dbsfaces.slider.pvEncodeValueLabel(pSlider, xOrientation);

		clearTimeout(pSlider.data("timeout"));
		pSlider.data("timeout", setTimeout(function(){
			pSlider.trigger("change");
		},0));
	},

	
	pvEncodeValueHorizontal: function(pContent, pHandle, pValuePerc){
		pContent.css("width", pValuePerc + "%");
		pHandle.css("left", pValuePerc + "%");
	},

	pvEncodeValueVertical: function(pContentSlider, pHandle, pValuePerc){
		pContentSlider.css("height", pValuePerc + "%");
		pHandle.css("top", 100 - pValuePerc + "%");
	},

	pvEncodeValueLabel: function(pSlider, pOrientation){
//		var xDimension = pSlider.data("dimension");
//		var xLabelFontSize; 
//		var xLabel = pSlider.data("label");
//		var xLabelValue = pSlider.data("labelvalue");
//		var xLabelSufix = pSlider.data("labelsufix");
//		var xValue = pSlider.attr("v");
//		var xPerc = pSlider.attr("perc");
//		var xClass = "";

//		pSlider.data("labelvalue").text(pSlider.data("percI"));
//		pSlider.data("labelsufix").text(pSlider.data("percD") + "%");
	}

}
