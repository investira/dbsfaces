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
	
	
	calculaFluxo: function(pI, pN, pPMT, pPV, pType){
		var xFV;
		var xList = [];
		//Total economizado
		for (var x=0; x <= pN; x++){
			xFV = dbsinvest.fv(pI, x, pPMT, pPV, pType);
			xList.push({value:xFV, label:x});
		}
		return xList;
	},
	
//	calculaFluxoCreDeb: function(pICre, pNCre, pPMTCre, pPV, pIDeb, pNDeb, pPMTDeb, pType){
//		
//	},
//	
//	pvAtingirMeta = function(pTargetValue, pMaxValue, pMinValue, pDecimalPlaces){
//		var xTargetSliderData = xData.targetSlider.data("data");
//		var xNData = xData.dom.slider_n.data("data");
//		var xSaqueNData = xData.dom.slider_saque_n.data("data");
//		var xCount = 0;
//		var xMax = xTargetSliderData.max;
//		var xMin = xTargetSliderData.min;
//		var xList = null;
//		var xVP;
//		var xFV;
//		var xDif;
//		var xErro = false;
//		while (true){
//			xCount++;
//			if (xData.targetLower){
//				xTargetSliderData.value = dbsfaces.math.round(xMin + ((xMax - xMin) / 2), pDecimalPlaces);
//			}else{
//				xTargetSliderData.value = dbsfaces.math.trunc(xMin + ((xMax - xMin) / 2), pDecimalPlaces);
//			}
//			xList = dbsinvest.calculaFluxo();
//			if (xList.length > xNData.value){
//				//FV no final geral
//				xFV = xList[xList.length - 1].value;
//				if (xSaqueNData.value == 80){
//					//FV no final do periodo de economia
//					if (xNData.length > 0){
//						xVP = xList[xNData.value].value;
//					}else{
//						xVP = xFV;
//					}
//					if (xData.targetLower){
//						xDif = xFV - xVP;
//					}else{
//						xDif = xVP - xFV;
//					}
//				}else{
//					if (xData.targetLower){
//						xDif = xFV;
//					}else{
//						xDif = -xFV;
//					}
//				}
//				if (xDif == 0){
//					break;
//				}else if(xDif > 0){
//					if (xMax == xTargetSliderData.value){
//						break;
//					}else{
//						xMax = xTargetSliderData.value;
//					}
//				}else{
//					if (xMin == xTargetSliderData.value){
//						break;
//					}else{
//						xMin = xTargetSliderData.value;
//					}
//				}
//			}else{
//				xMax = xTargetSliderData.value;
//			}
//			if (xCount > 200){
//				xErro = true;
//				break;
//			}
//		}
//		dbsfaces.slider.setValue(xTargetSliderData.dom.self, xTargetSliderData.value);
//		xData.dom.simulacao.removeClass("-disabled");
//		if (xErro){
//			dbsfaces.dialog.setMsg(xData.dom.msg, "t", "Não é possível encontrar o ponto de equilíbrio com os valores informados. Aumente o valor poupado e/ou a taxa de rentabilidade, ou diminua o valor do saque.");
//			dbsfaces.dialog.show(xData.dom.msg);
//		}
//		xData.running = false;
//		return xList;
//	}


	
}