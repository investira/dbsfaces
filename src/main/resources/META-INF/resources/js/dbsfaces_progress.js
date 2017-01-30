dbs_progress = function(pId) {
	dbsfaces.progress.initialize($(pId));
	$(window).resize(function(e){
		dbsfaces.progress.resize($(pId));
	});
}

dbsfaces.progress = {
	initialize: function(pProgress){
		dbsfaces.progress.pvInitializeData(pProgress);
		dbsfaces.progress.resize(pProgress);
	},
	
	resize: function(pProgress){
		dbsfaces.progress.pvSetValue(pProgress, pProgress.attr("v"));
		dbsfaces.progress.pvInitializeLayout(pProgress);
	},
	
	pvInitializeData: function(pProgress){
		pProgress.data("container", pProgress.children(".-container"));
		pProgress.data("content", pProgress.data("container").children(".-content"));
		pProgress.data("label", pProgress.data("content").find(".-label").first());
		pProgress.data("labelvalue", pProgress.data("label").children(".-value"));
		pProgress.data("labelsufix", pProgress.data("label").children(".-sufix"));
		pProgress.data("point", pProgress.data("content").find("> g > .-point"));
		pProgress.data("back", pProgress.data("content").find("> g > .-back"));
		pProgress.data("type", (pProgress.hasClass("-c") ? "c": (pProgress.hasClass("-h") ? "h" : "v")));
		pProgress.data("ani", (pProgress.hasClass("-ani") ? true: false));
	},

	pvInitializeLayout: function(pProgress){
		if (pProgress.data("type") == "c"){
			dbsfaces.progress.pvInitializeLayoutCircle(pProgress);
		}else{
			dbsfaces.progress.pvInitializeLayoutHorizontalVertical(pProgress);
		}
		pProgress.removeClass("-hide");
		setTimeout(function(e){
			dbsfaces.progress.pvEncodeValue(pProgress);
		}, 0);
	},

	pvInitializeLayoutHorizontalVertical: function(pProgress){
		var xColor = tinycolor(pProgress.css("color"));
		pProgress.data("container").css("border-color", xColor.setAlpha(.2))
		   						   .css("background-color", xColor.setAlpha(.05));
		var xBackground = "linear-gradient(135deg," + xColor.setAlpha(.7) + " 0%, " + xColor.setAlpha(1) + " 100%)";
		pProgress.data("content").css("background", xBackground);
	},
	
	pvInitializeLayoutCircle: function(pProgress){
		var xColor = tinycolor(pProgress.css("color"));
		var xStops = pProgress.data("content").find("> defs > linearGradient > stop");
		$(xStops[0]).svgAttr("stop-color", xColor.setAlpha(.7));
		$(xStops[1]).svgAttr("stop-color", xColor.setAlpha(1));
		dbsfaces.progress.pvInitializeLayoutCirclePath(pProgress);
	},
	
	pvInitializeLayoutCirclePath: function(pProgress){
		var xDimension = pProgress.data("dimension");
		var xStrokeWidth = parseFloat(pProgress.data("point").css("stroke-width")) - 1;
		var xDimensionHalf = xDimension / 2; 
		var xStrokeWidthHalf = xStrokeWidth / 2;
		var xPath = "";
		pProgress.data("sw", xStrokeWidth); //Salva strokewidth
		xPath += "M" + xDimensionHalf + "," + xStrokeWidthHalf;
		xPath += "A" + (xDimensionHalf - xStrokeWidthHalf) + "," + (xDimensionHalf - xStrokeWidthHalf);
		xPath += " 0 1 1 ";
		xPath += xDimensionHalf - .0001 + "," + xStrokeWidthHalf;
		pProgress.data("point").svgAttr("d", xPath);
		pProgress.data("back").svgAttr("d", xPath);
		dbsfaces.progress.pvInitializeDash(pProgress);
	},


	pvInitializeDash: function(pProgress){
		var xPoint = pProgress.data("point")[0];
		if (typeof(xPoint.getTotalLength) == "undefined"){return;}
		var xTotalLenght = xPoint.getTotalLength();
		pProgress.data("point").css("stroke-dasharray", xTotalLenght);
		pProgress.data("point").css("stroke-dashoffset", xTotalLenght);
		pProgress.data("totallenght", xTotalLenght);
	},

	pvEncodeValue: function(pProgress){
		var xType = pProgress.data("type");
		if (xType == "c"){
			dbsfaces.progress.pvEncodeValueCircle(pProgress);
		}else if (xType == "h"){
			dbsfaces.progress.pvEncodeValueHorizontal(pProgress);
		}else{
			dbsfaces.progress.pvEncodeValueVertical(pProgress);
		}
		dbsfaces.progress.pvEncodeValueLabel(pProgress, xType);
	},

	pvEncodeValueLabel: function(pProgress, pType){
		var xDimension = pProgress.data("dimension");
		var xLabelFontSize; 
		var xLabel = pProgress.data("label");
		var xLabelValue = pProgress.data("labelvalue");
		var xLabelSufix = pProgress.data("labelsufix");
		var xValue = pProgress.attr("v");
		var xPerc = pProgress.attr("perc");
		var xClass = "";

		if (pType == "c"){
			xLabelFontSize = (xDimension - (pProgress.data("sw") * 2)) / 2.2;
			xLabel.svgAttr("x", xDimension / 2);
			xLabel.svgAttr("y", (xDimension + (xLabelFontSize / 2)) / 2);
		}else{
			xLabelFontSize = xDimension * .35;
		}
		xLabel.css("font-size", xLabelFontSize);
		if (xValue > 45){
			xLabel.addClass("-th_i");
		}else{
			xLabel.removeClass("-th_i");
		}
		pProgress.data("labelvalue").text(pProgress.data("percI"));
		pProgress.data("labelsufix").text(pProgress.data("percD") + "%");
	},
	
	pvEncodeValueHorizontal: function(pProgress){
		var xContent = pProgress.data("content");
		var xValue = pProgress.attr("v");
		xContent.css("width", xValue + "%");
	},

	pvEncodeValueVertical: function(pProgress){
		var xContent = pProgress.data("content");
		var xValue = pProgress.attr("v");
		xContent.css("height", xValue + "%");
	},

	pvEncodeValueCircle: function(pProgress){
		var xPoint = pProgress.data("point");
		var xPerc = pProgress.data("perc");
		var xTotalLenght = pProgress.data("totallenght") - (pProgress.data("totallenght") * xPerc);
		pProgress.data("point").css("stroke-dashoffset", xTotalLenght);
	},
	
	pvSetValue: function(pProgress, pValue){
		pValue = dbsfaces.math.round(parseFloat(pValue),2);
		xInt = String(dbsfaces.math.trunc(pValue, 0)); //Parte interira
		xDec = String(dbsfaces.math.round(pValue - xInt, 4)).substring(1, 4); //Parte decimal
		pProgress.attr("v", pValue);
		pProgress.data("percI", xInt);
		pProgress.data("percD", xDec);
		pProgress.data("perc", (pValue / 100));
		pProgress.data("dimension", Math.min(pProgress[0].getBoundingClientRect().height, pProgress[0].getBoundingClientRect().width));
	}

}
