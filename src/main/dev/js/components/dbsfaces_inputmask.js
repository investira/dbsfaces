dbs_inputMask = function(pId, pInputDataId, pMask, pMaskEmptyChr, pStripMask){
	var xInputData = $(pInputDataId);
	
	dbsfaces.inputMask.initialize($(pId), xInputData, pMask, pMaskEmptyChr, pStripMask);
}

dbsfaces.inputMask = {

	initialize: function(pInputMask, pInputData, pMask, pMaskEmptyChr, pStripMask){
		var xData = {
			dom : {
				self : pInputMask,
				inputData : pInputData
			},
			dbsmask : null
		}

		if (!dbsfaces.util.isMobile()){
			//Retirado para não dar erro no firefox desktop 
			pInputData.attr("pattern", null);
		}
		var xRegexString = "0-9";
		xData.dbsmask = pInputData.dbsmask({
			parentDom : pInputMask,
			type : "fixed",
			mask : pMask,
			maskEmptyChr : pMaskEmptyChr,
			decimalPlaces : 0,
			separateThousand : false,
			maxLength: pMask.length
		});

		pInputMask.data("data", xData);
	},

	setValue: function(pInputMask, pValue){
		var xInputMaskData = pInputMask.data("data");
		xInputMaskData.dbsmask.pvSetValue(pValue);
	}
}


