dbs_progress = function(pId) {
	dbsfaces.progress.initialize($(pId));
	$(window).resize(function(e){
		dbsfaces.progress.resize($(pId).data("data"));
	});
}

dbsfaces.progress = {
	initialize: function(pProgress){
		var xProgressData = dbsfaces.progress.pvInitializeData(pProgress);
		dbsfaces.progress.resize(xProgressData);
	},
	
	resize: function(pProgressData){
		pProgressData.dom.self.addClass("-hide");
		dbsfaces.progress.pvSetValue(pProgressData, pProgressData.dom.self.attr("v"));
		dbsfaces.progress.pvInitializeLayout(pProgressData);
	},
	
	pvInitializeData: function(pProgress){
		var xData = {
			dom : {
				self: pProgress,
				container: pProgress.children(".-container"),
				content: null,
				progress: null,
				label: null,
				labelValue: null,
				labelSufix: null,
				point: null,
				back: null
			},
			type: (pProgress.hasClass("-c") ? "c": (pProgress.hasClass("-h") ? "h" : "v")),
			ani: (pProgress.hasClass("-ani") ? true: false),
			percFator: 0, //percentual em fator
			percI: 0, //parte inteira do percentual
			percD: 0, //parte decimal do percentual
			dimension: 0, //Comprimento do slider,
			sw: 0, //stroke width,
			totalLength: 0 //Comprimento do path do slider
		}
		pProgress.data("data", xData);
		xData.dom.content = xData.dom.container.children(".-content");
		xData.dom.progress = xData.dom.content.children(".-progress");
		xData.dom.label = xData.dom.progress.find(".-label").first();
		xData.dom.labelValue = xData.dom.label.children(".-value");
		xData.dom.labelSufix = xData.dom.label.children(".-sufix");
		xData.dom.point = xData.dom.content.find("> g > .-point");
		xData.dom.back = xData.dom.content.find("> g > .-back");
		return xData;
	},

	pvInitializeLayout: function(pProgressData){
		if (pProgressData.type == "c"){
			dbsfaces.progress.pvInitializeLayoutCircle(pProgressData);
		}else{
			dbsfaces.progress.pvInitializeLayoutHorizontalVertical(pProgressData);
		}
		pProgressData.dom.self.removeClass("-hide");
		setTimeout(function(e){
			dbsfaces.progress.pvEncodeValue(pProgressData);
		}, 0);
	},

	pvInitializeLayoutHorizontalVertical: function(pProgressData){
		var xColor = tinycolor(pProgressData.dom.self.css("color"));
		pProgressData.dom.content.css("border-color", xColor.setAlpha(.2))
		   						 .css("background-color", xColor.setAlpha(.05));
		pProgressData.dom.progress.css("color", xColor.setAlpha(1).invertLightness());
		var xBackground = "linear-gradient(135deg," + xColor.setAlpha(.70) + " 0%, " + xColor.setAlpha(1) + " 100%)";
		pProgressData.dom.progress.css("background", xBackground);
	},
	
	pvInitializeLayoutCircle: function(pProgressData){
		var xColor = tinycolor(pProgressData.dom.self.css("color"));
		var xStops = pProgressData.dom.content.find("> defs > linearGradient > stop");
		$(xStops[0]).svgAttr("stop-color", xColor.setAlpha(.7));
		$(xStops[1]).svgAttr("stop-color", xColor.setAlpha(1));
		dbsfaces.progress.pvInitializeLayoutCirclePath(pProgressData);
	},
	
	pvInitializeLayoutCirclePath: function(pProgressData){
		var xStrokeWidth = parseFloat(pProgressData.dom.point.css("stroke-width")) - 1;
		var xDimensionHalf = pProgressData.dimension / 2; 
		var xStrokeWidthHalf = xStrokeWidth / 2;
		var xPath = "";
		pProgressData.sw = xStrokeWidth; //Salva strokewidth
		xPath += "M" + xDimensionHalf + "," + xStrokeWidthHalf;
		xPath += "A" + (xDimensionHalf - xStrokeWidthHalf) + "," + (xDimensionHalf - xStrokeWidthHalf);
		xPath += " 0 1 1 ";
		xPath += xDimensionHalf - .0001 + "," + xStrokeWidthHalf;
		pProgressData.dom.point.svgAttr("d", xPath);
		pProgressData.dom.back.svgAttr("d", xPath);
		dbsfaces.progress.pvInitializeDash(pProgressData);
	},


	pvInitializeDash: function(pProgressData){
		var xPoint = pProgressData.dom.point[0];
		if (typeof(xPoint.getTotalLength) == "undefined"){return;}
		pProgressData.totalLength = xPoint.getTotalLength();
		pProgressData.dom.point.css("stroke-dasharray", pProgressData.totalLength);
		pProgressData.dom.point.css("stroke-dashoffset", pProgressData.totalLength);
	},

	pvEncodeValue: function(pProgressData){
		if (pProgressData.type == "c"){
			dbsfaces.progress.pvEncodeValueCircle(pProgressData);
		}else if (pProgressData.type == "h"){
			dbsfaces.progress.pvEncodeValueHorizontal(pProgressData);
		}else{
			dbsfaces.progress.pvEncodeValueVertical(pProgressData);
		}
		dbsfaces.progress.pvEncodeValueLabel(pProgressData);
	},

	pvEncodeValueLabel: function(pProgressData){
		var xLabelFontSize; 
		if (pProgressData.type == "c"){
			xLabelFontSize = (pProgressData.dimension - (pProgressData.sw * 2)) / 2.2;
			pProgressData.dom.label.svgAttr("x", pProgressData.dimension / 2)
								   .svgAttr("y", (pProgressData.dimension + (xLabelFontSize / 2)) / 2);
		}else{
			xLabelFontSize = pProgressData.dimension * .35;
		}
		pProgressData.dom.label.css("font-size", xLabelFontSize);
		pProgressData.dom.labelValue.text(pProgressData.percI);
		pProgressData.dom.labelSufix.text(pProgressData.percD + "%");
	},
	
	pvEncodeValueHorizontal: function(pProgressData){
		pProgressData.dom.progress.css("width", pProgressData.dom.self.attr("v") + "%");
	},

	pvEncodeValueVertical: function(pProgressData){
		pProgressData.dom.progress.css("height", pProgressData.dom.self.attr("v") + "%");
	},

	pvEncodeValueCircle: function(pProgressData){
		var xTotalLenght = pProgressData.totalLength - (pProgressData.totalLength * pProgressData.percFator);
		pProgressData.dom.point.css("stroke-dashoffset", xTotalLenght);
	},
	
	pvSetValue: function(pProgressData, pValue){
		pValue = dbsfaces.math.round(parseFloat(pValue),2);
		xInt = String(dbsfaces.math.trunc(pValue, 0)); //Parte interira
		xDec = String(dbsfaces.math.round(pValue - xInt, 4)).substring(1, 4); //Parte decimal
		pProgressData.dom.self.attr("v", pValue);
		pProgressData.percI = xInt;
		pProgressData.percD = xDec;
		pProgressData.percFator = (pValue / 100);
		pProgressData.dimension = Math.min(pProgressData.dom.self[0].getBoundingClientRect().height, pProgressData.dom.self[0].getBoundingClientRect().width);
		pProgressData.dom.self.val(pProgressData.percFator);
	}

}
