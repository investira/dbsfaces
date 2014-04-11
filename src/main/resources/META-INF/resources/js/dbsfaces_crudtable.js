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
	
	$(xNoDialogEdit).keyup(function(e){
		if ($(this).children(".-C1").children("input[type='hidden']")){
			
		}
		if ($(e.target).hasClass("dbs_input-data")){
			var xInput = $(this).children(".-C1").children("input[type='hidden']");
		}
	});

}
