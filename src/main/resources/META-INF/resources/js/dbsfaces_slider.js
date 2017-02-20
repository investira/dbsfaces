dbs_slider = function(pId) {
	dbsfaces.slider.initialize($(pId));
	$(window).resize(function(e){
		dbsfaces.slider.resize($(pId));
	});
	
	$(pId + " > .-container > .-content").on("mousedown touchstart", function(e){
		dbsfaces.slider.jump($(pId), e);
	});
	$(pId + " > .-container > .-handle").on("mousedown touchstart", function(e){
		dbsfaces.slider.handleStart($(pId), e);
	});
	$(pId + " > .-container > .-handle").on("mouseup touchend", function(e){
		dbsfaces.slider.handleStop($(pId), e);
	});
	$(pId + " > .-container > .-handle").on("mouseleave", function(e){
		dbsfaces.slider.handleStop($(pId), e);
	});
	$(pId + " > .-container > .-handle").on("mousemove touchmove", function(e){
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
	initialize: function(pSlider){
		dbsfaces.slider.pvInitializeData(pSlider);
		dbsfaces.slider.pvInitializeLayout(pSlider);
		dbsfaces.slider.resize(pSlider);
	},

	jump: function(pSlider, e){
		var xValue = 0;
		console.log(e.originalEvent.offsetY + "\t" + e.originalEvent.offsetX + "\t" + pSlider.data("length") + "\t" + pSlider.data("content")[0].getBoundingClientRect().top);
		if (pSlider.data("orientation") == "h"){
			xValue = (e.originalEvent.clientX - pSlider.data("content")[0].getBoundingClientRect().left) / pSlider.data("length");
		}else{
			xValue = 1 - ((e.originalEvent.clientY - pSlider.data("content")[0].getBoundingClientRect().top) / pSlider.data("length"));
		}
		dbsfaces.slider.pvSetValue(pSlider, xValue * 100);
		e.stopImmediatePropagation();
		e.preventDefault();
	},

	handleStart: function(pSlider, e){
		pSlider.addClass("-selected");
		if (pSlider.data("orientation") == "h"){
			pSlider.data("dif", e.originalEvent.pageX);
		}else{
			pSlider.data("dif", e.originalEvent.pageY);
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
		var xValue = 0;
		if (pSlider.data("orientation") == "h"){
			xDif -= pSlider.data("dif") - e.originalEvent.pageX;
		}else{
			xDif -= e.originalEvent.pageY - pSlider.data("dif");
		}
		if (xDif < 0){
			xDif = 0;
		}else if (xDif > pSlider.data("length")){
			xDif = pSlider.data("length");
		}
		xValue = (xDif / pSlider.data("length")) * 100;
//		console.log(pSlider.data("dif") + "\t" + e.originalEvent.pageX + "\t" + e.originalEvent.pageY + "\t" + xDif + "\t" + pSlider.data("length"));
		dbsfaces.slider.pvSetValue(pSlider, xValue);
		e.stopImmediatePropagation();
		e.preventDefault();
	},

	resize: function(pSlider){
		if (pSlider.data("orientation") == "h"){
			pSlider.data("length", pSlider.data("content")[0].getBoundingClientRect().width);
		}else{
			pSlider.data("length", pSlider.data("content")[0].getBoundingClientRect().height);
		}
		dbsfaces.slider.pvSetValue(pSlider, pSlider.attr("v"));
//		dbsfaces.slider.pvInitializeLayout(pSlider);
	},
	
	pvInitializeData: function(pSlider){
		pSlider.data("type", pSlider.attr("type"));
		pSlider.data("orientation", (pSlider.hasClass("-h") ? "h" : "v"));
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
		pSlider.data("dif", null);

	},

	pvInitializeLayout: function(pSlider){
		dbsfaces.slider.pvInitializeLayoutHorizontalVertical(pSlider);
		pSlider.removeClass("-hide");
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
		var xOrientation = pSlider.data("orientation");
		if (xOrientation == "h"){
			dbsfaces.slider.pvEncodeValueHorizontal(pSlider);
		}else{
			dbsfaces.slider.pvEncodeValueVertical(pSlider);
		}
		dbsfaces.slider.pvEncodeValueLabel(pSlider, xOrientation);
	},

	pvEncodeValueLabel: function(pSlider, pOrientation){
//		var xDimension = pSlider.data("dimension");
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
		xInt = String(dbsfaces.math.trunc(pValue, 0)); //Parte inteira
		xDec = String(dbsfaces.math.round(pValue - xInt, 4)).substring(1, 4); //Parte decimal
		pSlider.attr("v", pValue);
		pSlider.data("percI", xInt);
		pSlider.data("percD", xDec);
		pSlider.data("perc", (pValue / 100));
		setTimeout(function(e){
			dbsfaces.slider.pvEncodeValue(pSlider);
		}, 0);
//		pSlider.data("dimension", Math.min(pSlider[0].getBoundingClientRect().height, pSlider[0].getBoundingClientRect().width));
	}

}
