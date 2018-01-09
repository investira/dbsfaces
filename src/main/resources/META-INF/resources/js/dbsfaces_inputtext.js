dbs_inputText = function(pId) {
	var wSearching = false;
	var wBlur = false;

	var xInputTextData = dbsfaces.inputText.initialize($(pId));
	
	//Verificar caixa da digitação
//	$(pId + "-data.-upper").keydown(function(e){
//		dbsfaces.inputText.letterCase(pId, $(this), e,"upper");
//	});	
//
//	$(pId + "-data.-lower").keydown(function(e){
//		dbsfaces.inputText.letterCase(pId, $(this), e,"lower");
//	});	
//
//	$(pId + "-data.-proper").keydown(function(e){
//		dbsfaces.inputText.letterCase(pId, $(this), e,"proper");
//	});	
//	
//	$(pId + "-data.-upperfirst").keydown(function(e){
//		dbsfaces.inputText.letterCase(pId, $(this), e,"upperfirst");
//	});	
	
	xInputTextData.dom.inputData.focusin(function(e){
		wSearching = false;
		dbsfaces.ui.selectAll(this);
	});

	xInputTextData.dom.inputData.focus(function(e){
		xInputTextData.dom.suggestion.addClass("-th_input-data-FOCUS");
	});
	
	
	xInputTextData.dom.inputData.blur(function(e){
		xInputTextData.dom.suggestion.removeClass("-th_input-data-FOCUS");
//		dbsfaces.ui.selectNone(this); //Safari estava impedindo o blur
		xInputTextData.time = +new Date();

		//Esconde a lista
		dbsfaces.inputText.hideList(xInputTextData);

		//Omite blur caso pesquisa esteja em execução
		//Irá disparar após recebimento da resposta
		if (wSearching){
			wBlur = true;
			e.stopPropagation();
			e.stopImmediatePropagation();
		}else{
			dbsfaces.inputText.validateNullText(xInputTextData);
		}
	});

	
	/* copia a sugestão para o input ou navega pela lista de sugestões*/
	xInputTextData.dom.inputData.keydown(function(e){
		//Confirma sugestão com tab e seta para a direita
		if (dbsfaces.inputText.isSuggestion(xInputTextData)){
			if ((e.which == 13 || //ENTER
			 e.which == 39) && //RIGHT
			 $(this).get(0).selectionEnd == $(this).val().length) { //Se o cursor estiver no final do texto digitado pelo usuário
				e.stopImmediatePropagation();
				dbsfaces.inputText.acceptSuggestion(xInputTextData);
				if (e.which == 13){
					dbsfaces.inputText.hideList(xInputTextData);
					return false;
				}
			//Navega na lista de sugestões	
			}else if(e.which==40   //DOWN
				  || e.which==38){ //UP
				//Se existe suggestion, controla a exibição e a navegação
				if (dbsfaces.inputText.isSuggestion(xInputTextData)){
					e.preventDefault();
					e.stopPropagation();
					//Exibe suggestions no primeiro click
					if (xInputTextData.dom.list.css("display") == "none"){
						dbsfaces.inputText.showList(xInputTextData);
					}else{
						//Navega na lista de sugestões
						dbsfaces.dataTable.moveToNextOrPreviousRow(xInputTextData.dom.dataTable.data("data"), e.which);
					}
				}
			//Se não for tab, shift ou setas para a direira ou esquerda
			}else{
				if (dbsfaces.inputText.isValidKey(e)){
					//Apaga sugestão, pois será efetuada uma nova pesquisa no keyup
					dbsfaces.inputText.clearSuggestion(xInputTextData);
				}
				//Limpa campo da posição do cursos até o fim
				if (e.which == 8){ //BACKSPACES
					dbsfaces.ui.selectEnd($(this));
				}
			}
		}
	});
	
	//Faz a pesquisa ===================================================
	xInputTextData.dom.inputData.on("paste", function(e){
		dbsfaces.inputText.requestSuggestion(xInputTextData);
		
	});

	//Click o icone de pesquisar
	xInputTextData.dom.inputFind.on("click", function(e){
		if (xInputTextData.dom.list.css("display") == "none"){
			dbsfaces.inputText.showList(xInputTextData);
			xInputTextData.dom.inputData.focus();
		}else{
			dbsfaces.inputText.hideList(xInputTextData);
		}
		return false;
	});
	
	//Inicia pesquisa de dados
	xInputTextData.dom.submit.on(dbsfaces.EVENT.ON_AJAX_BEGIN, function(e){
		wSearching = true;

		xInputTextData.dom.input.append("<div class='-loading'></div>");
		xInputTextData.dom.inputFind.hide();
		e.stopImmediatePropagation();
	});
	
	//Recebe a resposta da requisição da sugestão
	xInputTextData.dom.submit.on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		wSearching = false;
		
		//Dispara o blur caso tenha sido ignorado originalmente
		if (wBlur){
			dbsfaces.inputText.pvTriggerBlur(xInputTextData);
		}
		xInputTextData.dom.input.children(".-loading").remove();
		xInputTextData.dom.inputFind.show();
		dbsfaces.inputText.fixLayout(xInputTextData);
		dbsfaces.inputText.showFirstSuggestion(xInputTextData);
		
		e.stopImmediatePropagation();
	});

	xInputTextData.dom.input.keyup(function(e){
		if (dbsfaces.inputText.isValidKey(e)){
			dbsfaces.inputText.requestSuggestion(xInputTextData);
		}
	});

	//Item selecionado da lista
	xInputTextData.dom.input.on(dbsfaces.EVENT.ON_ROW_SELECTED, ".-list", function(e, pRow){
		dbsfaces.inputText.suggestionReceived(xInputTextData, pRow, true);
	});
	
	//Seta foco na lista
	xInputTextData.dom.input.on("click", ".-list", function(e){
		xInputTextData.dom.inputData.focus();
		
		xInputTextData.dom.self.css("opacity","0");					
		setTimeout(function() {
			xInputTextData.dom.list.hide();
	  	}, 300);
	});

	
	//Validação inicial
	dbsfaces.inputText.validateNullText(xInputTextData);

	dbsfaces.inputText.fixLayout(xInputTextData);
		
}


dbsfaces.inputText = {
	
	initialize: function(pInputText){
		var xInputTextData = dbsfaces.inputText.initializeData(pInputText);
		return xInputTextData; 
	},

	initializeData: function(pInputText){
		var xId = dbsfaces.util.jsid(pInputText[0].id);
		var xData = {
			dom : {
				self: pInputText, //O próprio slider
				input: $(xId + " > .-container > .-input"),
				inputData: $(xId + "-data"),
				inputFind: null,
				suggestion: $(xId + "-suggestion"),
				suggestionKey: $(xId + "-suggestion-key"),
				list: $(xId + "-list"),
				dataTable: $(xId + "-dataTable"),
				submit: $(xId + "-submit")
			},
			type : pInputText.attr("type"),
			nullText: null,
			time : +new Date(),
			timeout : null
		}
		xData.dom.inputFind = xData.dom.input.children(".-i_find");
		xData.nullText = xData.dom.suggestionKey.attr("nulltext");
		pInputText.data("data", xData);
		return xData;
	},
		
	requestSuggestion: function(pInputTextData){
		dbsfaces.inputText.clearSuggestion(pInputTextData);
		//Não faze pesquisa se valor for vázio
		var xValue = pInputTextData.dom.inputData.val().trim();
		if (dbsfaces.inputText.pvIsNullText(pInputTextData, xValue)){return;}

		/* delay para evitar chamadas ajax contantes */
		if (pInputTextData.time == 0){
			pInputTextData.time = +new Date();
		}else{
			xDelay = +new Date() - pInputTextData.time;
			pInputTextData.time = +new Date();
			if (xDelay < 550){
				window.clearTimeout(pInputTextData.timeout); //Cancela request anterior
			}
			//Aguarda um tempo para chamar a rotina de sugestão
			pInputTextData.timeout = setTimeout(function(){
				pInputTextData.dom.submit.click();
				//Substituir o botão por uma chamada ajax. Estudar pq a chama ajax abaixo não funciona
//				jsf.ajax.request($(pId + "-submit"), "click", {execute: $(pId).get(0).id, render: $(pId).get(0).id + "-list", onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror}); 
//				return false;
			}, 550); //Time de delay para efetuar a chamada
		}
	},

	clearSuggestion: function(pInputTextData){
		pInputTextData.dom.suggestion.val("");
		pInputTextData.dom.suggestionKey.attr("key", "");
		//Verifica se houve mudança antes de disparar evento
		if (pInputTextData.dom.suggestionKey.val() != ""){
			pInputTextData.dom.suggestionKey.val("");
			dbsfaces.inputText.pvTriggerChange(pInputTextData);
		};
		dbsfaces.inputText.pvValidate(pInputTextData);
	},

	acceptSuggestion: function(pInputTextData){
		var xSuggestionValue = pInputTextData.dom.suggestion.val().trim();
		//Verifica se houve mudança antes de disparar evento
		if (pInputTextData.dom.suggestionKey.val() != pInputTextData.dom.suggestionKey.attr("key")){
			//Seta valor selecionado
			pInputTextData.dom.suggestionKey.val(pInputTextData.dom.suggestionKey.attr("key"));
			pInputTextData.dom.inputData.val(xSuggestionValue);
			dbsfaces.inputText.pvTriggerChange(pInputTextData);
		};
		dbsfaces.inputText.pvValidate(pInputTextData);
	},
	
	//Copia valor da lista para a sugestão
	suggestionReceived: function(pInputTextData, pRow, pFromRowSelected){
		//Se encontrou alguma sugestão
		var xRow = $(pRow);
		if (xRow.length > 0){
			var xRowKey = xRow.find('td .-key');
			var xRowValue = xRow.children('td.-dv');
			var xSuggestionValue = $(xRowValue).text().trim();
			var xSuggestionKey = $(xRowKey).text().trim();
			var xValue = pInputTextData.dom.inputData.val().trim();
			//Se não foi seleção da linha a partir da lista de sugestões, verifica se inicio do texto recebido é iqual a texto digitado.
			//Se não for, ignora sugestão recebida
			if (!pFromRowSelected){
				var xSuggestionValueTrunc = xSuggestionValue.substr(0, xValue.length);
				//Apaga sugestão se inicio do texto recebido não for iqual ao digitado.
				if (xSuggestionValueTrunc.toUpperCase() != xValue.toUpperCase()){
					dbsfaces.inputText.clearSuggestion(pInputTextData);
					return;
				}else{
					pInputTextData.dom.inputData.val(xSuggestionValueTrunc);
				} 
			}
			pInputTextData.dom.suggestionKey.attr("key", xSuggestionKey);
			pInputTextData.dom.suggestion.val(xSuggestionValue);
			//Se texto digitado já for igual a sugestão ou
			//foi seleção a partir da lista de sugestões, aceita a sugestão automaticamente
			if (pFromRowSelected 
			|| xValue == xSuggestionValue){
				dbsfaces.inputText.acceptSuggestion(pInputTextData);
			}
		//Se não encontrou alguma sugestão
		}else{
			dbsfaces.inputText.clearSuggestion(pInputTextData);
		}
		
	},

	showFirstSuggestion: function(pInputTextData){
		//Le primeiro item da lista
//		var xRow = $(pId + "-dataTable tbody").find("tr:first");
		var xRow = pInputTextData.dom.dataTable.find("tbody").find("tr:first");

		dbsfaces.inputText.suggestionReceived(pInputTextData, xRow, false);
	},

	pvValidate: function(pInputTextData){
		//Sai caso input não seja do tipo que controla suggestion
		if (dbsfaces.inputText.isSuggestion(pInputTextData)){
			//Verifica conteúdo da seleção
			var xSuggestionValue = pInputTextData.dom.suggestion.val().trim();
			var xValue = pInputTextData.dom.inputData.val();
			if (dbsfaces.inputText.pvIsNullText(pInputTextData, xValue)){
				dbsfaces.inputText.pvRemoveError(pInputTextData);
			}else if (xSuggestionValue == xValue){
				//Confirma a sugestão
				dbsfaces.inputText.pvRemoveError(pInputTextData);
			}else{
				//Campo com erro
				dbsfaces.inputText.pvAddError(pInputTextData);
			}
		}
	},		
	
	pvIsNullText: function(pInputTextData, pText){
		return (pText == "" || pText == pInputTextData.nullText);
	},
	
	validateNullText: function(pInputTextData){
		if (dbsfaces.inputText.isSuggestion(pInputTextData)){
			var xValue = pInputTextData.dom.inputData.val();
			if (xValue == ""){
				pInputTextData.dom.inputData.val(pInputTextData.nullText);
			}
		}
	},

	pvTriggerChange: function(pInputTextData){
		pInputTextData.dom.inputData.trigger("change");
	},		
	
	pvTriggerBlur: function(pInputTextData){
		wBlur = false;
		pInputTextData.dom.inputData.trigger("blur");
		dbsfaces.inputText.validateNullText(pInputTextData);
	},
	
	
	//Não fará pesquisa se for uma das teclas abaixo
	isValidKey: function(e){
		if (e.which == 9 || //TAB
			e.which == 12 || //CLEAR
			e.which == 13 || //ENTER
			e.which == 16 || //SHIFT
			e.which == 17 || //CONTROL
			e.which == 18 || //OPTION
			e.which == 27 || //ESC
			e.which == 34 || //PGDOWN
			e.which == 35 || //END
			e.which == 36 || //HOME
			e.which == 36 || //HOME
			e.which == 37 || //LEFT
			e.which == 38 || //UP
			e.which == 39 || //RIGHT
			e.which == 40 || //DOWN
			e.which == 91 || //COMMAND
			e.which == 33 || //PGUP
			(e.which >= 112 && e.which <= 130) || //fn
			e.ctrlKey ||
			e.altKey){
			return false;
		}else{
			return true;
		} 	
	},
	pvRemoveError: function(pInputTextData){
		pInputTextData.dom.suggestion.removeClass("-error");
		pInputTextData.dom.inputData.removeClass("-error");
	},

	pvAddError: function(pInputTextData){
		pInputTextData.dom.suggestion.addClass("-error");
		pInputTextData.dom.inputData.addClass("-error");
	},
	
	fixLayout: function(pInputTextData){
		var xId = dbsfaces.util.jsid(pInputTextData.dom.self[0].id);
		pInputTextData.dom.list = $(xId + "-list");
		pInputTextData.dom.dataTable = $(xId + "-dataTable");
		if (pInputTextData.dom.dataTable.length == 0){
			return;
		}
		pInputTextData.dom.dataTable.find("tbody:first").attr('tabindex', '-1');
		pInputTextData.dom.dataTable.find("input:first.-foo").attr('tabindex', '-1');
		
		var xWidth = pInputTextData.dom.inputData.outerWidth();
		pInputTextData.dom.dataTable.find("thead:first").css('min-width', xWidth);
		pInputTextData.dom.dataTable.find("tbody:first").css('min-width', xWidth);
	},
	
	//Indica se inputtext é do tipo suggestion
	isSuggestion: function(pInputTextData){
		return (pInputTextData.dom.list.length > 0);
	},
	
	showList: function(pInputTextData){
		if (dbsfaces.inputText.isSuggestion(pInputTextData)){
			pInputTextData.dom.list.css("opacity","1").show();
		}
	},

	//Esconde a lista
	hideList: function(pInputTextData){
		if (dbsfaces.inputText.isSuggestion(pInputTextData)){
			pInputTextData.dom.list.css("opacity","0");					
			setTimeout(function() {
				pInputTextData.dom.list.hide();
		  	}, 100);
		}
	},
	
	letterCase: function(pInputTextData, pInput, e, pLetterCase){
		if (e.metaKey){
			return; //Sai para não converte o caracter recebido quando foi tecla especial como um Ctrl-C
		}
		if(e.which >= 65 && e.which <=90){
			if (pLetterCase!="proper" &&
				pLetterCase!="upperfirst"){
				return;
			}
//			if (pLetterCase!="upper" &&
//				pLetterCase!="lower" &&
//				pLetterCase!="proper" &&
//				pLetterCase!="upperfirst"){
//				return;
//			}
			//Só testa tamanho máximo se imput não for com suggestion
			if (!dbsfaces.inputText.isSuggestion(pInputTextData)){ 
				if (typeof pInput.attr("maxlength") != "undefined"){
					if (pInput.val().length >= parseInt(pInput.attr("maxlength"))){
						return;
					}
				}
			}
			
			var xValue = pInput.val();
			
//			var xSelectionStart = e.originalEvent.currentTarget.selectionStart;
//			var xSelectionEnd = e.originalEvent.currentTarget.selectionEnd;
			var xSelectionStart = xValue.length;
			var xSelectionEnd = xValue.length;
			if (pInput[0].__proto__.hasOwnProperty("selectionStart")){
				xSelectionStart = pInput[0].selectionStart;
				xSelectionEnd = pInput[0].selectionEnd;
			}
			var xS = xSelectionEnd - xSelectionStart;
			var xSS = xSelectionStart + 1;
			var xMinusculo = 0; //Default é upper
			if (pLetterCase=="upper"){
				xMinusculo = 0; 
			}else if (pLetterCase=="lower"){
				xMinusculo = 32; //Incremento para os códigos de caracteres minúsculos
			}else if (pLetterCase=="proper"){
				if (xSelectionStart!=0){
					var xCharBefore = xValue.substr(xSelectionStart-1, 1);
					if (xCharBefore != " " && //Se não for inicio da palavra
						xCharBefore != ""){
						xMinusculo = 32; //Converte para minusculo
					}
				}
			}else if (pLetterCase=="upperfirst"){
				if (xSelectionStart!=0 
				 && xSelectionEnd!=0){
					return; //Sai para não converte o caracter recebido
				}
			}
			e.preventDefault();
			//Agrupo o inicio + valor digitado + final da texto. Isto previne os casos que a substituição no meio do texto
			xValue = xValue.substring(0, xSelectionStart) + String.fromCharCode(e.which + xMinusculo) + xValue.substring(xSelectionEnd);
			pInput.val(xValue);
//			pInput.get(0).setSelectionRange(xSS, xSS); 
			dbsfaces.ui.selectRange(pInput, xSS, xSS);
		}	
	}
}

