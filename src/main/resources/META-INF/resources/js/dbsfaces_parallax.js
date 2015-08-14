dbs_parallax = function(pId) {
	var wScrollTimer;
	dbsfaces.parallax.setZIndex($(pId));
	dbsfaces.parallax.scroll(null, $(pId), false);
	$(pId).off("scroll.dbs");
	$(pId).on("scroll.dbs", function(e) {
		dbsfaces.parallax.scroll(e, $(pId), false);
    });
	$(window).resize(function(e) {
		dbsfaces.parallax.scroll(e, $(pId), false);
	});
}


dbsfaces.parallax = {
	wAutoAjust: +new Date(),
	setZIndex: function(pParallax){
		var xList = [];
		var xA;
		//Armazena em xA a lista com os valores que forem diferentes para depois sortá-la		
		pParallax.find(".-container > .dbs_parallaxSection").each(function(){
			xA = dbsfaces.parallax.getA($(this));
			if (xList.indexOf(xA) == -1){
				xList.push(xA);
			}
		});
		xList.sort();
		//Atribui zIndex a seção conforma a posição dele na lista já sortada
		pParallax.find(".-container > .dbs_parallaxSection").each(function(){
			xA = dbsfaces.parallax.getA($(this));
			//Busca em qual posição da lista esta o Z na lista
			for	(i = 0; i < xList.length; i++) {
				if (xList[i] == xA){
					$(this).css("z-index", i);
					break;
				};
			}
		});
	},	
	
	scroll: function(e, pParallax, pForced){
		var xST = pParallax.scrollTop();
		if (xST < 0 ||
			((pParallax.get(0).scrollHeight - pParallax.outerHeight() - xST) < 2)){
			return;
		}
		var xA;
		var xContainer;
		var xNewY = 0;
		var xSection;
		var xVSF;
		var xContainerTop;
		var xMarginTop;
		pParallax.children(".-container").each(function(){
			xContainer = $(this);
			xSection = xContainer.children(".dbs_parallaxSection");
			//Ajusta posicão conforme scroll da tela
			xA = dbsfaces.parallax.getA(xSection);
			xMarginTop = parseFloat(xContainer.css("margin-top"));
			xContainerTop = xContainer.position().top;
			//Primeira seção
			if (xNewY == 0){
				xNewY = xST;
			}else{
				//Calcula scroll individual
				xNewY = xContainer.outerHeight() - xContainerTop + xMarginTop;
			}
			xNewY = -xNewY * (xA / 100);
			
			//Seção que já passou
			if ((xContainerTop + xMarginTop + xContainer.outerHeight()) < 0){
				//Não altera o margin-top
			//Seção corrente
			}else if (xContainerTop < xContainer.outerHeight()){
				xContainer.css("margin-top", xNewY + "px");
			//Seção que passará	
			}else{
				xContainer.css("margin-top", "0px");
			}	
			//Fator do scroll vertical relativo a seção, sendo 1 o inicio, 0 o meio e -1 o fim. 
			xVSF = (xContainer.position().top + parseFloat(xContainer.css("margin-top")));
			xVSF = (xVSF / xContainer.outerHeight());
			if (xVSF > -0.003 && xVSF < 0.003){
				xVSF = 0;
			}
			if (xVSF > .99){
				xVSF = 1;
			}else if (xVSF < -.99){
				xVSF = -1;
			}
//			var xHtml;
//			xHtml = xSection.attr("class") + "<br/>" +
//					"Container top:\t" + xContainer.position().top + "<br/>" +
//					"Container height:\t" + xContainer.outerHeight() + "<br/>" +
//					"Section top:\t" + xSection.position().top + "<br/>" +
//					"Section height:\t" + xSection.outerHeight() + "<br/>" +
//					"Scroll Total:\t" + (xContainer.position().top + parseFloat(xContainer.css("margin-top"))) + "<br/>" +
//					"VSF:\t" + xVSF + "<br/>" +
//					"ST:\t" + xST + "<br/>" +
//					"scrollTop:\t" + pParallax.scrollTop() + "<br/>" +
//					"scrollHeight:\t" + pParallax.get(0).scrollHeight + "<br/>" +
//					"newY:\t" + xNewY + "<br/>" +
//					"margin_top:\t" + parseInt(xContainer.css("margin-top")) + "<br/>" +
//					"zIndex:\t" + xA; 
//			xSection.html(xHtml);
			//Dispara evento informando que houve scroll vertical
			if (xSection.attr("vsf") != xVSF){
				xSection.attr("vsf", xVSF);
				var xEvent = jQuery.Event( "vscroll");
				/**
				 * Fator de scroll vertical. sendo 1=inicio(vindo de baixo), 0=meio, -1=fim.
				 * @const {int}
				 */
				xEvent.vsf = xVSF;
				//Verifica os filhos
				dbsfaces.parallax.scrollChildren(xSection);
				//Dispara evento 
				if (!pForced){
					xSection.trigger(xEvent);
				}
			}
		});
		if (pForced){return;}
		//Alinha seção verticalmente com o centro da tela
		clearTimeout(dbsfaces.parallax.wAutoAjust);
		dbsfaces.parallax.wAutoAjust = window.setTimeout(function(){
			pParallax.find("[data-centerattraction]").each(function(){
				var xVSF = parseFloat($(this).attr("vsf"));
				if ((xVSF > 0 && xVSF < 0.30)
				 || (xVSF < 0 && xVSF > -0.30)){
					var xDelta = pParallax.scrollTop();
					xDelta += $(this).outerHeight() * (xVSF / 2);
					if (Math.abs(pParallax.scrollTop() - xDelta) > 0.5){
						pParallax.scrollTop(xDelta);
						e.preventDefault();
						return false;
					}
				}
			});
		}, 2000); //Time de delay para efetuar a chamada
	},
	
	scrollChildren: function(pSection, pVSF){
		var xVSF = pSection.attr("vsf");
		var xItem;
		var xA;
		var xItem;
		var xOriginalTop;
		var xNewY = 0.0;
		pSection.find("[a]").each(function(){
			xItem = $(this);
			xA = parseFloat(xItem.attr("a"));
			xOriginalTop = xItem.position().top + parseFloat(xItem.css("margin-top")); 
			xNewY = (xOriginalTop + (pSection.outerHeight()/2)) * xVSF * xA;
			dbsfaces.ui.transform(xItem, "translateY(" +  xNewY + "px)");
		});
	},
	
	getA: function(pSection){
		var xA = pSection.data("a");
		if (typeof(xA) == 'undefined'){
			xA = 0;
		}
		return xA;
	},
	
	getVSF: function(pSection){
		var xVSF = pSection.attr("vsf");
		if (typeof(xVSF) == 'undefined'){
			xVSF = 0;
		}
		return xVSF;
	},
	
	setCurrentSection: function(pParallax, pSection){
		setTimeout( function(){
			var xVSF = dbsfaces.parallax.getVSF(pSection);
			var xContainer = pSection.parent();
			while (xVSF > .05 || xVSF < -.05){
				var xI = (xContainer.position().top + parseFloat(xContainer.css("margin-top")));
				pParallax.scrollTop(pParallax.scrollTop() + xI);
				dbsfaces.parallax.scroll(null, pParallax, true);
				xVSF = dbsfaces.parallax.getVSF(pSection);
				pParallax.hide().show();
			}
		},0);
	}

}

