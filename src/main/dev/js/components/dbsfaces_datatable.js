dbs_dataTable = function(pId) {
	var xDataTableData = dbsfaces.dataTable.initialize($(pId));
    var wRow = xDataTableData.dom.tbody.find("> tr:first");
    var wSelectedRow;
	
	//Exclui campo para evitar a exibição do teclado 
	if (dbsfaces.util.isMobile()){
//		$(pId + " > .-container > .-header > input.-foo").remove();
//		$(pId + " > .-container > input.-foo").remove();
	}

	dbsfaces.dataTable.showOverflowShadow(xDataTableData.dom.table);
	
	//	Inibir outros selects que possam ter sido disparados por componentes filhos deste.
	xDataTableData.dom.container.on("select", function(e){
		return false;
	});
	
	xDataTableData.dom.table.scroll(function(){
		dbsfaces.dataTable.showOverflowShadow($(this));
	});


	xDataTableData.dom.self.mouseout(function(e){
		dbsfaces.dataTable.rowFocusRemove(xDataTableData);
	})


	/*hover*/
	xDataTableData.dom.rows.off('mouseover.datatable'); 
	xDataTableData.dom.rows.on('mouseover.datatable', function(e){
		dbsfaces.dataTable.rowFocusAdd(xDataTableData, $(this));
	});

	xDataTableData.dom.rows.focusin(function(e){
		//Somente seleciona se for diferente do selecionado anteriormente
		if (this != wSelectedRow){
			wSelectedRow = this;
			dbsfaces.dataTable.rowSelect(xDataTableData, $(this));
		}
	});
	
	/*Força o foco no click*/
	xDataTableData.dom.rows.click(function(e){
		//Se click foi em campo de input, não precisa forçar o foco no input-foo, para não dar problema na edição diretamente no grid
		if ($(e.target).hasClass("-th_input-data:not(.-readOnly)")
		 || $(e.target).is("input:not(.-readOnly)")){
			dbsfaces.dataTable.headFocusAdd(xDataTableData);
		}else{
			//Força foco no input para possibilitar a navegação pelas setas(up/down)
			xDataTableData.dom.input.focus().click();
		}
		//Somente seleciona se for diferente do selecionado anteriormente
		if (this != wSelectedRow){
			wSelectedRow = this;
			dbsfaces.dataTable.rowSelect(xDataTableData, $(this));
		}
//		e.preventDefault();
	});
	
	/*Executa o click na linha */
	xDataTableData.dom.rows.dblclick(function (e){
		//ignora execução padrão do dblclick e marca a linha
		if (!$(pId).hasClass("-formStyle_Table")){
			var xB = $(this).find("td > .-selectOne");
	    	if (xB.length > 0){
	    		e.stopImmediatePropagation();
				e.preventDefault();
	    		dbsfaces.dataTable.headFocusRemove(xDataTableData);
	       		dbsfaces.dataTable.rowFocusRemove(xDataTableData);
	       		dbsfaces.dataTable.rowDeselect(xDataTableData);
	       		//Submit da seleção
	    		xB.click();
	    	}
	    	return false;
		}
    });

	/*Evitar a propagação do evento click do campo e dispara o click para o datagrid*/
	xDataTableData.dom.input.click(function(e){
		e.stopImmediatePropagation();
		xDataTableData.dom.self.trigger('click');
		return false;
	});
	
	xDataTableData.dom.input.on("select", function(e){
//		e.stopImmediatePropagation(); 
		return false;
	});

	
	xDataTableData.dom.input.keydown(function(e){
		if(e.keyCode==40 || //DOWN
		   e.keyCode==38){  //UP
			dbsfaces.dataTable.moveToNextOrPreviousRow(xDataTableData, e.keyCode);
			return false;
		}
	});  
	
	xDataTableData.dom.input.focusin(function(e){
		var xE = xDataTableData.dom.tbody.find("> tr.-selected");
		//Exibe foco do cabeçalho das colunas
		dbsfaces.dataTable.headFocusAdd(xDataTableData);
		e.stopPropagation();
		//Comentado em 28/08/2013 - no IE, retirava o foco do input.-foo, deixando de funcionar a nagevação por seta
		//$(pId).trigger('focus'); 
	}); 
	
	xDataTableData.dom.input.focusout(function(e){
		dbsfaces.dataTable.headFocusRemove(xDataTableData);
		e.stopPropagation();
		xDataTableData.dom.self.trigger('blur');
	}); 
	
	xDataTableData.dom.table.scroll(function(e){
		dbsfaces.dataTable.adjustWidth(pId, this);
	}); 

	
	//Click no botão pesquisar
	xDataTableData.dom.filter.find(" > .-button > .dbs_button").on("click", function(e){
		//Resete da linha selecionada anteriormente
		wSelectedRow = null;
		dbsfaces.dataTable.rowSelect(xDataTableData, wSelectedRow);
	});

	//Controle de Sort
	xDataTableData.dom.table.find(" > thead > tr > th.-sort").on("click", function(e){
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
		
		xDataTableData.dom.sort.click();
	});
	

	//Captura evento ajax dbsoft
	dbsfaces.ui.ajaxShowLoading(pId + ".dbs_dataTable");
}


dbsfaces.dataTable = {
	initialize: function(pDataTable){
		var xDataTableData = dbsfaces.dataTable.initializeData(pDataTable);
		dbsfaces.dataTable.initializeLayout(xDataTableData);
		return xDataTableData;
	},

	initializeData: function(pDataTable){
		var xData = {
			dom : {
				self: pDataTable,
				container: null,
				content: null,
				header: null,
				input: null,
				table: null,
				tbody: null,
				rows: null,
				focus: null
			}
		}
		xData.dom.container = xData.dom.self.children(".-container");
		xData.dom.content = xData.dom.container.children(".-content");
		xData.dom.input = xData.dom.container.children("input.-foo");
		xData.dom.sort = xData.dom.container.children(".-sort");
		xData.dom.header = xData.dom.container.children(".-header");
		xData.dom.filter = xData.dom.header.children(".-filter");
		xData.dom.table = xData.dom.content.children("table");
		xData.dom.selectAll = xData.dom.table.children("thead").children("tr").children("th.-checkbox");
		xData.dom.tbody = xData.dom.table.children("tbody");
		xData.dom.rows = xData.dom.tbody.children("tr");
		pDataTable.data("data", xData);
		return xData;
	},
	
	initializeLayout: function(pDataTableData){
	    var wW = 15;
		wW = wW + pDataTableData.dom.filter.find(" > .-input").outerWidth();
		wW = wW + pDataTableData.dom.filter.find(" > .-button").outerWidth();
		pDataTableData.dom.self.css("min-width", wW + "px");

		dbsfaces.dataTable.pvAdjustContent(pDataTableData);
		dbsfaces.dataTable.pvPositionOnSelectedRow(pDataTableData);

//		wW = wRow.outerWidth() + 2;
//		wW = wW - wRow.children(".-CX").outerWidth();
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
		
	rowSelect: function(pDataTableData, pNew){
		if(!pDataTableData.dom.self.hasClass("-selectable")){
			return;
		}
		dbsfaces.dataTable.rowFocusRemove(pDataTableData);
		dbsfaces.dataTable.rowDeselect(pDataTableData);
		if (pNew != null){
			var xRowIndex = pNew.attr("index");
			//Valor de fato que será utilizado no submit
			pDataTableData.dom.input.val(xRowIndex);
			//Sincroniza atributo value com o value interno do componente
			pDataTableData.dom.input.attr("value", xRowIndex);
			pNew.addClass("-selected");
		}
		//Precisa ser o mesmo componente onde está o encodeBehavior
		pDataTableData.dom.self.trigger(dbsfaces.EVENT.ON_ROW_SELECTED, pNew);
	},

	rowDeselect: function(pDataTableData){
//		pDataTableData.dom.tbody.find(" > tr.-selected").removeClass("-selected");
		pDataTableData.dom.rows.filter(".-selected").removeClass("-selected");
	},
	
	rowFocusAdd: function(pDataTableData, pNew){
		dbsfaces.dataTable.rowFocusRemove(pDataTableData);
		pDataTableData.dom.focus = pNew;
		pDataTableData.dom.focus.addClass("-focus");
	},
	
	rowFocusRemove: function(pDataTableData){
		if (pDataTableData.dom.focus != null){
			pDataTableData.dom.focus.removeClass("-focus");
			pDataTableData.dom.focus = null;
		}
//		$(pId + " > .-container > .-content > table > tbody > tr.-focus").removeClass("-focus");
	},
	
	headFocusAdd: function(pDataTableData){
		pDataTableData.dom.table.find(" > thead > tr").addClass("-focus");
	},
	
	headFocusRemove: function(pDataTableData){
		pDataTableData.dom.table.find(" > thead > tr").removeClass("-focus");
	},

	
	moveToNextOrPreviousRow: function(pDataTableData, pKeyCode){
		var xNew;
		var xDirection = 0;
		var xRow = pDataTableData.dom.rows;
		var xTBody = pDataTableData.dom.tbody;

		if (xRow.filter(".-selected").length == 0){
			xNew = xRow.first();
		}else{
			if (pKeyCode==40){ //DOWN
				xNew = xRow.filter(".-selected").next();
				if (xNew.length == 0){
					xNew = xRow.last();
				}else{
					xDirection = 1;
				}
			}else if (pKeyCode==38){ //UP
				xNew = xRow.filter(".-selected").prev();
				if (xNew.length == 0){
					xNew = xRow.first();
				}else{
					xDirection = -1;
				}
			}
		}
		if (xNew.length > 0){ 
			dbsfaces.dataTable.rowSelect(pDataTableData, xNew);
			if (xDirection != 0){
				var xNewPos = xTBody.scrollTop() + xNew.position().top + xNew.outerHeight();
				var xLT = xTBody.scrollTop() + xNew.outerHeight();
				var xLB = xTBody.scrollTop() + xTBody.outerHeight();
				if ((xNewPos) > xLB ||
					(xNewPos) < xLT){
					if (xDirection < 0){
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

	adjustWidth: function(pId, e){
//		Força o tamanho da conteudo da tabela para evitar problema de exibição no FF
//		$(e).children().css("width", $(e).scrollLeft() + $(e).width());
	},

	
	//Ajuste scroll para o item selectionado
	pvPositionOnSelectedRow: function(pDataTableData){
//		var xRow = $(pId + " > .-container > .-content > table > tbody > tr");
		var xNew = pDataTableData.dom.rows.filter(".-selected");
		if (xNew.length > 0){
			xNew.parent().scrollTop(xNew.position().top - xNew.height() - xNew.parent().offset().top);
		}
	},

	pvAdjustContent: function(pDataTableData){
		var wH = pDataTableData.dom.header.actual('outerHeight');
		pDataTableData.dom.content.css("margin-top", "-" + wH + "px")
		                          .css("padding-top", wH + "px");
	}
}

