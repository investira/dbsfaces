dbs_inputNumber = function(pId, pInputDataId, pDecimalPlaces, pSeparateThousand, pLeadingZero, pMinValue, pMaxValue, pLocale) {
	dbsfaces.setLocale(pLocale);
	var xInputData = $(pInputDataId);
	
	dbsfaces.inputNumber.initialize($(pId), xInputData, pDecimalPlaces, pSeparateThousand, pLeadingZero, pMinValue, pMaxValue);
}

dbsfaces.inputNumber = {

	initialize: function(pInputNumber, pInputData, pDecimalPlaces, pSeparateThousand, pLeadingZero, pMinValue, pMaxValue){
		var xData = {
			dom : {
				self : pInputNumber,
				inputData : pInputData
			},
			dbsmask : null
		}

		if (!dbsfaces.util.isMobile()){
			//Retirado para não dar erro no firefox desktop 
			pInputData.attr("pattern", null);
		}
		xData.dbsmask = pInputData.dbsmask({
			parentDom :  pInputNumber,
			type : "number",
			maskEmptyChr : (pLeadingZero ? "0":""),
			decimalPlaces : dbsfaces.number.parseFloat(pDecimalPlaces), 
			separateThousand : pSeparateThousand,
			maxLength: dbsfaces.number.parseFloat(pInputData.attr("maxlength")),
			minValue: dbsfaces.number.parseFloat(pMinValue),
			maxValue: dbsfaces.number.parseFloat(pMaxValue)
		});

		pInputNumber.data("data", xData);
	},

	setValue: function(pInputNumber, pValue){
		var xInputNumberData = pInputNumber.data("data");
		xInputNumberData.dbsmask.pvSetValue(pValue);
	}
}

