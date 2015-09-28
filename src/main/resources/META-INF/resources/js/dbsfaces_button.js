
dbs_button = function(pId) {
	dbsfaces.ui.ajaxShowLoading(pId + ".dbs_button");
	
	$(pId).on("click", function(e){
		if ($(this).hasClass("-disabled")){
			e.stopImmediatePropagation(); 
			return false;
		}
	})
}


dbsfaces.button = {
}

