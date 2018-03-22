dbs_tabPage = function(pId) {
	var xTabPageData = dbsfaces.tabPage.initialize($(pId));
	
	dbsfaces.tabPage.setSelected(xTabPageData);
	
	//Captura evento ajax dbsoft e exibe loading quando esta tabpage for source da chamada ajax
	dbsfaces.ui.ajaxShowLoading($(pId));

}

dbsfaces.tabPage = {
	
	initialize: function(pTabPage){
		var xTabPageData = dbsfaces.tabPage.initializeData(pTabPage);
		return xTabPageData;
	},	

	initializeData: function(pTabPage){
		var xData = {
			dom : {
				self: pTabPage,
				parent: pTabPage.closest(".dbs_tab")
			}
		}
		pTabPage.data("data", xData);
		return xData;
	},
	
	setSelected: function(pTabPageData){
		setTimeout(function(e){
			var xTabData = pTabPageData.dom.parent.data("data");
			var xSelectedTab = xTabData.dom.input.val().trim();
			if (pTabPageData.dom.self[0].id.toUpperCase() == xSelectedTab.toUpperCase()){
					dbsfaces.tab.showTabPage(xSelectedTab, xTabData);
			}
		},10);
	}
}
