dbs_inputNumber = function(pId, pInputData, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits) {

	if (!dbsfaces.util.isMobile()){
		//Retirado para não dar erro no firefox desktop 
		$(pInputData).attr("pattern", null);
	}
	
	var xDBSMask = $(pInputData).dbsmask({
		type: pType,  
		mask: pMask,
		maskEmptyChr: pMaskEmptyChr,
        decDigits: pDecDigits,
		groupSymbol: pGroupSymbol, 
        groupDigits: pGroupDigits,   
        decSymbol: pDecSymbol, 
        stripMask: false  
	});

	if (!$(pId).attr("increment")){return;}
	
	dbsfaces.inputNumber.initialize($(pId), $(pInputData), xDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits);
	
//	$(pInputData).on("focusin", function(e){
//		dbsfaces.inputNumber.open($(pId));
//	});
//
//	$(pInputData).on("focusout", function(e){
//		if ($(pId).data("action")){
//			$(pId).data("action", null);
//			e.preventDefault();
//			return false;
//		}
//	});

//	$(pInputData).on("blur", function(e){
//		dbsfaces.inputNumber.close($(pId));
//	});


//	$(pId + " > .-container > .-input > .-buttons > .-container > .-down").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "d");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-up").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "u");
//	});
	
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-direction").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.invert($(pId), e);
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-delete").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.reset($(pId), e, "delete");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_mm").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), "mm");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_cm").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "cm");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_xm").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "xm");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_m").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "m");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_c").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "c");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_x").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.increment($(pId), e, "x");
//	});
//
//	$(pId + " > .-container > .-input > .-buttons > .-container > .-close").on("mousedown touchstart", function(e){
//		dbsfaces.inputNumber.close($(pId), e);
//	});
	
//	$(pId + " > .-container > .-input > .-buttons > .-container > *").on("mouseup touchend", function(e){
//		dbsfaces.inputNumber.stopIncrement($(pId));
//	});
//	
//	$(pId + " > .-container > .-input > .-buttons > .-container > *").on("mouseleave", function(e){
//		dbsfaces.inputNumber.stopIncrement($(pId));
//	});



}

dbsfaces.inputNumber = {

	initialize: function(pInputNumber, pInputData, pDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits){
		pInputNumber.data("inputdata", pInputData);
		pInputNumber.data("dbsmask", pDBSMask);
		pInputNumber.data("type", pType);
		pInputNumber.data("mask", pMask);
		pInputNumber.data("maskemptychr", pMaskEmptyChr);
		pInputNumber.data("decdigits", Math.pow(10, parseFloat(pDecDigits)));
		pInputNumber.data("groupsymbol", pGroupSymbol);
		pInputNumber.data("decsymbol", pDecSymbol);
		pInputNumber.data("groupdigits", pGroupDigits);
		pInputNumber.data("container", pInputNumber.children(".-container"));
		pInputNumber.data("input", pInputNumber.data("container").children(".-input"));
		pInputNumber.data("buttonscontainer", pInputNumber.find(" > .-container > .-input > .-buttons > .-container"));
		pInputNumber.data("direction", pInputNumber.data("buttonscontainer").children(".-direction"));
		var xColor = tinycolor(pInputNumber.css("color")).invertLightness();
		pInputNumber.data("buttonscontainer").children(".-th_action").css("color", xColor.toHslString());
		pInputNumber.data("increment", 1);
		pInputNumber.data("repeat", 0);
		pInputNumber.data("maxvalue", parseFloat(pInputData.attr("maxvalue")) * pInputNumber.data("decdigits"));
		pInputNumber.data("minvalue", parseFloat(pInputData.attr("minvalue")) * pInputNumber.data("decdigits"));
	},


	increment: function(pInputNumber, e, pAction){
		dbsfaces.inputNumber.pvCancelBlur(pInputNumber, e);
		dbsfaces.inputNumber.stopIncrement(pInputNumber);
//		var xDBSMask = pInputNumber.data("dbsmask");
//		var xValue = parseFloat(xDBSMask.stripMask());
//		pInputNumber.data("increment", (Math.pow(10, Math.abs(xValue).toString().length - 1) / pInputNumber.data("decdigits")));
		dbsfaces.inputNumber.pvIncrement(pInputNumber, pAction);
		pInputNumber.data("timeout", setTimeout(function() {
										pInputNumber.data("interval", setInterval(function() {dbsfaces.inputNumber.pvIncrement(pInputNumber, pAction)}, 50));
								  	}, 1000)
	  					 );

	},
	
	pvIncrement: function(pInputNumber, pAction){
		if (!pInputNumber.data("action")){
			dbsfaces.inputNumber.stopIncrement(pInputNumber);
			return;
		}
//		if (pAction == "mm"){
//			xIncrement = 1000000;
//		}else if (pAction == "cm"){
//			xIncrement = 100000;
//		}else if (pAction == "xm"){
//			xIncrement = 10000;
//		}else if (pAction == "m"){
//			xIncrement = 1000;
//		}else if (pAction == "c"){
//			xIncrement = 100;
//		}else if (pAction == "x"){
//			xIncrement = 10;
//		}
		
		var xInputData = pInputNumber.data("inputdata");
		var xDBSMask = pInputNumber.data("dbsmask");
		var xSign = (xInputData.attr("n") == "-" ? -1 : 1);
		var xValue = parseFloat(xDBSMask.stripMask());
		var xDecDigits = pInputNumber.data("decdigits");
		var xIncrement = pInputNumber.data("increment");
		if (pAction == "d"){
			xIncrement *= -1;
		}
		xIncrement *= xDecDigits;
//		if (pInputNumber.data("direction").hasClass("-up")){
			xValue = (parseFloat(xDBSMask.stripMask()) * xSign) + xIncrement;
//		}else{
//			xValue = (parseFloat(xDBSMask.stripMask()) * xSign) - xIncrement;
//		}
		if (xValue <= pInputNumber.data("maxvalue")
		 && xValue >= pInputNumber.data("minvalue")){
			xInputData[0].value = xValue;
			if (xInputData[0].value > 0){
				xInputData.attr("n","");
			}else{
				xInputData.attr("n","-");
			}
			xDBSMask.formatNumber();
			var xRepeat = pInputNumber.data("repeat");
			console.log(xValue.toString().length + "\t" + xIncrement + "\t" + xRepeat + "\t" + xValue + "\t" + (Math.pow(10, xValue.toString().length - 1)));
			if ((xValue + xIncrement).toString().length != Math.abs(xValue).toString().length
			|| xRepeat >= 20){
				if (pAction == "d"){
					if (xValue.toString().length > 2){
						xIncrement = Math.pow(10, Math.abs(xValue).toString().length - 2) / xDecDigits;
					}else{
						xIncrement = 1;
					}
				}else{
					xIncrement = Math.pow(10, Math.abs(xValue).toString().length - 1) / xDecDigits;
				}
//				pInputNumber.data("increment", xIncrement);
//				if ((xValue % xIncrement) == 0){
					pInputNumber.data("increment", xIncrement);
					pInputNumber.data("repeat", 0);
//				}
//			if (xRepeat >= 20){
//				if (pAction == "d"){
//					xIncrement = Math.pow(10, Math.abs(xValue).toString().length - 2);
//				}else{
//					xIncrement = Math.pow(10, Math.abs(xValue).toString().length - 1);
//				}
//				xIncrement /= xDecDigits;
//				if ((xValue % xIncrement) == 0){
//					pInputNumber.data("increment", xIncrement);
//					pInputNumber.data("repeat", 0);
//				}
			}else{
				pInputNumber.data("repeat", xRepeat + 1);
			}
			
		}
	},
	
	open: function(pInputNumber){
		pInputNumber.data("input").addClass("-selected");
		if (dbsfaces.util.isMobile()){
			setTimeout(function(e){
				var xDBSMask = pInputNumber.data("dbsmask");
				xDBSMask.setEnd();
			},10);
		}
	},
	
	close: function(pInputNumber, e){
		pInputNumber.data("container").find("*").removeClass("-selected");
		dbsfaces.inputNumber.pvCancelBlur(pInputNumber, e);
	},
	
	reset: function(pInputNumber, e){
		var xDBSMask = pInputNumber.data("dbsmask");
		var xInputData = pInputNumber.data("inputdata");
		xInputData[0].value = 0;
		xDBSMask.formatNumber();
		dbsfaces.inputNumber.pvCancelBlur(pInputNumber, e);
	},
	
	invert: function(pInputNumber, e){
		if (pInputNumber.data("direction").hasClass("-up")){
			pInputNumber.data("direction").removeClass("-up")
										  .addClass("-down");
		}else{
			pInputNumber.data("direction").removeClass("-down")
			  							  .addClass("-up");
		}
		dbsfaces.inputNumber.pvCancelBlur(pInputNumber, e);
	},
	
	stopIncrement: function(pInputNumber){
		clearInterval(pInputNumber.data("interval"));
		clearTimeout(pInputNumber.data("timeout"));
		pInputNumber.data("repeat", 0);
	},
	
	
	pvCancelBlur: function(pInputNumber, e){
		if (e != null && typeof(e) != "undefined"){
			var xDBSMask = pInputNumber.data("dbsmask");
			xDBSMask.setEnd();
			pInputNumber.data("action", true);
			e.preventDefault();
			e.stopImmediatePropagation();
		}
	},
}

