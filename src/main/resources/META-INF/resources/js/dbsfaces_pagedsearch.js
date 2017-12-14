dbs_pagedSearch = function(pId) {
	var xComponentData = dbsfaces.pagedSearch.initialize($(pId));
	
	//Efetua a primeira pesquisa
	dbsfaces.pagedSearch.pvSynchronizeLists(xComponentData);
	
//	xComponentData.dom.inputData.on("keyup", function(e){
//		if (dbsfaces.pagedSearch.pvIsValidKey(e)) {//RIGHT
//			xComponentData.dom.btNewSearch.click();
//		}
//	});
	
	xComponentData.dom.inputData.on("keydown", function(e){
//		console.log('Keydown');
		var xComponent = $(dbsfaces.util.jsid(e.currentTarget.id)).parents('.dbs_pagedSearch');
		if (xComponent.attr('type') != 'suggestion'
		 && xComponent.attr('type') != 'select') {
			return dbsfaces.pagedSearch.pvIsValidKey(e);
		}
		
		if (e.which == 39) {//RIGHT
//			console.log('Suggestion aceito');
			var xItem = xComponent.children('.-container').children('.-R2').children('.-visible_container').children('.dbs_pagedSearch_container').children();
			dbsfaces.pagedSearch.pvSelectSuggestion(xComponent, xItem[0]);
		} else if (e.which == 40 && xComponent.attr('hasChildren') == 'true') {//DOWN
			if (xComponent.attr('type') != 'normal'){
				dbsfaces.pagedSearch.pvShowSuggestion(xComponentData);
			}
		}else {
			dbsfaces.pagedSearch.pvSetSuggestion(xComponentData.dom.self, "");
		}
		return dbsfaces.pagedSearch.pvIsValidKey(e);
	});
	
	/* Efetua a pesquisa para adicionar mais resultados.
	 * Clica no botão invisível para recuperar mais resultados.  
	 */
	xComponentData.dom.divResults.on("scroll", function(e){
		if ($(this).scrollTop() + $(this).height() == $(this).get(0).scrollHeight) {
			xComponentData.dom.btSearchMore.click();
		}
	});
	
	xComponentData.dom.visibleList.children().on("click", function(e){
		var xComponent;
		var xItem = $(e.currentTarget);
		if (xItem.length == 0){
			return;
		}
		xComponent = $(dbsfaces.util.jsid(e.currentTarget.id)).parents('.dbs_pagedSearch');
		dbsfaces.pagedSearch.pvSelectSuggestion(xComponent, xItem[0]);		
//		console.log('ITEM SELECIONADO: '+ xItem[0]);
	});
	
	xComponentData.dom.inputData.on("focusout", function(e){
//		console.log('focus out');
		var xComponent = $(dbsfaces.util.jsid(e.currentTarget.id)).parents('.dbs_pagedSearch');
		if (xComponent.attr('openWhenSearch') != 'true' && xComponent.attr('type') != 'normal'){
			dbsfaces.pagedSearch.pvHideSuggestion(xComponentData);
		}
	});
}

dbsfaces.pagedSearch = {
	initialize: function(pPagedSearch){
		var xInputTextData = dbsfaces.pagedSearch.initializeData(pPagedSearch);
		return xInputTextData; 
	},

	initializeData: function(pPagedSearch){
		var xId = dbsfaces.util.jsid(pPagedSearch[0].id);
		var xData = {
			dom : {
				self: pPagedSearch, //O próprio componente
				container: pPagedSearch.children(".-container"),
				searchBar: null,
				input: null,
				inputData: null,
				inputValor: null,
				inputValorData: null,
				btNewSearch: null,
				btSearchMore: null,
				divResults: null,
				visibleContainer: null,
				visibleList: null,
				invisibleContainer: null,
				invisibleList: null,
				inputIcon: null
			},
			indexPesquisa: 0,
			searching: false,
			interval: null
		}
		xData.dom.searchBar = xData.dom.container.children(".-R1");
		xData.dom.input = xData.dom.searchBar.children(".-input_search");
		xData.dom.inputData = xData.dom.input.children(".-container").children('.-th_input-data');
		xData.dom.inputValor = xData.dom.searchBar.children(".-input_valor");
		xData.dom.inputValorData = xData.dom.inputValor.children(".-container").children('.-th_input-data');
		xData.dom.inputIcon = xData.dom.searchBar.children(".-small");
		xData.dom.btNewSearch = xData.dom.searchBar.children(".-bt_new_search");
		xData.dom.btSearchMore = xData.dom.searchBar.children(".-bt_search_more");
		xData.dom.divResults = xData.dom.container.children(".-R2");
		xData.dom.visibleContainer = xData.dom.divResults.children(".-visible_container");
		xData.dom.visibleList = xData.dom.visibleContainer.children(".dbs_pagedSearch_container");
		xData.dom.invisibleContainer = xData.dom.divResults.children(".-invisible_container");
		xData.dom.invisibleList = xData.dom.invisibleContainer.children(".-invisible_list");
		pPagedSearch.data("data", xData);
		return xData;
	},

	/* Limpa as listas de ativos. Tanto a Visível quanto a invisível. */
	newSearch: function(e){
		var xPSData = dbsfaces.pagedSearch.pvGetComponetData(e);
		
		if (e.status == "begin"){
			//Reseta o index e as listas
			xPSData.indexPesquisa = 0;
			xPSData.dom.visibleList.empty();
			xPSData.dom.invisibleList.empty();
			if (xPSData.dom.self.attr('type') == 'suggestion'
			 || xPSData.dom.self.attr('type') == 'select') {
				dbsfaces.pagedSearch.pvSetSuggestion(xPSData.dom.self, "");
			}
		}
		//Configura a pesquisa
		dbsfaces.pagedSearch.pvSetSearching(xPSData, e);
	},

	setSearching: function (e) {
		dbsfaces.pagedSearch.pvSetSearching(dbsfaces.pagedSearch.pvGetComponetData(e), e);
	},

	pvGetComponetData: function(e) {
		xEle = $(e.source);
		if (xEle.length == 0){
			return;
		}
		return xEle.closest('.dbs_pagedSearch').data('data');
	},
	
	pvSetSearching: function (pComponentData, pEvent) {
		if (pEvent.status == "begin"){
			//Mostra o Loading
			dbsfaces.pagedSearch.pvShowLoading(pComponentData);
			//Ligar o flag de pesquisa
			pComponentData.searching = true;
			dbsfaces.pagedSearch.pvActivateInterval(pComponentData);
		}else if (pEvent.status == "success" || pEvent.status == "error"){
			//Desligar o flag de pesquisa
			pComponentData.searching = false;
		}
	},
	
	/* Inicia o interval de pesquisa */
	pvActivateInterval: function(pComponentData) {
		if (pComponentData.interval != null) {
			/* Uma requisição nova foi feita antes de terminar a anterior
			 * Interromperá a anterior e iniciará a nova
			 */
			clearInterval(pComponentData.interval);
			pComponentData.interval = null;
			//Limpa tudo
			pComponentData.indexPesquisa = 0;
			pComponentData.dom.visibleList.empty();
			pComponentData.dom.invisibleList.empty();
		}
		pComponentData.interval = setInterval(function(){
			dbsfaces.pagedSearch.pvUpdateList(pComponentData);
		}, 300);
	},
	
	/* Efetua o update da Lista Invisível caso a flag esteja ligada */
	pvUpdateList: function (pComponentData) {
		//Update da lista invisível
		dbsfaces.ajax.request(pComponentData.dom.input[0].id, null, pComponentData.dom.invisibleContainer[0].id, function(e) {
			xEle = $(e.source);
			if (xEle.length == 0){
				return;
			}
			if (e.status == "success"){
				dbsfaces.pagedSearch.pvSynchronizeLists(pComponentData);
			}
		}, null, null, 0);
		//Se não estiver mais processando, limpa o interval
		if (!pComponentData.searching){
			//Cancela o Interval
			clearInterval(pComponentData.interval);
			pComponentData.interval = null;
			//Esconde o Loading
			dbsfaces.pagedSearch.pvHideLoading(pComponentData);
			//Limpa a lista invisível
			pComponentData.dom.invisibleList.empty();
			//Copia o valor mostrado para o suggestion
//			if (pComponentData.dom.inputData.val() != "") {
//				dbsfaces.pagedSearch.pvCopyValueToSuggestion(pComponentData);
//			}
			//Recofigura o componente
			dbs_pagedSearch(dbsfaces.util.jsid(pComponentData.dom.self[0].id)); 
		}
	},
	
	/* Move os itens da lista invisível para a lista visível. */
	pvSynchronizeLists: function(pComponentData){
		if (pComponentData.dom == null) {
			return;
		}
		var xVisibleList = pComponentData.dom.visibleList;
		var xInvisibleList = $(dbsfaces.util.jsid(pComponentData.dom.invisibleList[0].id) +' .-item'); 
		
		for (var i = pComponentData.indexPesquisa; i < xInvisibleList.length; i++) {
			var xItem = xInvisibleList[i];
			if (xItem != null
			 && xVisibleList[0].querySelector(dbsfaces.util.jsid(xItem.id)) == null){
				 xVisibleList.append(xItem);
			}
			pComponentData.indexPesquisa++;
		}
		//Copia o valor mostrado para o suggestion
		if (pComponentData.dom.inputData.val() != "") {
			dbsfaces.pagedSearch.pvCopyValueToSuggestion(pComponentData);
		}
	},
	
	/**
	 * Copia o valor do suggestion para o campo inputSearch
	 */
	pvCopyValueToSuggestion: function(pComponentData){
//		console.log('Copiando');
		var xItemDisplayValue = $(pComponentData.dom.visibleList.children()[0]).children(".-item_display_value").children(".-container").children(".-th_input-data");
		var xSearchParam = pComponentData.dom.input.children(".-container").children(".-th_input-data")[0].value;
		if (xItemDisplayValue[0] != null 
		 && (xItemDisplayValue[0].value.toUpperCase()).match("^"+xSearchParam.toUpperCase()) != null) {
			dbsfaces.pagedSearch.pvSetSuggestion(pComponentData.dom.self, xItemDisplayValue[0].value);
		} else {
			dbsfaces.pagedSearch.pvSetSuggestion(pComponentData.dom.self, "");
		}
	},
	
	/**
	 * Configura o valor do campo Suggestion
	 */
	pvSetSuggestion: function(pComponent, pValue) {
//		if (pComponentData.dom.self.attr('type') == 'suggestion'
//		 || pComponentData.dom.self.attr('type') == 'select') {
//			var xInputSuggestion = pComponentData.dom.container.children(".-R1").children(".-input_suggestion").children('.-container').children('.-th_input-data');
//			xInputSuggestion[0].value = pValue;
//		}
		if (pComponent.attr('type') == 'suggestion'
		 || pComponent.attr('type') == 'select') {
			var xInputSuggestion = pComponent.children('.-container').children(".-R1").children(".-input_suggestion").children('.-container').children('.-th_input-data');
			xInputSuggestion[0].value = pValue;
		}
	},
	
	/**
	 * Seleciona o item: configura o ROWID, a Key e o DisplayValue do item selecionado.
	 */
	pvSelectSuggestion: function(pComponent, pItem) {
		var xComponentContainer = pComponent;
		var xItemKey;
		var xItemKeyValue;
		var xItemDisplayValue;
		var xRowID;
		var xInputSearch;
		var xInputValor;
		var xSelectedRow;
		var xSelectButton;
		
		xComponentContainer = pComponent.children('.-container');
		xInputValor = $(xComponentContainer).children('.-R1').children('.-input_valor');
		xItemKey = $(pItem).children(".-item_key");
		xItemKeyValue = xItemKey.children(".-container").children(".-th_input-data")[0].value;
		xInputValor.children('.-container').children('.-th_input-data')[0].value = xItemKeyValue;
		
		xInputSearch = xComponentContainer.children(".-R1").children(".-input_search").children('.-container').children('.-th_input-data');
		xItemDisplayValue = $(pItem).children(".-item_display_value").children(".-container").children(".-th_input-data")[0].value;
		xInputSearch[0].value = xItemDisplayValue;
		//Executa o valor
		dbsfaces.ajax.request(xInputSearch.attr('id'), xInputValor.attr('id'), null, null, null, null, 0);
		
		if (pComponent.attr('type') == 'select') {
			xRowID = pItem.id.substring(0, pItem.id.length-10);
			xRowID = xRowID.substring(xRowID.length-1);
			xSelectedRow = xComponentContainer.children(".-R1").children('.-select_row').children('.-container').children('.-th_input-data');
			xSelectedRow[0].value = xRowID;
			xSelectButton = xComponentContainer.children(".-R1").children('.-bt_select_item');
			xSelectButton.click();
		}
		dbsfaces.pagedSearch.pvSetSuggestion(pComponent, "");
	},
	
	//Não fará pesquisa se for uma das teclas abaixo
	pvIsValidKey: function(e){
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
	
	pvShowSuggestion: function(pComponentData) {
		pComponentData.dom.divResults.removeClass('-hide');
		pComponentData.dom.divResults.addClass('-show');
	},
	pvHideSuggestion: function(pComponentData) {
		pComponentData.dom.divResults.removeClass('-show');
		pComponentData.dom.divResults.addClass('-hide');
	},

	pvShowLoading: function(pComponentData) {
		pComponentData.dom.inputIcon.removeClass('-i_find');
		pComponentData.dom.inputIcon.addClass('-loading');
	},

	pvHideLoading: function(pComponentData) {
		pComponentData.dom.inputIcon.removeClass('-loading');
		pComponentData.dom.inputIcon.addClass('-i_find');
	},
	
	pvSelectClick: function(e){
		
	}

//	pvCopyValor: function (pComponentData) {
//		pComponentData.dom.inputValorData[0].value = pComponentData.dom.inputData[0].value;
//		dbsfaces.ajax.request(pComponentData.dom.inputValor[0].id, pComponentData.dom.inputValor[0].id, null, null, null, null, 0);
//	},
}