dbs_progress = function(pId) {
	dbsfaces.progress.initialize($(pId));
}

dbsfaces.progress = {
	initialize: function(pProgress){
		dbsfaces.progress.pvInitializeData(pProgress);
		dbsfaces.progress.pvInitializeLayout(pProgress);
	},
	
	pvInitializeData: function(pProgress){
		pProgress.data("container", pProgress.children(".-container"));
		pProgress.data("label", pProgress.data("container").children(".-label"));
		pProgress.data("value", pProgress.data("container").children(".-value"));
	},

	pvInitializeLayout: function(pProgress){
		var xColor = tinycolor(pProgress.css("color"));
		pProgress.data("container").css("border-color", xColor.setAlpha(.2))
								   .css("background-color", xColor.setAlpha(.05));
		pProgress.data("label").css("font-size", Math.min(pProgress[0].getBoundingClientRect().height, pProgress[0].getBoundingClientRect().width) * .65);
	}
}
