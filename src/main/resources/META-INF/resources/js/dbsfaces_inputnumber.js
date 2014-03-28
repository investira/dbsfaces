dbs_inputNumber = function(pId, pType, pMask, pMaskEmptyChr, pDecDigits, pGroupSymbol, pDecSymbol, pGroupDigits) {
	$(pId).dbsmask({
		type: pType,  
		mask: pMask,
		maskEmptyChr: pMaskEmptyChr,
        decDigits: pDecDigits,
		groupSymbol: pGroupSymbol, 
        groupDigits: pGroupDigits,   
        decSymbol: pDecSymbol, 
        stripMask: false  
	});
}

/* Eventos */



