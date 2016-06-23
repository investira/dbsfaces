dbs_nav = function(pId) {
	var xNav = $(pId);

	dbsfaces.nav.initialize(xNav);
	
	$(pId + " > .-caption").on("mousedown touchstart", function(e){
		dbsfaces.nav.show(xNav);
		return false;
	});

	$(pId + " > .-mask").on("mousedown touchstart", function(e){
		dbsfaces.nav.show(xNav);
		return false;
	});

	$(window).resize(function(){
		dbsfaces.nav.resized(xNav);
	});

};

dbsfaces.nav = {
	initialize: function(pNav){
		dbsfaces.nav.pvInitializeData(pNav);
	},

	pvInitializeData: function(pNav){
		var xTop = pNav.hasClass("-tlh")
		        || pNav.hasClass("-tlv")
		        || pNav.hasClass("-trh")
		        || pNav.hasClass("-trv");
		
		var xLeft = pNav.hasClass("-tlh")
		         || pNav.hasClass("-tlv")
		         || pNav.hasClass("-blh")
		         || pNav.hasClass("-blt");

		var xVertical = pNav.hasClass("-tlv")
		         	 || pNav.hasClass("-trv")
		         	 || pNav.hasClass("-blv")
		         	 || pNav.hasClass("-brv");
		
		pNav.data("t", xTop);
		pNav.data("l", xLeft);
		pNav.data("v", xVertical);
		pNav.data("navgroup", pNav.children(".-nav"));
		pNav.data("container", pNav.data("navgroup").children(".-container"));
		pNav.data("content", pNav.data("container").children(".-content"));
		pNav.data("nav", pNav.data("content").find("> div > nav"));
//		pNav.data("foocaption", pNav.data("container").children(".-foocaption"));
		pNav.data("caption", pNav.children(".-caption"));
		pNav.data("mask", pNav.children(".-mask"));
		pNav.data("padding", Number(dbsfaces.number.getOnlyNumber(pNav.data("mask").css("padding-left"))));
	},
	

	resized: function(pNav){
		if (!pNav.hasClass("-closed")){
			dbsfaces.nav.pvAjustLayout(pNav);
		}
	},

	show: function(pNav){
		//Está fechado e vai abrir
		if (pNav.hasClass("-closed")){
			dbsfaces.nav.pvAjustLayout(pNav);
			dbsfaces.ui.disableBackgroundInputs(pNav);
		}else{
			pNav.data("navgroup").css("height", "")
								 .css("width", "");
			dbsfaces.ui.enableForegroundInputs($("body"));
		}
		pNav.toggleClass("-closed -th_i");
	},
	
	pvAjustLayout: function(pNav){
//		var xPadding = pNav.data("padding");
		var xPadding = 0;
		var xMask = pNav.data("mask");
		var xMaskHeight = pNav.data("mask").get(0).getBoundingClientRect().height;
		var xMaskWidth = pNav.data("mask").get(0).getBoundingClientRect().width;
		var xCaptionHeight = pNav.data("caption").get(0).getBoundingClientRect().height;
		var xCaptionWidth = pNav.data("caption").get(0).getBoundingClientRect().width;
		//Limita dimensão
		if (pNav.data("v")){
			var xWidth = pNav.data("nav").get(0).getBoundingClientRect().width;
			//Utiliza largura do caption se este for maior que largura do conteúdo do nav
			if (xCaptionWidth > xWidth){
				xWidth = xCaptionWidth;
			}
			xWidth += (xPadding * 2);
			//Força largura máxima em 95% caso largura seja superior a largura da tela
			if (xWidth > xMaskWidth){
				xWidth = "95%";
			}
			//Limita largura a largura do conteúdo do nav
			pNav.data("navgroup").css("width", xWidth);
//			pNav.data("foocaption").css("height", xCaptionHeight + xPadding);
			if (pNav.data("t")){
				pNav.data("container").css("padding-top", xCaptionHeight + xPadding);
			}else{
				pNav.data("container").css("padding-bottom", xCaptionHeight + xPadding);
			}
		}else{
			var xHeight = pNav.data("nav").get(0).getBoundingClientRect().height + (xPadding * 2);
			if (xHeight > xMaskHeight){
				xHeight = "95%";
			}
			xHeight += (xPadding * 2);
			//Limita altura a altura do conteúdo do nav
			pNav.data("navgroup").css("height", xHeight);
//			pNav.data("foocaption").css("width", xCaptionHeight + xPadding);
			if (pNav.data("l")){
				pNav.data("container").css("padding-left", xCaptionWidth + xPadding);
			}else{
				pNav.data("container").css("padding-right", xCaptionWidth + xPadding);
			}
		}
		
	}
//	pvAjustLayout: function(pNav){
//		var xPadding = pNav.data("padding");
//		var xMask = pNav.data("mask");
//		var xMaskHeight = pNav.data("mask").get(0).getBoundingClientRect().height;
//		var xMaskWidth = pNav.data("mask").get(0).getBoundingClientRect().width;
//		var xCaptionHeight = pNav.data("caption").get(0).getBoundingClientRect().height;
//		var xCaptionWidth = pNav.data("caption").get(0).getBoundingClientRect().width;
//		//Limita dimensão
//		if (pNav.data("v")){
//			var xWidth = pNav.data("nav").get(0).getBoundingClientRect().width;
//			//Utiliza largura do caption se este for maior que largura do conteúdo do nav
//			if (xCaptionWidth > xWidth){
//				xWidth = xCaptionWidth;
//			}
////			xWidth += (xPadding * 2);
//			//Força largura máxima em 95% caso largura seja superior a largura da tela
//			if (xWidth > xMaskWidth){
//				xWidth = "95%";
//			}
//			//Limita largura a largura do conteúdo do nav
//			pNav.data("navgroup").css("width", xWidth);
//			if (pNav.data("t")){
//				pNav.data("container").css("padding-top", xCaptionHeight);
//			}else{
//				pNav.data("container").css("padding-bottom", xCaptionHeight);
//			}
//		}else{
//			var xHeight = pNav.data("nav").get(0).getBoundingClientRect().height + (xPadding * 2);
//			if (xHeight > xMaskHeight){
//				xHeight = "95%";
//			}
////			xHeight += (xPadding * 2);
//			//Limita altura a altura do conteúdo do nav
//			pNav.data("navgroup").css("height", xHeight);
//			if (pNav.data("l")){
//				pNav.data("container").css("padding-left", xCaptionWidth);
//			}else{
//				pNav.data("container").css("padding-right", xCaptionWidth);
//			}
//		}
//		
//	}

	
};

