dbs_quickInfo = function(pId) {
	$(pId + " > .-icon").on("mouseenter", function(e){
		dbsfaces.quickInfo.showInfo(pId);
	});
	$(pId + " > .-icon").on("mouseleave", function(e){
		dbsfaces.quickInfo.hideInfo(pId);
	});
	
}


dbsfaces.quickInfo = {
	showInfo: function(pId){
		var xInfo = $(pId + " > .-icon > .-container");
		var xLeft = $(pId).offset().left;
		xInfo.css({left: $(pId).offset().left})
			 .css({top: $(pId).offset().top});
		xInfo.show();
	},
	
	hideInfo: function(pId){
		var xInfo = $(pId + " > .-icon > .-container");
		xInfo.hide();
	}
	
}

