$(document).ready(function(){
	//SLIDER
	$(".-t_slide > div").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		var xThis = $(this)
		var xNext = xThis.next();
		if (xNext.length > 0){
			xNext.css("left", $(this).css("left"));
			dbsfaces.ui.cssAllBrowser(xNext, "transition-timing-function",  xThis.css("transition-timing-function"));
		}
		return false;
	});
	
	//ACCORDION
	$(".-t_accordion > div").on("mousedown touchstart", function(e){
		var xThis = $(this);
		if (xThis.hasClass("-selected")){
			dbsfaces.t.accordion.unSelect(xThis);
		}else{
			dbsfaces.t.accordion.select(xThis);
		}
		return false;
	});
	
}); 

dbsfaces.t = {}

dbsfaces.t.slide = {
	slideIn: function(pParent){
		var xItem = pParent.children().first();
		xItem.css("left","0");
		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-out");
	},

	slideLeft: function(pParent){
		var xItem = pParent.children().first();
		xItem.css("left","-100vw");
		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-in");
	},
		
	slideRight: function(pParent){
		var xItem = pParent.children().first();
		xItem.css("left","100vw");
		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-in");
	}
}

dbsfaces.t.accordion = {
	select: function(pElement){
		pElement.siblings().addClass("-hide").removeClass("-selected");
		pElement.removeClass("-hide").addClass("-selected");
		pElement.trigger("select");
//		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-out");
	},

	unSelect: function(pElement){
		pElement.parent().children().removeClass("-hide").removeClass("-selected");
	}
}