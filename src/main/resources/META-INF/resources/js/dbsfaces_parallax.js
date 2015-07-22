dbs_parallax = function(pId) {
	dbsfaces.parallax.setZIndex($(pId));
	$(pId).off("scroll.dbs");
	$(pId).on("scroll.dbs", function(e) {
		dbsfaces.parallax.scroll(e, $(pId))
    });
	$(window).resize(function(e) {
		dbsfaces.parallax.scroll(e, $(pId));
	});
}


dbsfaces.parallax = {
	wAutoAjust: +new Date(),
	setZIndex: function(pParallax){
		var xList = [];
		var xA;
		//Armazena em xA a lista com os valores que forem diferentes para depois sortá-la		
		pParallax.find(".-container > .dbs_parallaxSection").each(function(){
			xA = $(this).data("a");
			if (xList.indexOf(xA) == -1){
				xList.push($(this).data("a"));
			}
		});
		xList.sort();
		//Atribui zIndex a seção conforma a posição dele na lista já sortada
		pParallax.find(".-container > .dbs_parallaxSection").each(function(){
			xA = $(this).data("a");
			//Busca em qual posição da lista esta o Z na lista
			for	(i = 0; i < xList.length; i++) {
				if (xList[i] == xA){
					$(this).css("z-index", i);
					break;
				};
			}
		});
	},	
	
	scroll: function(e, pParallax){
		var xST = pParallax.scrollTop();
		if (xST < 0 ||
			((pParallax.get(0).scrollHeight - pParallax.outerHeight() - xST) < 10)){
			return;
		}
		
		var xA;
		var xContainer;
		var xNewY = 0;
		var xSection;
		var xVSF;
		var xContainerTop;
		
		pParallax.find(".-container").each(function(){
			xContainer = $(this);
			xSection = $(this).children(".dbs_parallaxSection");
			//Ajusta posicão conforme scroll da tela
			xA = xSection.data("a");
//			xContainerTop = xContainer.position().top;
			//Primeira seção
			if (xNewY == 0){
				xNewY = xST;
			}else{
				//Calcula scroll individual
				xNewY = xContainer.outerHeight() - (xContainer.position().top - parseFloat(xContainer.css("margin-top")));
			}
			xNewY = -xNewY * (xA / 100);
			//Seção que já passou
			if ((xContainer.position().top + parseFloat(xContainer.css("margin-top")) + xContainer.outerHeight()) < 0){
			//Seção corrente
			}else if (xContainer.position().top < xContainer.outerHeight()){
				xContainer.css("margin-top", xNewY + "px");
			//Seção que passará	
			}else{
				xContainer.css("margin-top", "0px");
			}	
			//Fator do scroll vertical relativo a seção, sendo 1 o inicio, 0 o meio e -1 o fim. 
			xVSF = (xContainer.position().top + parseFloat(xContainer.css("margin-top")));
			xVSF = (xVSF / parseFloat( xContainer.outerHeight()));
			if (xVSF > 1){
				xVSF = 1;
			}else if (xVSF < -1){
				xVSF = -1;
			}
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
				xSection.trigger(xEvent);
			}
			
//			var xHtml;
//			xHtml = xSection.attr("class") + "<br/>" +
//					"Container top:\t" + xContainer.position().top + "<br/>" +
//					"Container height:\t" + xContainer.outerHeight() + "<br/>" +
//					"Section top:\t" + xSection.position().top + "<br/>" +
//					"Section height:\t" + xSection.outerHeight() + "<br/>" +
//					"Scroll Total:\t" + (xContainer.position().top + parseInt(xContainer.css("margin-top"))) + "<br/>" +
//					"VSF:\t" + xVSF + "<br/>" +
//					"ST:\t" + xST + "<br/>" +
//					"scrollTop:\t" + pParallax.scrollTop() + "<br/>" +
//					"scrollHeight:\t" + pParallax.get(0).scrollHeight + "<br/>" +
//					"newY:\t" + xNewY + "<br/>" +
//					"margin_top:\t" + parseInt(xContainer.css("margin-top")) + "<br/>" +
//					"zIndex:\t" + xA; 
//			xSection.html(xHtml);
		});
		clearTimeout(dbsfaces.parallax.wAutoAjust);
		dbsfaces.parallax.wAutoAjust = window.setTimeout(function(){
			pParallax.find("[data-centerattraction]").each(function(){
				var xVSF = parseFloat($(this).attr("vsf"));
				if ((xVSF > 0 && xVSF < 0.05)
				 || (xVSF < 0 && xVSF > -0.05)){
					var xDelta = pParallax.scrollTop();
					xDelta += $(this).outerHeight() * (xVSF / 2);
					if (Math.abs(pParallax.scrollTop() - xDelta) > 0.5){
						console.log(pParallax.scrollTop() - xDelta);
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
			xItem.css("margin-top", xNewY + "px");
//			console.log(xNewY + "\t"  + xOriginalTop);
		});
	}

}

