dbs_nav = function(pId) {
	var xNav = $(pId);

	dbsfaces.nav.initialize(xNav);
	
	$(pId + " > .-caption").on("click touchstart", function(e){
		dbsfaces.nav.show(xNav);
	});

	$(pId + " > .-mask").on("click touchstart", function(e){
		dbsfaces.nav.show(xNav);
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

		var xHorizontal = pNav.hasClass("-tlh")
		         	   || pNav.hasClass("-trh")
		         	   || pNav.hasClass("-blh")
		         	   || pNav.hasClass("-brh");
		
		pNav.data("t", xTop);
		pNav.data("l", xLeft);
		pNav.data("h", xHorizontal);
		pNav.data("container", pNav.find(".-nav > .-container"));
		pNav.data("caption", pNav.children(".-caption"));
		pNav.data("nav", pNav.data("container").find(".-content > nav"));
		pNav.data("padding", Number(dbsfaces.number.getOnlyNumber(pNav.find(".-nav > .-container").css("padding-left"))) + 1);
	},
	
	pvAjustLayout: function(pNav){
		var xPadding = pNav.data("padding");
		var xCaptionHeight = pNav.data("caption").get(0).getBoundingClientRect().height;
		var xCaptionWidth = pNav.data("caption").get(0).getBoundingClientRect().width;
		//Limita dimensão
		if (pNav.data("h")){
			var xWidth = pNav.data("nav").get(0).getBoundingClientRect().width;
			//Utiliza largura do caption se este for maior que largura do conteúdo do nav
			if (xCaptionWidth > xWidth){
				xWidth = xCaptionWidth;
			}
			//Limita largura a largura do conteúdo do nav
			pNav.children(".-nav").css("max-width", xWidth + (xPadding * 2));
		}else{
			//Limita altura a altura do conteúdo do nav
			pNav.children(".-nav").css("max-height", pNav.data("nav").get(0).getBoundingClientRect().height + (xPadding * 2));
		}
		if (pNav.data("t")){
			pNav.data("container").css("padding-top", xCaptionHeight + xPadding);
		}else{
			pNav.data("container").css("padding-bottom", xCaptionHeight + xPadding);
		}
		
	},

	show: function(pNav){
		//Esta fechado e vai abrir
		if (pNav.hasClass("-closed")){
			dbsfaces.nav.pvAjustLayout(pNav);
			dbsfaces.ui.disableBackgroundInputs(pNav);
		}else{
			dbsfaces.ui.enableForegroundInputs($("body"));
		}
		pNav.toggleClass("-closed");
	}
		
};

