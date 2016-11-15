
dbs_button = function(pId) {
	dbsfaces.ui.ajaxShowLoading(pId);
	//Força to tamanho da div para ter o mesmo comportamento do button
//	$(pId + "[disabled]").css("height", $(pId).outerHeight())
//	 	  				 .css("width", $(pId).outerWidth());
//	$(pId + ".-disabled").css("height", $(pId).outerHeight())
//		 .css("width", $(pId).outerWidth());
	
//	$(pId).on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
//		var xButton = $(this);
//		if (xButton.attr("asid")){
//			$(dbsfaces.util.jsid(xButton.attr("asid"))).click();
//		}
//	});

}


dbsfaces.button = {
}

