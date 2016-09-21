dbs_dialog = function(pId) {
	var xNav = $(pId);

	$(window).resize(function(){
		dbsfaces.dialog.resized(xNav);
	});

	dbsfaces.dialog.initialize(xNav);

	$(pId + ":not([disabled]) > .-container > .-icon").on("click mousedown touchstart", function(e){
//		console.log("icon mousedown touchstart");
		dbsfaces.dialog.show(xNav);
		return false;
	});

	$(pId + ":not([disabled]) > .-container > .-mask").on("click mousedown touchstart", function(e){
//		console.log("mask mousedown touchstart");
		dbsfaces.dialog.show(xNav);
		return false;
	});
	
	$(pId + ":not([disabled]) > .-container > .-mask").on("mousewheel touchmove", function(e){
//		console.log("mask mousewheel touchmove");
		e.stopImmediatePropagation();
	});

	if (!xNav.data("c")) {
		$(pId + ":not([disabled]) > .-container > .-nav").touchwipe({
		     wipeLeft: function() {dbsfaces.dialog.whipe(xNav, "l");},
		     wipeRight: function() {dbsfaces.dialog.whipe(xNav, "r");},
		     wipeUp: function() {dbsfaces.dialog.whipe(xNav, "u");},
		     wipeDown: function() {dbsfaces.dialog.whipe(xNav, "d");},
		     min_move_x: 50,
		     min_move_y: 50,
		     preventDefaultEvents: true
		});
	}

	$(pId + ":not([disabled]) > .-container > .-nav > .-iconclose").on("click mousedown touchstart", function(e){
//		console.log("iconclose mousedown touchstart");
		dbsfaces.dialog.show(xNav);
		return false;
	});

	/*acabou de abrir ou fechar*/
	$(pId + ":not([disabled]) > .-container > .-nav").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
//		console.log("end transition\t");
		//Foi fechado
		if ($(this).closest(".dbs_dialog").hasClass("-closed")){
			xNav.trigger("closed");
			$(this).removeClass("-opened").addClass("-closed");
		//Foi aberto
		}else{
			$(this).removeClass("-closed").addClass("-opened");
			xNav.trigger("opened");
		}
	});
	
	$(pId + ":not([disabled]) > .-container > .-nav > .-header > .-content > .-iconcloseCentral").on("click mousedown touchstart", function(e){
//		console.log("mask mousedown touchstart");
		dbsfaces.dialog.show(xNav);
		return false;
	});
	
	var xOpened = xNav.attr('opened');
	if (xOpened == "true") {
		dbsfaces.dialog.show(xNav);
		return false;
	}
};

dbsfaces.dialog = {
	whipe: function(pNav, pDirection){
//		alert(pDirection + "\t" + pNav.data("v") + "\t" + pNav.data("l"));
		if ((pDirection=="l"
		  && pNav.data("l")
		  && pNav.data("v"))
		  
		 || (pDirection=="r"
		  && !pNav.data("l")
		  && pNav.data("v"))
		  
		 || (pDirection=="u"
		  && pNav.data("t")
		  && !pNav.data("v"))
		  
		 || (pDirection=="d"
		  && !pNav.data("t")
	      && !pNav.data("v"))){
			dbsfaces.dialog.show(pNav);
		} else {
			//TODO FAZER O SCROLL
//			alert(pDirection + "\t" + pNav.data("v") + "\t" + pNav.data("l"));
			return false;
		}
	},

	initialize: function(pNav, pTimeout){
		dbsfaces.dialog.pvInitializeData(pNav);
		dbsfaces.dialog.pvInitializeLayout(pNav);
	},
	pvInitializeData: function(pNav){
		var xTop = pNav.hasClass("-tlh")
		        || pNav.hasClass("-tlv")
		        || pNav.hasClass("-trh")
		        || pNav.hasClass("-trv");
		
		var xLeft = pNav.hasClass("-tlh")
		         || pNav.hasClass("-tlv")
		         || pNav.hasClass("-blh")
		         || pNav.hasClass("-blv");

		var xVertical = pNav.hasClass("-tlv")
		         	 || pNav.hasClass("-trv")
		         	 || pNav.hasClass("-blv")
		         	 || pNav.hasClass("-brv");
		
		var xCenter = pNav.hasClass("-tlc")
		    	   || pNav.hasClass("-trc")
		    	   || pNav.hasClass("-blc")
		    	   || pNav.hasClass("-brc");
		
		pNav.data("t", xTop);
		pNav.data("l", xLeft);
		pNav.data("v", xVertical);
		pNav.data("c", xCenter);
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
		pNav.data("progressTimeout", pNav.find("> .-container > .-nav > .-progress_timeout"));
	},
	
	pvInitializeLayout: function(pNav){
//		if (!pNav.hasClass("-closed")){
//			dbsfaces.nav.pvOpen(pNav);
//			dbsfaces.nav.pvCloseTimeout(pNav);
//		}
//		var xOpened = pNav.attr('opened');
//		if (xOpened == "true") {
//			dbsfaces.nav.show(pNav);
//		}
		
		pNav.children(".-container").css("opacity", "");
		//Configura cores
		var xColorClose = tinycolor(pNav.data("navgroup").css("background-color"));
		xColorClose.setAlpha(.5);
		pNav.data("iconclose").css("background-color", xColorClose.toRgbString());
//		var xColorNav = tinycolor(pNav.data("navgroup").css("background-color"));
//		xColorNav.setAlpha(.96);
		pNav.data("navgroup").css("box-shadow", tinycolor("rgba(0, 0, 0, 0.5") + " 0 0 .4em .2em");
//							 .css("background-color", xColorNav.toRgbString());
	},
	

	resized: function(pNav){
		if (!pNav.hasClass("-closed")){
			clearTimeout(pNav.data("resizetimeout"));
			pNav.data("resizetimeout", setTimeout(function(){
				dbsfaces.dialog.pvAjustLayout(pNav);
				},50)
			)
		}
	},

	show: function(pNav){
		//Está fechado e vai abrir
		if (pNav.hasClass("-closed")){
			dbsfaces.dialog.pvOpen(pNav);
			dbsfaces.dialog.pvCloseTimeout(pNav);
			pNav.data("nav").removeClass("-closed");
		}else{
			dbsfaces.dialog.pvClose(pNav);
			pNav.data("nav").addClass("-closed");
		}
		pNav.toggleClass("-closed");
	},

	pvOpen: function(pNav){
		dbsfaces.dialog.pvAjustLayout(pNav);
		dbsfaces.ui.disableBackgroundInputs(pNav);
		$("html").addClass("dbs_dialog-freeze");
		//Coloca o foco no primeiro campo de input dentro do nav
		dbsfaces.ui.focusOnFirstInput(pNav);
//		pNav.data("header").css("display", "block");
	},
	
	pvClose: function(pNav){
		if (pNav.data("v") || pNav.data("c")){
			pNav.data("navgroup").css("width", "0");
		}
		if (!pNav.data("v") || pNav.data("c")){
			pNav.data("navgroup").css("height", "0");
		}
		pNav.data("progressTimeout").css("animation-name", "none");
		
		dbsfaces.ui.enableForegroundInputs($("body"));
		$("html").removeClass("dbs_dialog-freeze");
		//Retira foco do componente que possuir foco
		$(":focus").blur();
	},
	
	pvForceClose: function(pNav){
		dbsfaces.dialog.pvClose(pNav);
		pNav.removeClass("-opened");
		pNav.addClass("-closed");
	},
	
	pvAjustLayout: function(pNav){
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
		if (pNav.data("v") || pNav.data("c")){
			pNav.data("navgroup").css("width", "");
			var xWidth = pNav.data("nav").get(0).getBoundingClientRect().width + (xPadding * 2);
			//Utiliza largura do caption se este for maior que largura do conteúdo do nav
			if (xHeaderWidth > xWidth){
				xWidth = xHeaderWidth;
			}
			//Força largura máxima em 95% caso largura seja superior a largura da tela
			if (xWidth > xMaskWidth){
				xWidth = xLimit + "%";
			}
			//Limita largura do conteúdo do nav
			pNav.data("navgroup").css("width", xWidth);
			
			//Configura o espaço do header e do footer
			if (pNav.data("v")){
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
			}
		}
		if (!pNav.data("v") || pNav.data("c")){
			pNav.data("navgroup").css("height", "");
			var xHeight = pNav.data("nav").get(0).getBoundingClientRect().height + (xPadding * 2);
			if (pNav.data("header").size() > 0) {
				xHeight = xHeight + pNav.data("header").get(0).getBoundingClientRect().height;
			}
			if (xHeight > xMaskHeight){
				xHeight = xLimit + "%";
			}
			//Limita altura do conteúdo do nav
			pNav.data("navgroup").css("height", xHeight);
			if (!pNav.data("c")){
				if (!pNav.data("v")){
					//Configura o espaço do footer
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
		}
	},

	pvCloseTimeout: function(pNav){
		var xTimeout = pNav.attr('timeout');
		if (xTimeout > 0) {
			pNav.data("progressTimeout").css("animation-name", "progress_timeout_animation");
			pNav.data("progressTimeout").css("animation-duration", xTimeout+"s");
			setTimeout(function () {
				dbsfaces.dialog.pvForceClose(pNav);
			}, (xTimeout*1000));
		}
	}
};

