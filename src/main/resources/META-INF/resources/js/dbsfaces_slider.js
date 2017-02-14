dbs_slider = function(pId) {
	dbsfaces.slider.initialize($(pId));
	$(window).resize(function(e){
		dbsfaces.slider.resize($(pId));
	});
}

dbsfaces.slider = {
	initialize: function(pSlider){
		dbsfaces.slider.pvInitializeData(pSlider);
		dbsfaces.slider.resize(pSlider);
	},
	
	resize: function(pSlider){
		dbsfaces.slider.pvSetValue(pSlider, pSlider.attr("v"));
		dbsfaces.slider.pvInitializeLayout(pSlider);
	},
	
	pvInitializeData: function(pSlider){
		pSlider.data("type", (pSlider.hasClass("-h") ? "h" : "v"));
		pSlider.data("container", pSlider.children(".-container"));
		pSlider.data("content", pSlider.data("container").children(".-content"));
		pSlider.data("handle", pSlider.data("container").children(".-handle"));
		pSlider.data("slider", pSlider.data("content").children(".-slider"));
		pSlider.data("label", pSlider.data("slider").find(".-label").first());
		pSlider.data("labelvalue", pSlider.data("label").children(".-value"));
		pSlider.data("labelsufix", pSlider.data("label").children(".-sufix"));
		pSlider.data("point", pSlider.data("content").find("> g > .-point"));
		pSlider.data("back", pSlider.data("content").find("> g > .-back"));
		pSlider.data("ani", (pSlider.hasClass("-ani") ? true: false));
	},

	pvInitializeLayout: function(pSlider){
		dbsfaces.slider.pvInitializeLayoutHorizontalVertical(pSlider);
		pSlider.removeClass("-hide");
		setTimeout(function(e){
			dbsfaces.slider.pvEncodeValue(pSlider);
		}, 0);
	},

	pvInitializeLayoutHorizontalVertical: function(pSlider){
		var xColor = tinycolor(pSlider.css("color"));
		pSlider.data("content").css("border-color", xColor.setAlpha(.2))
		   						 .css("background-color", xColor.setAlpha(.05))
		pSlider.data("slider").css("color", xColor.setAlpha(1).invertLightness());
		var xBackground = "linear-gradient(135deg," + xColor.setAlpha(.70) + " 0%, " + xColor.setAlpha(1) + " 100%)";

		pSlider.data("slider").css("background", xBackground);
	},
	
	pvEncodeValue: function(pSlider){
		var xType = pSlider.data("type");
		if (xType == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(pSlider);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(pSlider);
		}
		dbsfaces.slider.pvEncodeValueLabel(pSlider, xType);
	},

	pvEncodeValueLabel: function(pSlider, pType){
		var xDimension = pSlider.data("dimension");
		var xLabelFontSize; 
		var xLabel = pSlider.data("label");
		var xLabelValue = pSlider.data("labelvalue");
		var xLabelSufix = pSlider.data("labelsufix");
		var xValue = pSlider.attr("v");
		var xPerc = pSlider.attr("perc");
		var xClass = "";

		pSlider.data("labelvalue").text(pSlider.data("percI"));
		pSlider.data("labelsufix").text(pSlider.data("percD") + "%");
	},
	
	pvEncodeValueHorizontal: function(pSlider){
		var xContent = pSlider.data("slider");
		var xHandle = pSlider.data("handle");
		var xValue = pSlider.attr("v");
		xContent.css("width", xValue + "%");
		xHandle.css("left", xValue + "%");
	},

	pvEncodeValueVertical: function(pSlider){
		var xContent = pSlider.data("slider");
		var xHandle = pSlider.data("handle");
		var xValue = pSlider.attr("v");
		xContent.css("height", xValue + "%");
		xHandle.css("top", 100 - xValue + "%");
	},

	pvSetValue: function(pSlider, pValue){
		pValue = dbsfaces.math.round(parseFloat(pValue),2);
		xInt = String(dbsfaces.math.trunc(pValue, 0)); //Parte interira
		xDec = String(dbsfaces.math.round(pValue - xInt, 4)).substring(1, 4); //Parte decimal
		pSlider.attr("v", pValue);
		pSlider.data("percI", xInt);
		pSlider.data("percD", xDec);
		pSlider.data("perc", (pValue / 100));
		pSlider.data("dimension", Math.min(pSlider[0].getBoundingClientRect().height, pSlider[0].getBoundingClientRect().width));
	}

}
