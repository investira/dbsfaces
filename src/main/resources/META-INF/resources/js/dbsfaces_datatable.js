dbs_dataTable = function(pId) {
	var wW = 15;
	var wTable = pId + " > .-container > .-content > table ";
    var wRow = $(wTable + " > tbody > tr:first");
    var wSelectedRow;
	wW = wW + $(pId + " > .-container > .-header > .-filter > .-input").outerWidth();
	wW = wW + $(pId + " > .-container > .-header > .-filter > .-button").outerWidth();
	
	dbsfaces.dataTable.initialize($(pId));
	
	$(pId).css("min-width", wW + "px");
	
	//Exclui campo para evitar a exibição do teclado 
	if (dbsfaces.util.isMobile()){
//		$(pId + " > .-container > .-header > input.-foo").remove();
//		$(pId + " > .-container > input.-foo").remove();
	}

	wW = wRow.outerWidth() + 2;
	wW = wW - wRow.children(".-CX").outerWidth();
//	$(wTable + " > tbody").css("min-width", wW);
//	$(wTable + " > thead").css("min-width", wW);
//	$(wTable + " > thead > tr").css("min-width", wW);

	dbsfaces.dataTable.adjustContent(pId);
	dbsfaces.dataTable.showOverflowShadow($(wTable));
	dbsfaces.dataTable.positionOnSelectedRow(pId);

	//	Inibir outros selects que possam ter sido disparados por componentes filhos deste.
	$(pId + " .-container").on("select", function(e){
		return false;
	});
	
	$(wTable).scroll(function(){
		dbsfaces.dataTable.showOverflowShadow($(this));
	});
	
	var xMouseover = function(e) {
		dbsfaces.dataTable.rowFocusAdd(pId, this);
	}

	var xMousemove = function(e){
		$(this).off("mousemove");
		$(wTable + " > tbody > tr").off('mouseover.datatable') 
								   .on('mouseover.datatable', xMouseover); 
	}

	/*hover*/
	$(wTable + " > tbody > tr").off('mouseover.datatable') 
							   .on('mouseover.datatable', xMouseover);

	$(pId).mouseout(function(e){
		dbsfaces.dataTable.rowFocusRemove(pId);
	})

	$(wTable + " > tbody > tr").focusin(function(e){
		//Somente seleciona se for diferente do selecionado anteriormente
		if (this != wSelectedRow){
			wSelectedRow = this;
			dbsfaces.dataTable.rowSelect(pId, this);
		}
	});
	
	/*Força o foco no click*/
	$(wTable + " > tbody > tr").click(function(e){
		//Se click foi em campo de input, não precisa forçar o foco no input-foo, para não dar problema na edição diretamente no grid
		if ($(e.target).hasClass("-th_input-data:not(.-readOnly)")
		 || $(e.target).is("input:not(.-readOnly)")){
			dbsfaces.dataTable.headFocusAdd(pId);
		}else{
			//Força foco no input para possibilitar a navegação pelas setas(up/down)
			$(pId + " > .-container > input.-foo").focus().click();
		}
		//Somente seleciona se for diferente do selecionado anteriormente
		if (this != wSelectedRow){
			wSelectedRow = this;
			dbsfaces.dataTable.rowSelect(pId, this);
		}
//		e.preventDefault();
	});
	
	/*Executa o click na linha */
	$(wTable + " > tbody > tr").dblclick(function (e){
		//ignora execução padrão do dblclick e marca a linha
		if (!$(pId).hasClass("-formStyle_Table")){
			var xB = $(this).find("td > .-selectOne");
	    	if (xB.length > 0){
	    		e.stopImmediatePropagation();
				e.preventDefault();
	    		dbsfaces.dataTable.headFocusRemove(pId);
	       		dbsfaces.dataTable.rowFocusRemove(pId);
	       		dbsfaces.dataTable.rowDeselect(pId);
	       		//Submit da seleção
	    		xB.click();
	    	}
	    	return false;
		}
    });

	/*Evitar a propagação do evento click do campo e dispara o click para o datagrid*/
	$(pId + " > .-container > input.-foo").click(function(e){
		e.stopImmediatePropagation();
		$(pId).trigger('click');
		return false;
	});
	
	$(pId + " > .-container > input.-foo").on("select", function(e){
//		e.stopImmediatePropagation(); 
		return false;
	});
	
	//Click no botão pesquisar
	$(pId + " > .-container > .-header > .-filter > .-button > .dbs_button").on("click", function(e){
		//Resete da linha selecionada anteriormente
		wSelectedRow = null;
		dbsfaces.dataTable.rowSelect(pId, wSelectedRow);
	});

	$(pId + " > .-container > input.-foo").keydown(function(e){
		if(e.keyCode==40 || //DOWN
		   e.keyCode==38){  //UP
			dbsfaces.dataTable.moveToNextOrPreviousRow(pId, e.keyCode);
			var xRow = $(wTable + " > tbody > tr");
			var xTBody = xRow.parent();
			xRow.off("mouseover.datatable");
			xTBody.off('mousemove.datatable');
			xTBody.on('mousemove.datatable', xMousemove);
			return false;
		}
	});  
	
	$(pId + " > .-container > input.-foo").focusin(function(e){
		var xE = $(wTable + " > tbody > tr.-selected");
		//Exibe foco do cabeçalho das colunas
		dbsfaces.dataTable.headFocusAdd(pId);
		e.stopPropagation();
		//Comentado em 28/08/2013 - no IE, retirava o foco do input.-foo, deixando de funcionar a nagevação por seta
		//$(pId).trigger('focus'); 
	}); 
	
	$(pId + " > .-container > input.-foo").focusout(function(e){
		dbsfaces.dataTable.headFocusRemove(pId);
		e.stopPropagation();
		$(pId).trigger('blur');
	}); 
	
	$(pId + " > .-container > .-content > table ").scroll(function(e){
		dbsfaces.dataTable.adjustWidth(pId, this);
	}); 

	//Controle de Sort
	$(pId + " .-container > .-content > table > thead > tr > th.-sort").on("click", function(e){
		var xInputSortColumn = $(pId + "\\:sortcolumn");
		var xInputSortDirection = $(pId + "\\:sortdirection");
		var xDirection = xInputSortDirection.attr("value");
		var xSortColumn = xInputSortColumn.attr("value");
		//Seta qual a coluna foi selecionada e a direção
		if (xSortColumn != $(this).attr("sortColumn")){
			xSortColumn = $(this).attr("sortColumn");
			xDirection = "A";
		//Só altera a direção da ordenação, caso não tenha alterado de coluna;
		}else{
			//Alterna as opções de direção da ordenação
			if (xDirection == "A"){
				xDirection = "D";
			}else if (xDirection == "D"){
				xDirection = "A";
			}else if (xDirection == ""){
				xDirection = "A";
			}
		}
		xInputSortColumn.attr("value", xSortColumn);
		xInputSortDirection.attr("value", xDirection);
		
		$(pId + " > .-container > .-sort").click();
	});
	

	//Captura evento ajax dbsoft
	dbsfaces.ui.ajaxShowLoading(pId + ".dbs_dataTable");
}


dbsfaces.dataTable = {
	initialize: function(pDataTable){
//		pDataTable.data("input", pDataTable.find(".-container > input.-foo"));
//		pDataTable.data("body", pDataTable.find(".-container > .-content > table > tbody"));
//		pDataTable.data("tr", pDataTable.children("tr"));
	},

	focus: function(e){
		var xTBody = $(dbsfaces.util.jsid(e.source.id) + " > .-container > .-content > table > tbody");
		//Monitora evento ajax recebido e dispara evento dbsoft
		dbsfaces.onajax(e);
		
		if (e.status=='complete'){
			jQuery.data(e.source, 'wScrollTop', xTBody.scrollTop());
		}else if (e.status=='success'){
			var wScrollTopValue = jQuery.data(e.source, 'wScrollTop');
			if (typeof(wScrollTopValue) != "undefined"){
				xTBody.scrollTop(jQuery.data(e.source, 'wScrollTop'));
			}
			$(dbsfaces.util.jsid(e.source.id) + " > .-container > input.-foo").focus();
		}
	},
		
	rowSelect: function(pId, pNew){
		if(!$(pId).hasClass("-selectable")){
			return;
		}
		dbsfaces.dataTable.rowFocusRemove(pId);
		dbsfaces.dataTable.rowDeselect(pId);
		if (pNew != null){
			var xRowIndex = $(pNew).attr("index");
			//Valor de fato que será utilizado no submit
			$(pId + " > .-container > input.-foo").val(xRowIndex);
			//Sincroniza atributo value com o value interno do componente
			$(pId + " > .-container > input.-foo").attr("value", xRowIndex);
			$(pNew).addClass("-selected");
		}
		//Precisa ser o mesmo componente onde está o encodeBehavior
		$(pId).trigger(dbsfaces.EVENT.ON_ROW_SELECTED, pNew);
//		$(pId).trigger("select.datatable");
//		$(pId + " > .-container > input.-foo").select();
	},

	rowDeselect: function(pId){
		$(pId + " > .-container > .-content > table > tbody > tr.-selected").removeClass("-selected");
	},
	
	rowFocusAdd: function(pId, pNew){
		dbsfaces.dataTable.rowFocusRemove(pId);
		$(pNew).addClass("-focus");
	},
	
	rowFocusRemove: function(pId){
		$(pId + " > .-container > .-content > table > tbody > tr.-focus").removeClass("-focus");
	},
	
	headFocusAdd: function(pId){
		$(pId + " > .-container > .-content > table > thead > tr").addClass("-focus");
	},
	
	headFocusRemove: function(pId){
		$(pId + " > .-container > .-content > table > thead > tr").removeClass("-focus");
	},

	
	moveToNextOrPreviousRow: function(pId, pKeyCode){
		var xNew;
		var xDirection = 0;
		var xRow = $(pId + " > .-container > .-content > table > tbody > tr");
		var xTBody = xRow.parent();

		if (xTBody.find(".-selected").length == 0){
			xNew = xRow.first();
		}else{
			if (pKeyCode==40){ //DOWN
				xNew = xRow.filter(".-selected").next();
				if ($(xNew).length == 0){
					xNew = xRow.last();
				}else{
					xDirection = 1;
				}
			}else if (pKeyCode==38){ //UP
				xNew = xRow.filter(".-selected").prev();
				if ($(xNew).length == 0){
					xNew = xRow.first();
				}else{
					xDirection = -1;
				}
			}
		}
		if ($(xNew).length > 0){ 
			dbsfaces.dataTable.rowSelect(pId, xNew);
			if (xDirection!=0){
				var xNewPos = xTBody.scrollTop() + xNew.position().top + xNew.outerHeight();
				var xLT = xTBody.scrollTop() + xNew.outerHeight();
				var xLB = xTBody.scrollTop() + xTBody.outerHeight();
				if ((xNewPos) > xLB ||
					(xNewPos) < xLT){
					if (xDirection<0){
						xTBody.scrollTop(xNewPos - xNew.outerHeight());
					}else{
						xTBody.scrollTop(xNewPos - xTBody.outerHeight());
					}
				}
			}
		}
	},
	
	showOverflowShadow: function(pE){
		if (pE.length == 0){return;}
		var xScrollLeftMax = pE.get(0).scrollWidth - pE.get(0).clientWidth;
		if (pE.get(0).scrollLeft == 0){
			pE.removeClass("-scrollLeft");
			pE.removeClass("-scrollLeftRight");
			if (xScrollLeftMax > 0){
				pE.addClass("-scrollRight");
			} 
		}else if (pE.get(0).scrollLeft == xScrollLeftMax){
			pE.removeClass("-scrollRight");
			pE.removeClass("-scrollLeftRight");
			if (pE.get(0).scrollLeft > 0){
				pE.addClass("-scrollLeft");
			} 
		}else if (xScrollLeftMax < pE.get(0).scrollWidth){
			pE.removeClass("-scrollLeft");
			pE.removeClass("-scrollRight");
			pE.addClass("-scrollLeftRight");
		}
	},	
	
	//Ajuste scroll para o item selectionado
	positionOnSelectedRow: function(pId){
		var xNew;
		var xRow = $(pId + " > .-container > .-content > table > tbody > tr");
		
		xNew = xRow.filter(".-selected");
		if ($(xNew).length != 0){
			xNew.parent().scrollTop($(xNew).position().top - $(xNew).height() - xNew.parent().offset().top);
		}
	},

	adjustWidth: function(pId, e){
//		Força o tamanho da conteudo da tabela para evitar problema de exibição no FF
//		$(e).children().css("width", $(e).scrollLeft() + $(e).width());
	},

	adjustContent: function(pId){
		var xHeader = $(pId + " > .-container > .-header");
		var wH = xHeader.actual('outerHeight');
		$(pId + " > .-container > .-content").css("margin-top", "-" + wH + "px")
		                                     .css("padding-top", wH + "px");
	}
}

