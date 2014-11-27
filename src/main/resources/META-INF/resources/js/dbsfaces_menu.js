dbs_menu = function(pId) {
	dbsfaces.menu.eraseOrphans();
}

dbsfaces.menu = {
	//Retira os separadores seguidos ou quando for o último item
	eraseOrphans: function(){
		$('.dbs_menuitemSeparator').each(function(){
			var xNext = $(this).next();
			if(xNext.length == 0){
				$(this).remove();
			}else if (xNext.hasClass("dbs_menuitemSeparator")){
				$(this).remove();
			}
		});
	}
}