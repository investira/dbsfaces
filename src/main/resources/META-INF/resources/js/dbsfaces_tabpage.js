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
//	setSelected: function(pTabPageData){
//		var xTabId = pTabPageData.dom.parent[0].id;
//		var xTabPageId = pTabPageData.dom.self[0].id;
//		var xInputId = xTabId + ":input";
//		var xSelectedTab = $(dbsfaces.util.jsid(xInputId)).val().trim();
//		if (xTabPageId.toUpperCase() == xSelectedTab.toUpperCase()){
//			dbsfaces.tab.selectTabPage(xTabPageId);
//		}
//	}
//	setSelected: function(pId){
//		var xTabId = $(pId).closest(".dbs_tab").get(0).id;
//		var xTabPageId = $(pId).get(0).id;
//		var xInputId = xTabId + ":input";
//		var xSelectedTab = $(dbsfaces.util.jsid(xInputId)).val().trim().toUpperCase();
//		if (xTabPageId.toUpperCase() == xSelectedTab){
//			dbsfaces.tab.showTab(pId, xTabPageId);
//		}
//	}
}
