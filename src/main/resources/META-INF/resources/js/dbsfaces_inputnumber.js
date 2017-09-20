dbs_inputNumber = function(pId, pInputData, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits) {

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
		groupSymbol: pGroupSymbol, 
        groupDigits: pGroupDigits,   
        decSymbol: pDecSymbol, 
        stripMask: false  
	});

	dbsfaces.inputNumber.initialize($(pId), $(pInputData), xDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits);
}

dbsfaces.inputNumber = {

	initialize: function(pInputNumber, pInputData, pDBSMask, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits){
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

