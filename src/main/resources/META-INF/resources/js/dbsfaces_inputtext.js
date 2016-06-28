dbs_inputText = function(pId) {
	var wSearching = false;
	var wBlur = false;

	
	$(pId + " input").focusin(function(e){
		wSearching = false;
		dbsfaces.ui.selectAll(this);
	});
	//Verificar caixa da digitação
	$(pId + "-data.-upper").keydown(function(e){
		dbsfaces.inputText.letterCase(pId, $(this), e,"upper");
	});	

	$(pId + "-data.-lower").keydown(function(e){
		dbsfaces.inputText.letterCase(pId, $(this), e,"lower");
	});	

	$(pId + "-data.-proper").keydown(function(e){
		dbsfaces.inputText.letterCase(pId, $(this), e,"proper");
	});	
	
	$(pId + "-data.-upperfirst").keydown(function(e){
		dbsfaces.inputText.letterCase(pId, $(this), e,"upperfirst");
	});	
	
	$(pId + " > .-container > .-input > .-th_input-data").focus(function(e){
		$(pId + "-suggestion").addClass("-th_input-data-FOCUS");
	});
	
	
	$(pId + " > .-container > .-input > .-th_input-data").blur(function(e){
		$(pId + "-suggestion").removeClass("-th_input-data-FOCUS");
		dbsfaces.ui.selectNone(this);
		wTime = +new Date();

		//Esconde a lista
		dbsfaces.inputText.hideList(pId);

		//Omite blur caso pesquisa esteja em execução
		//Irá disparar após recebimento da resposta
		if (wSearching){
			wBlur = true;
			e.stopPropagation();
			e.stopImmediatePropagation();
		}else{
			dbsfaces.inputText.validateNullText(pId);
		}
	});

	/* copia a sugestão para o input ou navega pela lista de sugestões*/
	$(pId + " > .-container > .-input > .-th_input-data").keydown(function(e){
		//Confirma sugestão com tab e seta para a direita
		if ((e.which == 13 || //ENTER
			 e.which == 39) && //RIGHT
			$(this).get(0).selectionEnd == $(this).val().length) { //Se o cursor estiver no final do texto digitado pelo usuário
			e.stopImmediatePropagation();
			dbsfaces.inputText.acceptSuggestion(pId);
			if (e.which == 13){
				dbsfaces.inputText.hideList(pId);
				return false;
			}
		//Navega na lista de sugestões	
		}else if(e.which==40   //DOWN
			  || e.which==38){ //UP
			//Se existe suggestion, controla a exibição e a navegação
			if (dbsfaces.inputText.isSuggestion(pId)){
				e.preventDefault();
				e.stopPropagation();
				//Exibe suggestions no primeiro click
				if ($(pId + "-list").css("display") == "none"){
					dbsfaces.inputText.showList(pId);
				}else{
					//Navega na lista de sugestões
					dbsfaces.dataTable.moveToNextOrPreviousRow(pId + "-dataTable", e.which);
				}
			}
		//Se não for tab, shift ou setas para a direira ou esquerda
		}else{
			if (dbsfaces.inputText.isValidKey(e)){
				//Apaga sugestão, pois será efetuada uma nova pesquisa no keyup
				dbsfaces.inputText.clearSuggestion(pId);
			}
			//Limpa campo da posição do cursos até o fim
			if (e.which == 8){ //BACKSPACES
				dbsfaces.ui.selectEnd($(this));
			}
		}
	});
	
	//Faz a pesquisa ===================================================
	$(pId + " > .-container > .-input").keyup(function(e){
		if (dbsfaces.inputText.isValidKey(e)){
			dbsfaces.inputText.requestSuggestion(pId);
		}
	});

	$(pId + " > .-container > .-input > .-th_input-data").on("paste", function(e){
		dbsfaces.inputText.requestSuggestion(pId);
		
	});

	//Click o icone de pesquisar
	$(pId + " > .-container > .-input > .-i_find").on("click", function(e){
		if ($(pId + "-list").css("display") == "none"){
			dbsfaces.inputText.showList(pId);
			$(pId + "-data").focus();
		}else{
			dbsfaces.inputText.hideList(pId);
		}
		return false;
	});
	
	//Inicia pesquisa de dados
	$(pId + "-submit").on(dbsfaces.EVENT.ON_AJAX_BEGIN, function(e){
		wSearching = true;

		$(pId + " > .-container > .-input").append("<div class='-loading'></div>");
		$(pId + " > .-container > .-input > .-i_find").hide();
		e.stopImmediatePropagation();
	});
	
	//Recebe a resposta da requisição da sugestão
	$(pId + "-submit").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		wSearching = false;
		
		//Dispara o blur caso tenha sido ignorado originalmente
		if (wBlur){
			dbsfaces.inputText.triggerBlur(pId);
		}

		$(pId + " > .-container > .-input > .-loading").remove();
		$(pId + " > .-container > .-input > .-i_find").show();
		dbsfaces.inputText.fixLayout(pId);
		dbsfaces.inputText.showFirstSuggestion(pId);
		
		e.stopImmediatePropagation();
	});
	
	//Item selecionado da lista
	$(pId + " > .-container > .-input").on(dbsfaces.EVENT.ON_ROW_SELECTED, ".-list", function(e, pRow){
		dbsfaces.inputText.suggestionReceived(pId, pRow, true);
	});
	
	//Seta foco na lista
	$(pId + " > .-container > .-input").on("click", ".-list", function(e){
		$(pId + "-data").focus();
		
		$(this).css("opacity","0");					
		setTimeout(function() {
			$(pId + "-list").hide();
	  	}, 300);
	});

	
	//Validação inicial
	dbsfaces.inputText.validateNullText(pId);

	dbsfaces.inputText.fixLayout(pId);
		
}


dbsfaces.inputText = {
	wTime: +new Date(),
	wTimeout: 0,

	requestSuggestion: function(pId){
		dbsfaces.inputText.clearSuggestion(pId);
		//Não faze pesquisa se valor for vázio
		var xValue = $.trim($(pId + "-data").val());
		var xNullText = $(pId + "-suggestion-key").attr("nulltext");
		if (xValue==""
		 || xValue==xNullText){
			return;
		}
		/* delay para evitar chamadas ajax contantes */
		if (dbsfaces.inputText.wTime == 0){
			dbsfaces.inputText.wTime = +new Date();
		}else{
			xDelay = +new Date() - dbsfaces.inputText.wTime;
			dbsfaces.inputText.wTime = +new Date();
			if (xDelay < 550){
				window.clearTimeout(dbsfaces.inputText.wTimeOut); //Cancela request anterior
			}
			//Aguarda um tempo para chamar a rotina de sugestão
			dbsfaces.inputText.wTimeOut = window.setTimeout(function(){
				$(pId + "-submit").click();
				//Substituir o botão por uma chamada ajax. Estudar pq a chama ajax abaixo não funciona
//				jsf.ajax.request($(pId + "-submit"), "click", {execute: $(pId).get(0).id, render: $(pId).get(0).id + "-list", onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror}); 
//				return false;
			}, 550); //Time de delay para efetuar a chamada
		}
	},

	clearSuggestion: function(pId){
		$(pId + "-suggestion").val("");
		$(pId + "-suggestion-key").attr("key", "");
		//Verifica se houve mudança antes de disparar evento
		if ($(pId + "-suggestion-key").val() != ""){
			$(pId + "-suggestion-key").val("");
			dbsfaces.inputText.triggerChange(pId);
		};
		dbsfaces.inputText.validate(pId);
	},

	acceptSuggestion: function(pId){
		var xSuggestionValue = $.trim($(pId + "-suggestion").val());
		//Verifica se houve mudança antes de disparar evento
		if ($(pId + "-suggestion-key").val() != $(pId + "-suggestion-key").attr("key")){
			//Seta valor selecionado
			$(pId + "-suggestion-key").val($(pId + "-suggestion-key").attr("key"));
			$(pId + "-data").val(xSuggestionValue);
			dbsfaces.inputText.triggerChange(pId);
		};
		dbsfaces.inputText.validate(pId);
	},
	
	//Copia valor da lista para a sugestão
	suggestionReceived: function(pId, pRow, pFromRowSelected){
		//Se encontrou alguma sugestão
		if ($(pRow).length > 0){
			var xRowKey = $(pRow).find('td .-key');
			var xRowValue = $(pRow).children('td.-dv');
			var xSuggestionValue = $.trim($(xRowValue).text());
			var xSuggestionKey = $.trim($(xRowKey).text());
			var xValue = $.trim($(pId + "-data").val());
			//Se não foi seleção da linha a partir da lista de sugestões, verifica se inicio do texto recebido é iqual a texto digitado.
			//Se não for, ignora sugestão recebida
			if (!pFromRowSelected){
				var xSuggestionValueTrunc = xSuggestionValue.substr(0, xValue.length);
				//Apaga sugestão se inicio do texto recebido não for iqual ao digitado.
				if (xSuggestionValueTrunc!=xValue){
					dbsfaces.inputText.clearSuggestion(pId);
					return;
				} 
			}
			$(pId + "-suggestion-key").attr("key", xSuggestionKey);
			$(pId + "-suggestion").val(xSuggestionValue);
			//Se texto digitado já for igual a sugestão ou
			//foi seleção a partir da lista de sugestões, aceita a sugestão automaticamente
			if (pFromRowSelected 
			|| xValue == xSuggestionValue){
				dbsfaces.inputText.acceptSuggestion(pId);
			}
		//Se não encontrou alguma sugestão
		}else{
			dbsfaces.inputText.clearSuggestion(pId);
		}
		
	},

	showFirstSuggestion: function(pId){
		//Le primeiro item da lista
		var xRow = $(pId + "-dataTable tbody").find("tr:first");

		dbsfaces.inputText.suggestionReceived(pId, xRow, false);
	},

	validate: function(pId){
		//Sai caso input não seja do tipo que controla suggestion
		if (dbsfaces.inputText.isSuggestion(pId)){
			//Verifica conteúdo da seleção
			var xSuggestionValue = $.trim($(pId + "-suggestion").val());
			var xValue = $.trim($(pId + "-data").val());
			var xNullText = $(pId + "-suggestion-key").attr("nulltext");
			if (xValue==""
			 || xValue==xNullText){
				dbsfaces.inputText.removeError(pId);
			}else if (xSuggestionValue==xValue){
				//Confirma a sugestão
				dbsfaces.inputText.removeError(pId);
			}else{
				//Campo com erro
				dbsfaces.inputText.addError(pId);
			}
		}
	},		
	
	validateNullText: function(pId){
		if (dbsfaces.inputText.isSuggestion(pId)){
			var xValue = $.trim($(pId + "-data").val());
			var xNullText = $(pId + "-suggestion-key").attr("nulltext");
			if (xValue==""){
				$(pId + "-data").val(xNullText);
			}
		}
	},

	triggerChange: function(pId){
		$(pId + "-data").trigger("change");
	},		
	
	triggerBlur: function(pId){
		wBlur = false;
		$(pId + "-data").trigger("blur");
		dbsfaces.inputText.validateNullText(pId);
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
	removeError: function(pId){
		$(pId + "-suggestion").removeClass("-error");
		$(pId + "-data").removeClass("-error");
	},

	addError: function(pId){
		$(pId + "-suggestion").addClass("-error");
		$(pId + "-data").addClass("-error");
	},
	
	fixLayout: function(pId){
		if ($(pId + "-dataTable").length == 0){
			return;
		}
		$(pId + "-dataTable tbody:first").attr('tabindex', '-1');
		$(pId + "-dataTable input:first.-foo").attr('tabindex', '-1');
		
		var xWidth = $(pId + "-data").outerWidth();
		$(pId + "-dataTable thead:first").css('min-width', xWidth);
		$(pId + "-dataTable tbody:first").css('min-width', xWidth);
	},
	
	//Indica se inputtext é do tipo suggestion
	isSuggestion: function(pId){
		return ($(pId + "-list").length > 0);
	},
	
	showList: function(pId){
		if (dbsfaces.inputText.isSuggestion(pId)){
			$(pId + "-list").css("opacity","1").show();
		}
	},

	//Esconde a lista
	hideList: function(pId){
		if (dbsfaces.inputText.isSuggestion(pId)){
			$(pId + "-list").css("opacity","0");					
			setTimeout(function() {
				$(pId + "-list").hide();
		  	}, 100);
		}
	},
	
	letterCase: function(pId, pInput, e, pLetterCase){
		if (e.metaKey){
			return; //Sai para não converte o caracter recebido quando foi tecla especial como um Ctrl-C
		}
		if(e.which >= 65 && e.which <=90){
			if (pLetterCase!="upper" &&
				pLetterCase!="lower" &&
				pLetterCase!="proper" &&
				pLetterCase!="upperfirst"){
				return;
			}
			//Só testa tamanho máximo se imput não for com suggestion
			if (!dbsfaces.inputText.isSuggestion(pId)){ 
				if (typeof pInput.attr("maxlength") != "undefined"){
					if (pInput.val().length >= parseInt(pInput.attr("maxlength"))){
						return;
					}
				}
			}
			
			var xS = pInput.get(0).selectionEnd - pInput.get(0).selectionStart;
			var xValue = pInput.val();
			var xSS = pInput.get(0).selectionStart + 1;
			var xMinusculo = 0; //Default é upper
			if (pLetterCase=="upper"){
				xMinusculo = 0; 
			}else if (pLetterCase=="lower"){
				xMinusculo = 32; //Incremento para os códigos de caracteres minúsculos
			}else if (pLetterCase=="proper"){
				if (pInput.get(0).selectionStart!=0){
					var xCharBefore = xValue.substr(pInput.get(0).selectionStart-1, 1);
					if (xCharBefore != " " && //Se não for inicio da palavra
						xCharBefore != ""){
						xMinusculo = 32; //Converte para minusculo
					}
				}
			}else if (pLetterCase=="upperfirst"){
				if (pInput.get(0).selectionStart!=0 
				 && pInput.get(0).selectionEnd!=0){
					return; //Sai para não converte o caracter recebido
				}
			}
			e.preventDefault();
			//Agrupo o inicio + valor digitado + final da texto. Isto previne os casos que a substituição no meio do texto
			xValue = xValue.substring(0, pInput.get(0).selectionStart) + String.fromCharCode(e.which + xMinusculo) + xValue.substring(pInput.get(0).selectionEnd, 200);
			pInput.val(xValue);
			pInput.get(0).setSelectionRange(xSS, xSS); 
		}	
	}
}

