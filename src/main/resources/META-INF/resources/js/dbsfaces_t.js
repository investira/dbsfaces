$(document).ready(function(){
	//SLIDER
	$(".-t_slide > div").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		var xThis = $(this);
		var xParent = xThis.parent();
		var xNext = xThis.next();
		if (xNext.length > 0){
			if (xParent.hasClass("-in")){
				dbsfaces.t.slide.moveIn(xNext);
			}else if (xParent.hasClass("-out_left")){
				dbsfaces.t.slide.moveOutLeft(xNext);
			}else if (xParent.hasClass("-out_right")){
				dbsfaces.t.slide.moveOutRight(xNext);
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
		dbsfaces.t.slide.move(pSlide, "-in");
	},	
	moveOutLeft: function(pSlide){
		dbsfaces.t.slide.move(pSlide, "-out_left");
	},	
	moveOutRight: function(pSlide){
		dbsfaces.t.slide.move(pSlide, "-out_right");
	},	
	move: function(pSlide, pPosition){
		pSlide.removeClass("-out_left").removeClass("-out_right").removeClass("-in").addClass(pPosition);
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

