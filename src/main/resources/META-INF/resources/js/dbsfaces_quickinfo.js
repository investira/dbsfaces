dbs_quickInfo = function(pId) {
	$(pId + " > .-container").on("mouseenter", function(e){
		dbsfaces.quickInfo.showInfo(pId);
	});
	$(pId + " > .-container").on("mouseleave", function(e){
		dbsfaces.quickInfo.hideInfo(pId);
	});
	
}


dbsfaces.quickInfo = {
	showInfo: function(pId){
		var xInfo = $(pId + " > .-container > .-content");
		var xLeft = $(pId).offset().left;
		xInfo.css({left: xLeft});
		xInfo.show();
	},
	
	hideInfo: function(pId){
		var xInfo = $(pId + " > .-container > .-content");
		xInfo.hide();
	}
	
}

