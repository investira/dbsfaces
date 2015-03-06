dbs_quickInfo = function(pId) {
	$(pId + " > .-container").on("mouseenter", function(e){
		dbsfaces.quickInfo.showInfo(pId);
	});
	$(pId + " > .-container").on("mouseleave", function(e){
		dbsfaces.quickInfo.hideInfo(pId);
	});
	
	$(pId).offsetParent().scroll(function(e){
		console.log("moved");
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


//$(window).resize(function() {
//	pvFooterShow();
//});
//
//function pvFooterShow(){
//	var xEle = $('#footer');
//	if(window.innerHeight < 380){
//		if (xEle.is(":visible")){
//			xEle.fadeOut("slow");
//		}
//	}else{
//		if (!xEle.is(":visible")){
//			xEle.fadeIn("slow");
//		}
//	}
//}
//
//$("button.app_button").mouseleave(function(){
//	pvShowText(this, false);
//}).mouseenter(function(){
//	pvShowText(this, true);
//}).click(function(){
//	pvShowText(this, true);
//	pvShowText(this, false);
//});	
//
//function pvShowText(e, pShow){
//	var xId = "#" + $(e).attr("id") + "_text";
//	if (pShow){
//		$(xId).show();
//	}else{
//		$(xId).fadeOut("slow");
//	}
//}	