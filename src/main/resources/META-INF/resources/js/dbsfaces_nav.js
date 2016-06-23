dbs_nav = function(pId) {
	var xNav = $(pId);

	$(window).resize(function(){
		dbsfaces.nav.resized(xNav);
	});

	dbsfaces.nav.initialize(xNav);

	$(pId + " > .-container > .-icon").on("mousedown touchstart", function(e){
		dbsfaces.nav.show(xNav);
		return false;
	});

	$(pId + " > .-container > .-mask").on("mousedown touchstart", function(e){
		dbsfaces.nav.show(xNav);
		return false;
	});


};

dbsfaces.nav = {
	initialize: function(pNav){
		dbsfaces.nav.pvInitializeData(pNav);
		dbsfaces.nav.pvInitializeLayout(pNav);
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
		pNav.data("navgroup", pNav.find("> .-container > .-nav"));
		pNav.data("container", pNav.data("navgroup").children(".-container"));
		pNav.data("content", pNav.data("container").children(".-content"));
		pNav.data("nav", pNav.data("content").find("> div > nav"));
		pNav.data("header", pNav.data("navgroup").children(".-header"));
		pNav.data("footer", pNav.data("navgroup").children(".-footer"));
//		pNav.data("foocaption", pNav.data("container").children(".-foocaption"));
		pNav.data("icon", pNav.find("> .-container > .-icon"));
		pNav.data("mask", pNav.find("> .-container > .-mask"));
		pNav.data("padding", Number(dbsfaces.number.getOnlyNumber(pNav.data("mask").css("padding-left"))));
	},
	
	pvInitializeLayout: function(pNav){
		if (!pNav.hasClass("-closed")){
			dbsfaces.nav.pvOpen(pNav);
		}
		pNav.children(".-container").css("opacity", "");
	},
	

	resized: function(pNav){
		if (!pNav.hasClass("-closed")){
			dbsfaces.nav.pvAjustLayout(pNav);
		}
	},

	show: function(pNav, p){
		//Está fechado e vai abrir
		if (pNav.hasClass("-closed")){
			dbsfaces.nav.pvOpen(pNav);
		}else{
			pNav.data("navgroup").css("height", "")
								 .css("width", "");
			dbsfaces.ui.enableForegroundInputs($("body"));
		}
		pNav.toggleClass("-closed -th_i");
	},

	pvOpen: function(pNav){
		dbsfaces.nav.pvAjustLayout(pNav);
		dbsfaces.ui.disableBackgroundInputs(pNav);
	},
	
	pvAjustLayout: function(pNav){
//		var xPadding = pNav.data("padding");
		var xPadding = 0;
		var xMask = pNav.data("mask");
		var xHeader = pNav.data("header");
		var xFooter = pNav.data("footer");
		var xContainer = pNav.data("container");
		var xMaskWidth = xMask.get(0).getBoundingClientRect().width;
		var xMaskHeight = xMask.get(0).getBoundingClientRect().height;
		var xHeaderWidth = null;
		var xHeaderHeight = null;
		var xFooterWidth = null;
		var xFooterHeight = null;
		if (xHeader.length > 0){
			xHeaderWidth = xHeader.get(0).getBoundingClientRect().width;
			xHeaderHeight = xHeader.get(0).getBoundingClientRect().height;
		}
		if (xFooter.length > 0){
			xFooterWidth = xFooter.get(0).getBoundingClientRect().width;
			xFooterHeight = xFooter.get(0).getBoundingClientRect().height;
		}

		//Limita dimensão
		if (pNav.data("v")){
			var xWidth = pNav.data("nav").get(0).getBoundingClientRect().width;
			//Utiliza largura do caption se este for maior que largura do conteúdo do nav
			if (xHeaderWidth > xWidth){
				xWidth = xHeaderWidth;
			}
			xWidth += (xPadding * 2);
			//Força largura máxima em 95% caso largura seja superior a largura da tela
			if (xWidth > xMaskWidth){
				xWidth = "95%";
			}
			//Limita largura a largura do conteúdo do nav
			pNav.data("navgroup").css("width", xWidth);
//			pNav.data("foocaption").css("height", xHeaderHeight + xPadding);
			if (pNav.data("t")){
				if (xHeaderHeight != null){
					xContainer.css("padding-top", xHeaderHeight + xPadding);
				}
				if (xFooterHeight != null){
					xContainer.css("padding-bottom", xFooterHeight + xPadding);
				}
			}else{
				if (xHeaderHeight != null){
					xContainer.css("padding-bottom", xHeaderHeight + xPadding);
				}
				if (xFooterHeight != null){
					xContainer.css("padding-top", xFooterHeight + xPadding);
				}
			}
		}else{
			var xHeight = pNav.data("nav").get(0).getBoundingClientRect().height + (xPadding * 2);
			if (xHeight > xMaskHeight){
				xHeight = "95%";
			}
			xHeight += (xPadding * 2);
			//Limita altura a altura do conteúdo do nav
			pNav.data("navgroup").css("height", xHeight);
//			pNav.data("foocaption").css("width", xHeaderHeight + xPadding);
			if (pNav.data("l")){
				if (xHeaderHeight != null){
					xContainer.css("padding-left", xHeaderWidth + xPadding);
				}
				if (xFooterWidth != null){
					xContainer.css("padding-right", xFooterWidth + xPadding);
				}
			}else{
				if (xHeaderHeight != null){
					xContainer.css("padding-right", xHeaderWidth + xPadding);
				}
				if (xFooterWidth != null){
					xContainer.css("padding-left", xFooterWidth + xPadding);
				}
			}
		}
		
	}
	
};

