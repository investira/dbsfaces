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
		var xTop = $(pId).offset().top;
		xInfo.css({left: xLeft})
			 .css({top: xTop});
		xInfo.show();
	},
	
	hideInfo: function(pId){
		var xInfo = $(pId + " > .-icon > .-container");
		xInfo.hide();
	}
	
}

