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
	
//	$(pId + " > .-container > .-nav").on("mousewheel touchmove", function(e){
//		e.stopImmediatePropagation();
//	});
	$(pId + " > .-container > .-mask").on("mousewheel touchmove", function(e){
//		e.preventDefault();
//		e.stopPropagation();
		e.stopImmediatePropagation();
//		return false;
	});

	$(pId + " > .-container > .-nav").touchwipe({
	     wipeLeft: function() {dbsfaces.nav.whipe(xNav, "l");},
	     wipeRight: function() {dbsfaces.nav.whipe(xNav, "r");},
	     wipeUp: function() {dbsfaces.nav.whipe(xNav, "u");},
	     wipeDown: function() {dbsfaces.nav.whipe(xNav, "d");},
	     min_move_x: 50,
	     min_move_y: 50,
	     preventDefaultEvents: true
	});

	$(pId + " > .-container > .-nav > .-iconclose").on("mousedown touchstart", function(e){
		dbsfaces.nav.show(xNav);
		return false;
	});

	$(pId + " > .-container > .-nav").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		if (xNav.hasClass("-closed")){
			$(":focus").blur();
			xNav.trigger("closed");
		}else{
			dbsfaces.ui.focusOnFirstInput($(this));
			xNav.trigger("opened");
		}
	});
	


};

dbsfaces.nav = {
	whipe: function(pNav, pDirection){
//		alert(pDirection + "\t" + pNav.data("v") + "\t" + pNav.data("l"));
		if (pDirection=="l"
		 && pNav.data("v")
		 && pNav.data("l")){
			dbsfaces.nav.show(pNav);
		}
		if (pDirection=="r"
		 && pNav.data("v")
		 && !pNav.data("l")){
			dbsfaces.nav.show(pNav);
		}
		if (pDirection=="d"
		 && !pNav.data("v")
		 && !pNav.data("t")){
			dbsfaces.nav.show(pNav);
		}
		if (pDirection=="u"
		 && !pNav.data("v")
		 && pNav.data("t")){
			dbsfaces.nav.show(pNav);
		}
	},

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
		pNav.data("iconclose", pNav.data("navgroup").children(".-iconclose"));
		pNav.data("padding", parseFloat(pNav.data("mask").css("padding-left")));
	},
	
	pvInitializeLayout: function(pNav){
		if (!pNav.hasClass("-closed")){
			dbsfaces.nav.pvOpen(pNav);
		}
		pNav.children(".-container").css("opacity", "");
		var xColor = tinycolor(pNav.data("navgroup").css("background-color"));
		xColor.setAlpha(.5);
		pNav.data("iconclose").css("background-color", xColor.toRgbString());
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
			dbsfaces.nav.pvClose(pNav);
		}
		pNav.toggleClass("-closed");
	},

	pvOpen: function(pNav){
		dbsfaces.nav.pvAjustLayout(pNav);
		dbsfaces.ui.disableBackgroundInputs(pNav);
		$("html").addClass("dbs_nav-freeze");
	},
	
	pvClose: function(pNav){
		pNav.data("navgroup").css("height", "")
		 					 .css("width", "");
		dbsfaces.ui.enableForegroundInputs($("body"));
		$("html").removeClass("dbs_nav-freeze");
	},
	
	pvAjustLayout: function(pNav){
//		var xPadding = pNav.data("padding");
		var xLimit = 95;
		var xPadding = 0;
		var xMask = pNav.data("mask");
		var xHeader = pNav.data("header");
		var xFooter = pNav.data("footer");
		var xContainer = pNav.data("container");
		var xMaskWidth = xMask.get(0).getBoundingClientRect().width * (xLimit / 100);
		var xMaskHeight = xMask.get(0).getBoundingClientRect().height * (xLimit / 100);
		var xHeaderWidth = pNav.data("iconclose").get(0).getBoundingClientRect().width;
		var xHeaderHeight = pNav.data("iconclose").get(0).getBoundingClientRect().height;
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
			pNav.data("navgroup").css("width", "");
			var xWidth = pNav.data("nav").get(0).getBoundingClientRect().width + (xPadding * 2);
			//Utiliza largura do caption se este for maior que largura do conteúdo do nav
			if (xHeaderWidth > xWidth){
				xWidth = xHeaderWidth;
			}
//			xWidth += (xPadding * 2);
			//Força largura máxima em 95% caso largura seja superior a largura da tela
			if (xWidth > xMaskWidth){
				xWidth = xLimit + "%";
			}
			//Limita largura a largura do conteúdo do nav
			pNav.data("navgroup").css("width", xWidth);
//			pNav.data("foocaption").css("height", xHeaderHeight + xPadding);
			if (pNav.data("t")){
				xContainer.css("padding-top", xHeaderHeight + xPadding);
				if (xFooterHeight != null){
					xContainer.css("padding-bottom", xFooterHeight + xPadding);
				}
			}else{
				xContainer.css("padding-bottom", xHeaderHeight + xPadding);
				if (xFooterHeight != null){
					xContainer.css("padding-top", xFooterHeight + xPadding);
				}
			}
		}else{
			pNav.data("navgroup").css("height", "");
			var xHeight = pNav.data("nav").get(0).getBoundingClientRect().height + (xPadding * 2);
			if (xHeight > xMaskHeight){
				xHeight = xLimit + "%";
			}
//			xHeight += (xPadding * 2);
			//Limita altura a altura do conteúdo do nav
			pNav.data("navgroup").css("height", xHeight);
//			pNav.data("foocaption").css("width", xHeaderHeight + xPadding);
			if (pNav.data("l")){
				xContainer.css("padding-left", xHeaderWidth + xPadding);
				if (xFooterWidth != null){
					xContainer.css("padding-right", xFooterWidth + xPadding);
				}
			}else{
				xContainer.css("padding-right", xHeaderWidth + xPadding);
				if (xFooterWidth != null){
					xContainer.css("padding-left", xFooterWidth + xPadding);
				}
			}
		}
		
	}
	
};

