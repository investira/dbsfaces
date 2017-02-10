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
	
	$(pInputData).on("focusin", function(e){
		dbsfaces.inputNumber.open($(pId));
	});

	$(pInputData).on("focusout", function(e){
		if ($(pId).data("action")){
			$(pId).data("action", null);
			e.preventDefault();
			return false;
		}
	});

	$(pInputData).on("blur", function(e){
		dbsfaces.inputNumber.close($(pId));
	});

	
	$(pId + " > .-container > .-input > .-buttons > .-container > .-direction").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.invert($(pId));
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-delete").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.reset($(pId), "delete");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_mm").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.increment($(pId), "mm");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_cm").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.increment($(pId), "cm");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_xm").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.increment($(pId), "xm");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_m").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.increment($(pId), "m");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_c").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.increment($(pId), "c");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-op_x").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.increment($(pId), "x");
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});

	$(pId + " > .-container > .-input > .-buttons > .-container > .-close").on("mousedown touchstart", function(e){
		dbsfaces.inputNumber.close($(pId));
		dbsfaces.inputNumber.cancelBlur($(pId), e);
	});
	



}

dbsfaces.inputNumber = {

	initialize: function(pInputNumber, pInputData, pDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits){
		pInputNumber.data("inputdata", pInputData);
		pInputNumber.data("dbsmask", pDBSMask);
		pInputNumber.data("type", pType);
		pInputNumber.data("mask", pMask);
		pInputNumber.data("maskemptychr", pMaskEmptyChr);
		pInputNumber.data("decdigits", parseFloat(pDecDigits));
		pInputNumber.data("groupsymbol", pGroupSymbol);
		pInputNumber.data("decsymbol", pDecSymbol);
		pInputNumber.data("groupdigits", pGroupDigits);
		pInputNumber.data("container", pInputNumber.children(".-container"));
		pInputNumber.data("input", pInputNumber.data("container").children(".-input"));
		pInputNumber.data("buttonscontainer", pInputNumber.find(" > .-container > .-input > .-buttons > .-container"));
		pInputNumber.data("direction", pInputNumber.data("buttonscontainer").children(".-direction"));
		var xColor = tinycolor(pInputNumber.css("color")).invertLightness();
		pInputNumber.data("buttonscontainer").children(".-th_action").css("color", xColor.toHslString());
	},


	increment: function(pInputNumber, pAction){
		var xDBSMask = pInputNumber.data("dbsmask");
		xDBSMask.setEnd();
		if (pAction == "mm"){
			dbsfaces.inputNumber.pvIncrement(pInputNumber, 1000000);
		}else if (pAction == "cm"){
			dbsfaces.inputNumber.pvIncrement(pInputNumber, 100000);
		}else if (pAction == "xm"){
			dbsfaces.inputNumber.pvIncrement(pInputNumber, 10000);
		}else if (pAction == "m"){
			dbsfaces.inputNumber.pvIncrement(pInputNumber, 1000);
		}else if (pAction == "c"){
			dbsfaces.inputNumber.pvIncrement(pInputNumber, 100);
		}else if (pAction == "x"){
			dbsfaces.inputNumber.pvIncrement(pInputNumber, 10);
		}
	},
	
	pvIncrement: function(pInputNumber, pIncrement){
		var xInputData = pInputNumber.data("inputdata");
		var xDBSMask = pInputNumber.data("dbsmask");
		var xSign = (xInputData.attr("n") == "-" ? -1 : 1);
		var xValue;
		var xDecDigits = Math.pow(10, pInputNumber.data("decdigits"));
		pIncrement *= xDecDigits;
		if (pInputNumber.data("direction").hasClass("-up")){
			xValue = (parseFloat(xDBSMask.stripMask()) * xSign) + pIncrement;
		}else{
			xValue = (parseFloat(xDBSMask.stripMask()) * xSign) - pIncrement;
		}
		if (xValue <= (parseFloat(xInputData.attr("maxvalue")) * xDecDigits)
		 && xValue >= (parseFloat(xInputData.attr("minvalue")) * xDecDigits)){
			xInputData[0].value = xValue;
			if (xInputData[0].value > 0){
				xInputData.attr("n","");
			}else{
				xInputData.attr("n","-");
			}
			xDBSMask.formatNumber();
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
	
	close: function(pInputNumber){
		pInputNumber.data("container").find("*").removeClass("-selected");
	},
	
	cancelBlur: function(pInputNumber, e){
		var xDBSMask = pInputNumber.data("dbsmask");
		xDBSMask.setEnd();
		pInputNumber.data("action", true);
		e.preventDefault();
		e.stopImmediatePropagation();
	},

	reset: function(pInputNumber){
		var xDBSMask = pInputNumber.data("dbsmask");
		var xInputData = pInputNumber.data("inputdata");
		xInputData[0].value = 0;
		xDBSMask.formatNumber();
	},
	
	invert: function(pInputNumber){
		if (pInputNumber.data("direction").hasClass("-up")){
			pInputNumber.data("direction").removeClass("-up")
										  .addClass("-down");
		}else{
			pInputNumber.data("direction").removeClass("-down")
			  							  .addClass("-up");
		}
	}

}
