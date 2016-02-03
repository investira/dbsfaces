dbs_tooltip = function(pId) {
	$(pId).mouseenter(function(e){
		dbsfaces.tooltip.showTooltip(pId);
	});

	$(pId).mouseleave(function(e){
		dbsfaces.tooltip.hideTooltip(pId);
	});
	
	$(pId + " input").focus(function(e){
		dbsfaces.tooltip.hideTooltip(pId);
	});

	$(pId + " input").click(function(e){
		dbsfaces.tooltip.hideTooltip(pId);
	});

}

dbsfaces.tooltip = {
	wTimerShow: +new Date(),
	wTimerHide: +new Date(),
	wRightLeft: 10,//2 + 8;
	wTomBottom: 5, //1 + 4;
	
	showTooltip: function(pId){
		dbsfaces.tooltip.wTimerShow = setTimeout(function(){
			//Exibe tooltip
			if (!dbsfaces.tooltip.show(pId, "tt", 1)){return;}
			//Tempo de exibição
			var xContainer = $(pId).find(".-tooltip.-tt:first").children(".-container");
			var xContent = xContainer.children(".-content");
			var xTime = dbsfaces.ui.getDelayFromTextLength(xContent.text());
			dbsfaces.tooltip.wTimerHide = setTimeout(function(){
				//Esconde tooltip gradativamente e depois apaga
				xContainer.fadeOut( "slow", function(){
					xContainer.css("opacity",0);
				});
			}, xTime);
		}, 1200); //2 Segundos -Tempo para exibir
	},

	hideTooltip: function(pId){
		dbsfaces.tooltip.hide(pId, "tt");
	},
	disableTooltip: function(pId){
		$(pId).find(".-tooltip.-tt:first").addClass("-disabled");
	},
	enableTooltip: function(pId){
		$(pId).find(".-tooltip.-tt:first").removeClass("-disabled");
	},

	hide: function(pId, pTooltipType){
		if (typeof(dbsfaces.tooltip.wTimerShow) != "undefined"){
			clearTimeout(dbsfaces.tooltip.wTimerShow);
			clearTimeout(dbsfaces.tooltip.wTimerHide);
			var xContainer = $(pId).find(".-tooltip.-" + pTooltipType + ":first > .-container");
			xContainer.hide().css("opacity",0);
		}
	},

	show: function(pId, pTooltipType, pDefaultLocation){
		var xComponent = pId;
		if (!(pId instanceof jQuery)){
			xComponent = $(pId);
		}
		if (xComponent.length == 0){
			return false;
		}
		var xTooltip = xComponent.find(".-tooltip.-" + pTooltipType + ":first");
		//Não exibi se tooltip estiver desabilitado
		if (xTooltip.hasClass("-disabled")){
			return false;
		}
		var xContainer = xTooltip.children(".-container");
		var xContent = xContainer.children(".-content");
		/* Se o foco estiver em algum compenente filho */

		//Exibe internamente, mas esconde exibição de fato para poder saber as dimensões do tooltip
        xContainer.css("opacity",0);
        xContainer.show();

        //Remove localização anterior se houver
        xTooltip.removeClass("-l1 -l2 -l3 -l4");

        //Configura como localização default e depois verifica se ficou dentro dos limites da janela principal
        var xBestLocationCode = dbsfaces.tooltip.setBestLocation(pDefaultLocation, xComponent, xContainer);

        //Adiciona class da localização ao componente
        xTooltip.addClass(dbsfaces.tooltip.getLocationClass(xBestLocationCode));
        
        //Exibe tooltip
		setTimeout(function(){
	        xContainer.css("opacity",1);
		}, 0);
        return true;
	},
	
	preShow: function(pLocationCode, pComponent, pContainer){
		var xArrowSize = 8;
		var xLeft;
		var xTop;
		
		
		//Ajuste do scroll
		xTop = (pComponent.get(0).getBoundingClientRect().top - pComponent.offset().top);
//		xLeft = (pComponent.get(0).getBoundingClientRect().left - pComponent.offset().left);
		xLeft = 0;
		
		//Top e Bottom
		if (pLocationCode == 1
		 || pLocationCode == 3){
			xLeft += pComponent.outerWidth() / 2;
			xLeft -= pContainer.outerWidth() / 2;
			//Top
			if (pLocationCode == 1){
				//Ajuste do componente e do conteúdo da tooltip
				xTop -= (pComponent.height() + pContainer.outerHeight() + xArrowSize);
			}else{
			//Bottom
				xTop += xArrowSize;
			}
		//Right e Left
		}else{
			//Ajuste do componente e do conteúdo da tooltip
			xTop -= ((pComponent.height() + pContainer.outerHeight()) / 2);
			//Right
			if (pLocationCode == 2){
				xLeft += (pComponent.outerWidth() + xArrowSize);
			}else{
			//Left
				xLeft -= (pContainer.outerWidth() + xArrowSize);
			}
		}
		
		dbsfaces.ui.transform(pContainer, "translateX(" + xLeft + "px) translateY(" + xTop + "px)");

	},

	//Retorna o código a partir no nome da class
	getLocationCode: function(pLocationClass){
		pLocationClass = pLocationClass.trim().toLowerCase();
		if (pLocationClass == dbsfaces.tooltip.getLocationClass(1)){
			return 1;
		}
		if (pLocationClass == dbsfaces.tooltip.getLocationClass(2)){
			return 2;
		}
		if (pLocationClass == dbsfaces.tooltip.getLocationClass(3)){
			return 3;
		}
		if (pLocationClass == dbsfaces.tooltip.getLocationClass(4)){
			return 4;
		}
		return 1;
	},
	
	//Retorna a class a partir do número do código
	getLocationClass: function(pLocationCode){
		if (pLocationCode == 1){
			return "-l1";
		}
		if (pLocationCode == 2){
			return "-l2";
		}
		if (pLocationCode == 3){
			return "-l3";
		}
		if (pLocationCode == 4){
			return "-l4";
		}
		return "-l1";
	},
	
	//Encontra a melhor localização, considerando a localização desejada/default
	setBestLocation: function(pDefaultLocation, pComponent, pContainer){
		var xBestLocationCode = pDefaultLocation;
		var xLocationTested = Math.pow(2, (pDefaultLocation - 1)); //Converte para binário
		//Loop pelas 4 posições posíveis(top,right,bottom,left)
		for (var xI=1; xI<=4; xI++){
			//Cria tooltip, mas não exibe. É necessário para verificação abaixo se esta dentro dos limites
			dbsfaces.tooltip.preShow(xBestLocationCode, pComponent, pContainer);
	        //Verifica se localização default esta dentro dos limites 
	        xBestLocationCode = dbsfaces.tooltip.getBestLocationCode(xBestLocationCode, pContainer);
	        //Localização OK
	        if (xBestLocationCode != 0){
	        	break;
	        //Localização não Ok
	        }else{
        		//Inverte
	        	if (xI == 1 || xI == 3){
		        	if (xLocationTested & dbsfaces.tooltip.wRightLeft){
		        		xLocationTested = (~xLocationTested & dbsfaces.tooltip.wRightLeft);
		        	}else if (xLocationTested & dbsfaces.tooltip.wTomBottom){
		        		xLocationTested = (~xLocationTested & dbsfaces.tooltip.wTomBottom);
		        	}
        		//Gira
	        	}else if (xI == 2){
		        	if (xLocationTested & dbsfaces.tooltip.wRightLeft){
		        		xLocationTested = 1; //Top
		        	}else if (xLocationTested & dbsfaces.tooltip.wTomBottom){
		        		xLocationTested = 2; //Right
		        	}
	        	}
	        }
	        //Converte para inteiro
	        xBestLocationCode = (Math.log(xLocationTested) / Math.log(2)) + 1;
		}
    	return xBestLocationCode;
	},
	
	//Verifica e re configura posição caso tenha ultrapassado os limites da tela
	getBestLocationCode: function(pDefaultLocation, pContainer){
		if (pContainer.length == 0){return;} //Força help no top se não for encontrado alternativas
		var xLocations = 0;
		var xDefaultLocation = Math.pow(2, (pDefaultLocation - 1));//Converte para binário
		//Verifica top
		if (pContainer.get(0).getBoundingClientRect().top > 0){
			xLocations += 1;
		}
		//Verifica right
		if (pContainer.get(0).getBoundingClientRect().left + pContainer.outerWidth() < $(document).width()){
			xLocations += 2;
		}
		//Verifica bottom
		if (pContainer.get(0).getBoundingClientRect().top + pContainer.outerHeight() < $(document).height()){
			xLocations += 4;
		}
		//Verifica left
		if (pContainer.get(0).getBoundingClientRect().left > 0){
			xLocations += 8;
		}
		
		//Se localizações não tiver a desejada
		if ((xLocations & xDefaultLocation) != xDefaultLocation){
			return 0;
		}
		
		//Top ou Bottom precisam ter esquera e direta ok
		if (xDefaultLocation & dbsfaces.tooltip.wTomBottom){
			//Se esquera e direta NÃO ok
			if ((xLocations & dbsfaces.tooltip.wRightLeft) != dbsfaces.tooltip.wRightLeft){
				return 0;
			}
		}
		//Esquera e direta precisam ter Top ou Bottom ok
		if (xDefaultLocation & dbsfaces.tooltip.wRightLeft){
			//Se Top ou Bottom NÃO ok
			if ((xLocations & dbsfaces.tooltip.wTomBottom) != dbsfaces.tooltip.wTomBottom){
				return 0;
			}
		}
		//Converte para inteiro
		return (Math.log(xDefaultLocation) / Math.log(2)) + 1;
	}	
}


//console.log(parseInt(pComponent.get(0).getBoundingClientRect().left) + "\t" + parseInt(pComponent.get(0).getBoundingClientRect().top) + "\t" + 
//		parseInt(pComponent.offset().left) + "\t" + parseInt(pComponent.offset().top) + "\t" + 
//		parseInt(pComponent.parent().offset().left) + "\t" + parseInt(pComponent.parent().offset().top) + "\t" +
//		parseInt(pComponent.position().left) + "\t" + parseInt(pComponent.position().top) + "\t" +
////		parseInt(pContainer.position().left) + "\t" + parseInt(pContainer.position().top) + "\t" +
////		xDialogPositionOld.left + "\t" + xDialogPositionOld.top + "\t" +
////		xDialogPositionOld.top + "\t" + xDialogPositionOld.width + "\t" +
////		parseInt(pContainer.get(0).getBoundingClientRect().left) + "\t" + parseInt(pContainer.get(0).getBoundingClientRect().top) + "\t" + 
//		parseInt(pContainer.offset().left) + "\t" + parseInt(pContainer.offset().top) + "\t" + 
//		parseInt(pComponent.outerHeight()) + "\t" + parseInt(pComponent.outerWidth()) + "\t" +
//		parseInt(pComponent.height()) + "\t" + parseInt(pComponent.width()) + "\t" +
////		parseInt(pComponent.get(0).height) + "\t" + parseInt(pComponent.get(0).width) + "\t" +
//		parseInt(pComponent.get(0).getBoundingClientRect().width) + "\t" + parseInt(pComponent.get(0).getBoundingClientRect().height) + "\t" + 
//		parseInt(pContainer.outerHeight()) + "\t" + parseInt(pContainer.outerWidth()) + '\n');
