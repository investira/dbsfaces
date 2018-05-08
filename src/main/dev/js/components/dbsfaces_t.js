$(document).ready(function(){
	//SLIDER
	$(".-t_slide > div").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
//		console.log("transitend\t" + e.target);
		var xThis = $(this);
		var xParent = xThis.parent();
		var xNext = xThis.next();
		if (xNext.length > 0){
			if (xParent.hasClass("-in")){
//				dbsfaces.t.slide.moveIn(xNext);
			}else if (xParent.hasClass("-t_slide-left")){
//				dbsfaces.t.slide.moveLeft(xNext);
			}else if (xParent.hasClass("-t_slide-right")){
//				dbsfaces.t.slide.moveRight(xNext);
			}
//			xNext.css("left", $(this).css("left"));
//			dbsfaces.ui.cssAllBrowser(xNext, "transition-timing-function",  xThis.css("transition-timing-function"));
		}
		return false;
	});
	
}); 

dbsfaces.t = {}

dbsfaces.t.slide = {
	moveIn: function(pSlide){
		dbsfaces.t.slide.pvMove(pSlide, "-t_slide-in");
	},	
	moveOutLeft: function(pSlide){
		dbsfaces.t.slide.pvMove(pSlide, "-t_slide-left");
	},	
	moveOutRight: function(pSlide){
		dbsfaces.t.slide.pvMove(pSlide, "-t_slide-right");
	},	
	pvMove: function(pSlide, pPosition){
		pSlide.removeClass("-t_slide-left").removeClass("-t_slide-right").removeClass("-t_slide-in").addClass(pPosition);
		pSlide.trigger("change", pPosition);
	}	

}
//	slideIn: function(pParent){
//		var xItem = pParent.children().first();
//		xItem.css("left","0");
//		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-out");
//	},
//
//	slideLeft: function(pParent){
//		var xItem = pParent.children().first();
//		xItem.css("left","-100vw");
//		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-in");
//	},
//		
//	slideRight: function(pParent){
//		var xItem = pParent.children().first();
//		xItem.css("left","100vw");
//		dbsfaces.ui.cssAllBrowser(xItem, "transition-timing-function", "ease-in");
//	}
//}

