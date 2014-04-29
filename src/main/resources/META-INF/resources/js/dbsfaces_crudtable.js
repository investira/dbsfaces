dbs_crudTable = function(pId) {
	var xCheckbox = pId + " > .-container > .dbs_dataTable > .-container > .-content > table  > tbody > tr > .-checkbox";
	var xEditableRows = pId + " > .-container > .dbs_dataTable.-noDialogEdit > .-container > .-content > table  > tbody > tr";

	//Foi necessário o artifício de capturar o click, pois o update na linha que tem o checkbox, quebrava o vinculo com on(event).
	//Desta forma o vinculo é refeito a cada click.
	$(xCheckbox).click(function(e){
		$(xCheckbox + " > .dbs_checkbox").off(dbsfaces.EVENT.ON_AJAX_SUCCESS)
										 .on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function (e){
			//Força o refresh do toolbar quando for selecionado o checkbox para multiplaseleção
			jsf.ajax.request(e, 'update', {render: $(pId).attr("id") + ':crudTableMessages ' + $(pId).attr("id") + ':dataTable:toolbar', onevent:dbsfaces.onajax});
		});
		
		//Apaga indicador ajax no complete, pois o checkbox que disparou, já não existe no view
		$(xCheckbox + " > .dbs_checkbox").off(dbsfaces.EVENT.ON_AJAX_COMPLETE)
										 .on(dbsfaces.EVENT.ON_AJAX_COMPLETE, function (e){
			window.clearTimeout(wAjaxTimeout);
			e.stopImmediatePropagation();
			//Apaga o indicador do ajax, pois a linha que disparou(Begin) já não existe, então não receberá o SUCESS.
			dbsfaces.ui.showLoading("main",false);
		});
	});	
	

	//Controle de edição diretamente no grid
	$(xEditableRows).keydown(function(e){
		if (e.which == 9 //TAB
		 || e.which == 33 //PAGEUP
		 || e.which == 34 //PAGEDOWN
		 || e.which == 35 //END
		 || e.which == 36){ //HOME
			return;
		}
		
//		if(e.keyCode >= 65 && e.keyCode <=90){
		
		dbsfaces.crudTable.rowEdit(e, pId, $(this));

	});
	
//	//Posiciona a úlima linha após a inclusão
	$(dbsfaces.util.jsid(pId + ":dataTable:inserir")).on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
		dbsfaces.crudTable.setCellFocus($(xEditableRows).last(), 1);
	});

}

dbsfaces.crudTable = {
	//Incluir nova linha ou nevega entre elas
	rowEdit: function(e, pId, pRow){
		//Se foi em campo de input
		if ($(e.target).hasClass("dbs_input-data")){
			var xCheckbox = pRow.children(".-C1").children("input[type='hidden']");
			//Inclusão de nova linha
			if (e.which == 40){ //DOWN
				if (pRow.next().length == 0){
				//Inclui nova linha se estiver na última linha
					//Se já foi digitado alguma informação nesta linha. Permitindo que seja incluida outro.
					if (xCheckbox.val() == "true"){
						var xBtInserir = $(dbsfaces.util.jsid(pId + ":dataTable:inserir"));
						xBtInserir.click();
						return;
					}
				}
			}
			//Confirma
			if (e.which == 13){ //Enter
				var xBtOk = $(dbsfaces.util.jsid(pId + ":dataTable:ok"));
				xBtOk.click();
			}
			//Cancela
			if (e.which == 27){ //Esc
				var xBtOk = $(dbsfaces.util.jsid(pId + ":dataTable:cancelar"));
				xBtOk.click();
			}

			//Seleção da nova linha ou coluna
			var xCurColumn = $(e.target).closest("td");
			if (e.which == 37 //LEFT
			 || e.which == 39 //RIGHT
			 || e.which == 40 //DOWN
			 || e.which == 38){ //UP
//				var xEditing = xCurColumn.hasClass("-editing");
				var xCurCell = xCurColumn.get(0).cellIndex;
				var xDestRow = pRow;
				if (e.which == 40){ //DOWN
					xDestRow = pRow.next(); //Proximá linha
				}else if (e.which == 38){ //UP
					xDestRow = pRow.prev(); //Linha anterior
				}else if (e.which == 37){
					xCurCell++;
				}else if (e.which == 39){
					xCurCell--;
				}
				if (xCurCell != xCurColumn.get(0).cellIndex
				 || xDestRow != pRow){	
					dbsfaces.crudTable.setCellFocus(xDestRow, xCurCell);
				}
				return;
			}
			//Marca como checkbox que indica que foi digitado algo
			if (xCheckbox.length > 0){
				xCheckbox.val("true");
			}		
		}
	},
	
	//Ativa focus no input
	setCellFocus: function(pRow, pCurCell){
		if (pRow != null){
			if (pRow.length > 0){
				//Ativa foco no imput, se existir
//				$(pRow).children("td").removeClass("-editing");
				var xTD = pRow.children("td:eq(" + pCurCell + ")");
				dbsfaces.ui.focusOnFirstInput(xTD);
//				var xDestInput = pRow.children("td:eq(" + pCurCell + ")").find(".dbs_input-data");
//				if (xDestInput.length > 0){
//					xDestInput.focus();
//				}
			}
		}
	},
	
	//Ativa focus no input
	setGridFocus: function(pId){
		var xFoo = $(dbsfaces.util.jsid(pId + ":dataTable:foo"));
		xFoo.focus();
	}
	
}
