dbs_inputMask = function(pId, pMask, pMaskEmptyChr, stripMask) {
	$(pId).dbsmask({
		type: 'fixed',  
		mask: pMask,
		maskEmptyChr: pMaskEmptyChr, 
        stripMask: stripMask  
	});
}



/* Eventos */



