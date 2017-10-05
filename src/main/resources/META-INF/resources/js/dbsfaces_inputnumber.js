dbs_inputNumber = function(pId, pInputData, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupDigits, pLocale) {
	dbsfaces.setLocale(pLocale);
	
	if (!dbsfaces.util.isMobile()){
		//Retirado para não dar erro no firefox desktop 
		$(pInputData).attr("pattern", null);
	}
	
	var xDBSMask = $(pInputData).dbsmask({
		parentDom: $(pId)[0],
		type: pType,  
		mask: pMask,
		maskEmptyChr: pMaskEmptyChr,
        decDigits: pDecDigits,
		groupSymbol: dbsfaces.groupSeparator, 
        groupDigits: pGroupDigits,   
        decSymbol: dbsfaces.decimalSeparator, 
        stripMask: false  
	});

	dbsfaces.inputNumber.initialize($(pId), $(pInputData), xDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupDigits);
}

dbsfaces.inputNumber = {

	initialize: function(pInputNumber, pInputData, pDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupDigits){
		var xData = {
			dom : {
				self : pInputNumber,
				inputData : pInputData
			},
			dbsmask : pDBSMask
		}
		pInputNumber.data("data", xData);
	},


	setValue: function(pInputNumber, pValue){
		var xInputNumberData = pInputNumber.data("data");
		xInputNumberData.dbsmask.setValue(pValue);
		xInputNumberData.dbsmask.formatNumber();
	}
}

