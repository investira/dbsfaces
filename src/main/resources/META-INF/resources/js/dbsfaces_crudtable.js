dbs_crudTable = function(pId) {
	var xCheckbox = pId + " > .-container > .dbs_dataTable > .-container > .-content > table  > tbody > tr > .-checkbox";
	var xNoDialogEdit = pId + " > .-container > .dbs_dataTable.-noDialogEdit > .-container > .-content > table  > tbody > tr";

	//Foi necessário o artifício de capturar o click, pois o update na linha que tem o checkbox, quebrava o vinculo com on(event).
	//Desta forma o vinculo é refeito a cada click.
	$(xCheckbox).click(function(e){
		$(xCheckbox + " > .dbs_checkbox").off(dbsfaces.EVENT.ON_AJAX_SUCCESS);
		$(xCheckbox + " > .dbs_checkbox").on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function (e){
			//Força o refresh do toolbar quando for selecionado o checkbox para multiplaseleção
			jsf.ajax.request(e, 'update', {render: $(pId).attr("id") + ':crudTableMessages ' + $(pId).attr("id") + ':dataTable:toolbar', onevent:dbsfaces.onajax});
		});
		
		//Apaga indicador ajax no complete, pois o checkbox que disparou, já não existe no view
		$(xCheckbox + " > .dbs_checkbox").off(dbsfaces.EVENT.ON_AJAX_COMPLETE);
		$(xCheckbox + " > .dbs_checkbox").on(dbsfaces.EVENT.ON_AJAX_COMPLETE, function (e){
			window.clearTimeout(wAjaxTimeout);
			e.stopImmediatePropagation();
			//Apaga o indicador do ajax, pois a linha que disparou(Begin) já não existe, então não receberá o SUCESS.
			dbsfaces.ui.showLoading("main",false);
		});
	});	
	

	//Controle de edição diretamente no grid
	$(xNoDialogEdit).keydown(function(e){
		if (e.which == 9 //TAB
		 || e.which == 33 //PAGEUP
		 || e.which == 34 //PAGEDOWN
		 || e.which == 35 //END
		 || e.which == 36 //HOME
		 || e.which == 37 //LEFT
		 || e.which == 38 //UP
		 || e.which == 39){ //RIGHT
			return;
		}
		
//		if(e.keyCode >= 65 && e.keyCode <=90){
		
		dbsfaces.crudTable.rowEdit(e, pId, $(this));

	});

}

dbsfaces.crudTable = {
	rowEdit: function(e, pId, pRow){
		var xCheckbox = pRow.children(".-C1").children("input[type='hidden']");
		//Inserir nova linha
		if (e.which == 40){ //DOWN
			//É a última linha
			if (pRow.next().length == 0){
				//Se foi em campo de input
				if ($(e.target).hasClass("dbs_input-data")){
					//Se já foi digitado alguma informação nesta linha. Permitindo que seja incluida outro.
					if (xCheckbox.val() == "true"){
						var xBtInsert = $(dbsfaces.util.jsid(pId + ":dataTable:insert"));
						xBtInsert.click();
					}
				}
			}
			return;
		}
		
		//Marca como checkbox que indica que foi digitado algo
		if (xCheckbox.length > 0){
			xCheckbox.val("true");
		}
	}
}
