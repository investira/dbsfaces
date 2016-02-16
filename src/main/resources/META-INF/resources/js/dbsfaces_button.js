
dbs_button = function(pId) {
	if (!$(pId).hasClass("-disabled")){
		dbsfaces.ui.ajaxShowLoading(pId + ".dbs_button");
		$(pId).on("click", function(e){
			if ($(this).hasClass("-disabled")){
				e.stopImmediatePropagation(); 
				return false;
			}
		});
	}else{
		//Força to tamanho da div para ter o mesmo comportamento do button
		$(pId).css("height", $(pId).outerHeight())
		 	  .css("width", $(pId).outerWidth());
	}
}


dbsfaces.button = {
}

