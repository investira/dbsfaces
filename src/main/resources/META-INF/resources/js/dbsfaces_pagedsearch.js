dbs_pagedSearch = function(pId) {
	var xComponentData = dbsfaces.pagedSearch.initialize($(pId));
	
	//Efetua a primeira pesquisa
	dbsfaces.pagedSearch.pvSynchronizeLists(xComponentData);
	
	xComponentData.dom.input.on("keydown", function(e){
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
				btSearchMore: null,
				divResults: null,
				visibleContainer: null,
				visibleList: null,
				invisibleContainer: null,
				invisibleList: null,
				loading: null
			},
			indexPesquisa: 0,
			searching: false,
			interval: null
		}
		xData.dom.searchBar = xData.dom.container.children(".-R1");
		xData.dom.input = xData.dom.searchBar.children(".-input_search");
		xData.dom.inputData = xData.dom.input.children(".-container > .-th_input-data");
		xData.dom.btSearchMore = xData.dom.searchBar.children(".-bt_search_more");
		xData.dom.divResults = xData.dom.container.children(".-R2");
		xData.dom.visibleContainer = xData.dom.divResults.children(".-visible_container");
		xData.dom.visibleList = xData.dom.visibleContainer.children(".-visible_list");
		xData.dom.invisibleContainer = xData.dom.divResults.children(".-invisible_container");
		xData.dom.invisibleList = xData.dom.invisibleContainer.children(".-invisible_list");
		xData.dom.loading = xData.dom.divResults.children(".-loading");
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
	
	pvSetSearching: function (pPSData, pEvent) {
		if (pEvent.status == "begin"){
			//Mostra o Loading
			dbsfaces.pagedSearch.pvShowLoading(pPSData);
			//Ligar o flag de pesquisa
			pPSData.searching = true;
			dbsfaces.pagedSearch.pvActivateInterval(pPSData);
		}else if (pEvent.status == "success" || pEvent.status == "error"){
			//Desligar o flag de pesquisa
			pPSData.searching = false;
		}
	},
	
	/* Inicia o interval de pesquisa */
	pvActivateInterval: function(pComponentData) {
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
			//Esconde o Loading
			dbsfaces.pagedSearch.pvHideLoading(pComponentData);
		}
	},
	
	/* Move os itens da lista invisível para a lista visível. */
	pvSynchronizeLists: function(pComponentData){
		if (pComponentData.dom == null) {
			return;
		}
		var xVisibleList = pComponentData.dom.visibleList;
		var xInvisibleList = $(dbsfaces.util.jsid(pComponentData.dom.invisibleList[0].id) +' .-paged_item'); 
		
		for (var i = pComponentData.indexPesquisa; i < xInvisibleList.length; i++) {
			var xItem = xInvisibleList[i];
			if (xItem != null
			 && xVisibleList[0].querySelector(dbsfaces.util.jsid(xItem.id)) == null){
				 xVisibleList.append(xItem);
			}
			pComponentData.indexPesquisa++;
		}
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

	pvShowLoading: function(pComponentData) {
		pComponentData.dom.loading.removeClass('-hideLoading');
		pComponentData.dom.loading.addClass('-showLoading');
	},

	pvHideLoading: function(pComponentData) {
		pComponentData.dom.loading.removeClass('-showLoading');
		pComponentData.dom.loading.addClass('-hideLoading');
	}

}