dbs_inputText = function(pId) {
	var wTime = +new Date();
	var wTimeout;
	var wSearching = false;
	var wBlur = false;
	
	$(pId + " input").focusin(function(e){
		dbsfaces.ui.selectAll(this);
		wSearching = false;
	});

	$(pId + "-data.-upper").keydown(function(e){
		dbsfaces.inputText.letterCase($(this), e,"upper");
	});	

	$(pId + "-data.-lower").keydown(function(e){
		dbsfaces.inputText.letterCase($(this), e,"lower");
	});	

	$(pId + "-data.-proper").keydown(function(e){
		dbsfaces.inputText.letterCase($(this), e,"proper");
	});	
	
	$(pId + "-data.-upperfirst").keydown(function(e){
		dbsfaces.inputText.letterCase($(this), e,"upperfirst");
	});	
	
	$(pId + " > .-container > .-input > .dbs_input-data").focus(function(e){
		$(pId + "-suggestion").addClass("dbs_input-data-FOCUS");
	});
	
	
	$(pId + " > .-container > .-input > .dbs_input-data").blur(function(e){
		$(pId + "-suggestion").removeClass("dbs_input-data-FOCUS");
		dbsfaces.ui.selectNone(this);
		wTime = 0;

		dbsfaces.inputText.setNullText(pId);
		
		if ($(pId + "-list").length > 0){
			$(pId + "-list").css("opacity","0");					
			setTimeout(function() {
				$(pId + "-list").hide();
		  	}, 300);
		}

		//Omite blur caso pesquisa esteja em execução
		//Irá disparar após recebimento da resposta
		if (wSearching){
			wBlur = true;
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
		}
	});

	/* copia a sugestão para o input ou navega pela lista de sugestões*/
	$(pId + " > .-container > .-input > .dbs_input-data").keydown(function(e){
		if (e.which == 39 && //RIGHT
			$(this).get(0).selectionEnd == $(this).val().length) { //Se o cursor estiver no final do texto digitado pelo usuário
			e.stopImmediatePropagation();
			dbsfaces.inputText.acceptSuggestion(pId);
		}else if(e.which==40   //DOWN
			  || e.which==38){ //UP
			//Se existe suggestion, controla a exibição e a navegação
			if ($(pId + "-list").length > 0){
				e.preventDefault();
				e.stopPropagation();
				//Exibe suggestions no primeiro click
				if ($(pId + "-list").css("display") == "none"){
					$(pId + "-list").css("opacity","1").show();
				}else{
					//Navega na lista de sugestões
					dbsfaces.dataTable.selectRow(pId + "-dataTable", e.which);
				}
			}
		//Se não for tab, shift ou setas para a direira ou esquerda
		}else if (e.which != 9  //TAB
			   && e.which != 16 //SHIFT
			   && e.which != 37 //LEFT
			   && e.which != 39){ //RIGHT
			//Limpa campo caso não seja backspaces
			//Limpa campo caso haja algum texto selecionado para edição
			if (e.which != 8 || 
				(($(this).get(0).selectionEnd - $(this).get(0).selectionStart) > 0)){
				dbsfaces.inputText.clearSuggestion(pId);
			}
		}
	});
	
	$(pId + " > .-container > .-input > .dbs_input-data").on("paste", function(e){
		dbsfaces.inputText.clearSuggestion(pId);
	});

	//Faz a pesquisa ===================================================
	$(pId + " > .-container > .-input").keyup(function(e){
		if (e.which != 37  //LEFT
		 && e.which != 39  //RIGHT
		 && e.which != 40  //DOWN
		 && e.which != 38  //UP
		 && e.which != 9  //TAB
		 && e.which != 13  //ENTER
		 && !e.ctrlKey
		 && !e.altKey){
			/* delay para evitar chamadas ajax contantes */
			if (wTime == 0){
				wTime = +new Date();
			}else{
				xDelay = +new Date() - wTime;
				wTime = +new Date();
				if (xDelay < 350){
					window.clearTimeout(wTimeout); //Cancela request anterior
				}
				//Aguarda um tempo para chamar a rotina de sugestão
				wTimeout = window.setTimeout(function(){
					dbsfaces.inputText.requestSuggestion(pId);
				}, 350); //Time de delay para efetuar a chamada
			}
		}
	});

	$(pId + "-submit").on(dbsfaces.EVENT.ON_AJAX_BEGIN, function(e){
		wSearching = true;

		$(pId + " > .-container > .-input").append("<div class='-loading'></div>");
		$(pId + " > .-container > .-input > .-is_pesquisar").hide();
		e.stopPropagation();
	});
	
	$(pId + "-submit").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		wSearching = false;
		
		//Dispara o blur caso tenha sido ignorado originalmente
		if (wBlur){
			dbsfaces.inputText.triggerBlur(pId);
		}

		$(pId + " > .-container > .-input > div.-loading").remove();
		$(pId + " > .-container > .-input > .-is_pesquisar").show();
		dbsfaces.inputText.fixLayout(pId);
		dbsfaces.inputText.showFirstSuggestion(pId);
		
		e.stopPropagation();
	});
	
	$(pId + " > .-container > .-input").on(dbsfaces.EVENT.ON_ROW_SELECTED, ".-list", function(e, pRow){
		dbsfaces.inputText.updateSuggestion(pId, pRow, false);
		dbsfaces.inputText.acceptSuggestion(pId, pRow);
	});
	
	//Seta foco na lista
	$(pId + " > .-container > .-input").on("click", ".-list", function(e){
		$(pId + "-data").focus();
		
		$(this).css("opacity","0");					
		setTimeout(function() {
			$(pId + "-list").hide();
	  	}, 300);
	});
	
	dbsfaces.inputText.fixLayout(pId);
		
}


dbsfaces.inputText = {
	validate: function(pId){
		var xSuggestionValue = $.trim($(pId + "-suggestion").val());
		var xValue = $.trim($(pId + "-data").val());
		var xNullText = $(pId + "-suggestion-key").attr("nulltext");
		if (xSuggestionValue==xValue){
			dbsfaces.inputText.removeError(pId);
			if (xValue=="" ||
				xValue==xNullText){
				$(pId + "-suggestion-key").val("");
			}else{
				$(pId + "-suggestion-key").val($(pId + "-suggestion-key").attr("key"));
			}
			$(pId).trigger(dbsfaces.EVENT.ON_SUGGESTION_ACCEPTED);
		}else{
			//Considerra erro se texto for NULL_TEXT
			if (xValue!=xNullText){
				dbsfaces.inputText.addError(pId);
				$(pId + "-suggestion-key").val("");
			}		
		}
		$(pId + "-data").trigger("change");
	},		
	
	triggerBlur: function(pId){
		wBlur = false;
		$(pId + "-data").trigger("blur");
	},
	
	setNullText: function(pId){
		var xSuggestionValue = $.trim($(pId + "-suggestion").val());
		var xValue = $.trim($(pId + "-data").val());
		var xNullText = $(pId + "-suggestion-key").attr("nulltext");
		if (xValue==""
		 || xValue==xNullText){
			$(pId + "-suggestion-key").val("");
			$(pId + "-suggestion").val(xNullText);
			$(pId + "-data").val(xNullText);
			dbsfaces.inputText.removeError(pId);
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
		$(pId + "-dataTable tbody:first").attr('tabindex', '-1');
		$(pId + "-dataTable input:first.-foo").attr('tabindex', '-1');
		
		var xWidth = $(pId + "-data").outerWidth();
		$(pId + "-dataTable thead:first").css('min-width', xWidth);
		$(pId + "-dataTable tbody:first").css('min-width', xWidth);
	},
	
	requestSuggestion: function(pId){
		$(pId + "-submit").click();
	},
	
	isValidKey: function(e){
		if (e.which == 9 || //TAB
			e.which == 8 || //BACKSPACE
			e.which == 46 || //DELETE
			e.which == 37 || //LEFT
			e.which == 39 || //RIGHT
			e.ctrlKey ||
			e.altKey){
			return false;
		}else{
			return true;
		} 	
	},

	clearSuggestion: function(pId){
		$(pId + "-suggestion").val("");
		$(pId + "-suggestion-key").attr("key", "");
		dbsfaces.inputText.validate(pId);
	},

	
	acceptSuggestion: function(pId){
		var xSuggestionValue = $.trim($(pId + "-suggestion").val());
		if (xSuggestionValue.length > 0){
			$(pId + "-data").val(xSuggestionValue);
			dbsfaces.inputText.validate(pId);
		}
	},
	
	updateSuggestion: function(pId, pRow, pAjax){
		if ($(pRow).length > 0){
			var xRowKey = $(pRow).find('td .-key');
			var xRowValue = $(pRow).children('td.-dv');
			var xSuggestionValue = $.trim($(xRowValue).text());
			var xSuggestionKey = $.trim($(xRowKey).text());
			//Ignora sugestão se for prefixo for diferente do valor retornado
			if (pAjax){
				var xValue = $.trim($(pId + "-data").val());
				var xSuggestionValueTrunc = xSuggestionValue.substr(0, xValue.length);
				if (xSuggestionValueTrunc!=xValue){
					return;
				}
			}
			$(pId + "-suggestion-key").attr("key", xSuggestionKey);
			$(pId + "-suggestion").val(xSuggestionValue);
		}else{
			dbsfaces.inputText.clearSuggestion(pId);
		}
	},

	showFirstSuggestion: function(pId){
		$(pId).trigger(dbsfaces.EVENT.ON_SUGGESTION_RESPONSE);
		var xRow = $(pId + "-dataTable tbody").find("tr:first");

		dbsfaces.inputText.updateSuggestion(pId, xRow, true);
	},

	
	letterCase: function(pInput, e, pLetterCase){
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
			
			if (typeof pInput.attr("maxlength") != "undefined"){
				if (pInput.val().length >= parseInt(pInput.attr("maxlength"))){
					return;
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
				if (pInput.get(0).selectionStart!=0){
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

