dbsinvest = {
	pmt : function(pI, pN, pPV, pFV, pType) {
		pI /= 100;
		pFV || (pFV = 0);
		pType || (pType = 0);

		var xPMT, xPow;

	    if (pI === 0){
	    	return -(pPV + pFV) / pN;
	    }

	    xPow = Math.pow(1 + pI, pN);
	    xPMT = - pI * pPV * (xPow + pFV) / (xPow - 1);

	    if (pType === 1){
	    	xPMT /= (1 + pI);
	    }

	    return parseFloat(xPMT.toFixed(2));
	},
	
	fv : function(pI, pN, pPMT, pPV, pType) {
		pI /= 100; 
		pPV || (pPV = 0);
		pType || (pType = 0);

		var xPow = Math.pow(1 + pI, pN);
		var xFV;


		if (pI) {
			xFV = (pPMT * (1 + pI * pType) * ( 1 - xPow ) / pI) - pPV * xPow;
		} else {
			xFV = -1 * (pPV + pPMT * pN);
		}
		return parseFloat(xFV.toFixed(2));
	},
	
	
}