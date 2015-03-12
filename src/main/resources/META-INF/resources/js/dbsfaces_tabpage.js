dbs_tabPage = function(pId) {
	dbsfaces.tabPage.setSelected(pId);

	//Captura evento ajax dbsoft e exibe loading quando esta tabpage for source da chamada ajax
	dbsfaces.ui.ajaxShowLoading($(pId));

}

dbsfaces.tabPage = {
	setSelected: function(pId){
		var xTabId = $(pId).closest(".dbs_tab").get(0).id;
		var xTabPageId = $(pId).get(0).id;
		var xInputId = xTabId + ":input";
		var xSelectedTab = $(dbsfaces.util.jsid("#" + xInputId)).val().trim().toUpperCase();
		if (xTabPageId.toUpperCase() == xSelectedTab){
			dbsfaces.tab.showTab(pId, xTabPageId);
		}
	}
}

	