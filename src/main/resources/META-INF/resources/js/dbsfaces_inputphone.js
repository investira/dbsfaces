dbs_inputPhone = function(pId) {
	$(pId + " input").focusin(function(e){
		dbsfaces.ui.selectAll(this);
	});
	
	$(pId + " > .-container > .-th_input-data").focusin(function(){
		$(this).addClass("-th_input-data-FOCUS");
	});

	$(pId + " > .-container > .-th_input-data").focusin(function(){
		$(this).addClass("-th_input-data-FOCUS");
	});
//	
	
	$(pId + " > .-container > .-th_input-data").focusout(function(){
		$(this).removeClass("-th_input-data-FOCUS");
	});
	
	$(pId + " > .-container > .-th_input-data > .-number").focusin(function(e){
		$(this).val(dbsfaces.number.getOnlyNumber($(this).val()));
	});
	
	$(pId + " > .-container > .-th_input-data > input").keydown(function(e){
		var xC = "";
		//Ignora tecla se não for uma tecla válida para o campo de data
		if (!dbsfaces.inputPhone.isValidKey(e)){
			e.preventDefault();
			return false;
		}
	
		//Pula para o próximo campo de input
		if (e.which == 9 && 
			!e.shiftKey){
			//Desabilitador até verificar se é bom ter está opção
			//$(this).parent().children("input").last().focusNextInputField();
			return;
		//puda para o próximo campo dento da data com a BARRA
		}else if (e.which == 109){ // Separador
			xC = $(this).nextAll("input:first");
			e.preventDefault();
			return;
		}
		//Se foi configurado um novo campo para receber o foco, pula para ele
		if (xC.length){
			e.preventDefault();
			xC.focus();
			return;
		}		
		
	});
	$(pId + " > .-container > .-th_input-data > .-ddd").keyup(function(e){
		dbsfaces.inputPhone.nextField($(this), 2, e);
	});

	$(pId + " > .-container > .-th_input-data > .-ddi").keyup(function(e){
		dbsfaces.inputPhone.nextField($(this), 3, e);
	});
	
}

dbsfaces.inputPhone = {
	isValidKey: function(e){
		if (e.altKey ||
			e.ctrlKey){
			return false;
		} 	
		if (e.which == 9 ||
			e.which == 8 ||
			e.which == 13 ||
			e.which == 37 ||
			e.which == 38 ||
			e.which == 39 ||
			e.which == 40 ||
			e.which == 46 ||
			e.which == 47 ||
			e.which == 109 ||
			(e.which >= 96 && e.which <= 105) ||
			(!e.shiftKey && dbsfaces.number.isNumber(dbsfaces.string.fromCharCode(e.which))) ){
			return true;
		}
		return false;
	},

	
	leadingZero: function(pValue){
		if (pValue != ""){
			if (dbsfaces.number.isNumber(pValue)){
				pValue = parseInt(pValue,10);
				if (pValue < 10){
					pValue = "0" + pValue; 
				}
			}else{
				pValue = "";
			}
		}
		return pValue;
	},

	nextField: function(pInput, pSize, e){
		var xC = "";
		var xS = pInput.get(0).selectionEnd - pInput.get(0).selectionStart; 
		//Pula para o próximo campo(mes ou ano) da data caso e campo atual esteja completo
		if (xS == 0 &&
			dbsfaces.number.isNumber(dbsfaces.string.fromCharCode(e.which))){
			var xL = pInput.val().length;
			//Se estiver completo com os dois digitos, pula
			if (xL == pSize){
				var xC = pInput.nextAll("input:first");
			}
		}
		//Volta o campo quando digitado a seta para esquerda
		if (e.which == 37 &&
			!e.shiftKey){
			xC = pInput.prevAll("input:first");
		//Avança para o próximo campo quando digitado a seta para direita
		}else if ((e.which == 39  &&
				  !e.shiftKey) ||
				  e.which == 47){
			xC = pInput.nextAll("input:first");
		}
		//Se foi configurado um novo campo para receber o foco, pula para ele
		if (xC.length){
			e.preventDefault();
			xC.focus();
		}
	}
}


