dbs_tooltip = function(pSourceComponent) {
	dbsfaces.tooltip.initialize($(pSourceComponent));
	
	$(pSourceComponent).mouseenter(function(e){
//		var xE = $(e.originalEvent.toElement || e.relatedTarget);
//		console.log(xE);
		dbsfaces.tooltip.showDelayed(pSourceComponent);
	});

	$(pSourceComponent).mouseleave(function(e){
		dbsfaces.tooltip.hide(pSourceComponent);
	});
	
	$(pSourceComponent + " input").focus(function(e){
		dbsfaces.tooltip.hide(pSourceComponent);
	});

	$(pSourceComponent + " input").click(function(e){
		dbsfaces.tooltip.hide(pSourceComponent);
	});

}

dbsfaces.tooltip = {
	wRightLeft: 10,//2 + 8(Right + Left)
	wTomBottom: 5, //1 + 4(Top + Bottom);
	
	initialize: function(pSourceComponent){
		dbsfaces.tooltip.initializeData(pSourceComponent);
		dbsfaces.tooltip.initializeLayout(pSourceComponent);
	},
	
	initializeData: function(pSourceComponent){
		var xTooltip = pSourceComponent.children(".-tooltip");
		if (xTooltip.length == 0){
			xTooltip == null;
		}else{
			xTooltip.data("parent", pSourceComponent);
			xTooltip.data("delay", xTooltip.attr("delay"));
			xTooltip.data("container", xTooltip.children(".-container"));
			xTooltip.data("content", xTooltip.data("container").children(".-content"));
			xTooltip.data("timerhide", null);
			xTooltip.data("timershow", null);
			
			xTooltip.data("dl", Number(xTooltip.attr("dl")));

			xTooltip.data("setPos", !xTooltip.isParentFixed());
			xTooltip.attr("setPos", xTooltip.data("setPos"));//Somente para exibir atributo no fonte html
		}
		pSourceComponent.data("tooltip", xTooltip);
	},
	
	initializeLayout: function(pSourceComponent){
		var xContent = pSourceComponent.data("tooltip").data("content");
		var xContainer = pSourceComponent.data("tooltip").data("container");
		xContainer.css("border-color", xContent.css("background-color"));
	},
	
	showDelayed: function(pSourceComponent){
		if (pSourceComponent == null){return;}
		var xComponent = pSourceComponent;
		if (!(xComponent instanceof jQuery)){
			xComponent = $(pSourceComponent);
		}
		var xTooltip = xComponent.data("tooltip");
		if (xTooltip == null){return;} 
		var xDelay = xTooltip.data("delay");
		//Cria timer para exibição e armazena no próprio componente
		clearTimeout(xTooltip.data("timershow"));
		xTooltip.data("timershow", 
			setTimeout(function(){
				dbsfaces.tooltip.show(xComponent);
				//Tempo de exibição em função do tamanho do conteúdo
				var xHideDelay = dbsfaces.ui.getDelayFromTextLength(xTooltip.data("content").text());
				clearTimeout(xTooltip.data("timerhide"));
				//Cria timer para escoder e armazena no próprio componente
				xTooltip.data("timerhide", 
					setTimeout(function(){
						dbsfaces.tooltip.hide(pSourceComponent);
					}, xHideDelay)
				);
			}, xDelay) //Tempo para exibir
		);
	},

	disable: function(pSourceComponent){
		var xComponent = pSourceComponent;
		if (!(xComponent instanceof jQuery)){
			xComponent = $(pSourceComponent);
		}
		var xTooltip = xComponent.data("tooltip");
		if (xTooltip == null){return;}
		xTooltip.addClass("-disabled");
	},
	enable: function(pSourceComponent){
		var xComponent = pSourceComponent;
		if (!(xComponent instanceof jQuery)){
			xComponent = $(pSourceComponent);
		}
		var xTooltip = xComponent.data("tooltip");
		if (xTooltip == null){return;}
		xTooltip.removeClass("-disabled");
	},

	hide: function(pSourceComponent){
		if (pSourceComponent == null){return;}
		var xComponent = pSourceComponent;
		if (!(xComponent instanceof jQuery)){
			xComponent = $(pSourceComponent);
		}
		var xTooltip = xComponent.data("tooltip");
		if (xTooltip == null){return;}
		if (xTooltip.data("timershow") != null){
			clearTimeout(xTooltip.data("timershow"));
			xTooltip.data("timershow", null);				
		}
		if (xTooltip.data("timerhide") != null){
			clearTimeout(xTooltip.data("timerhide"));
			xTooltip.data("timerhide", null);
		}
		xTooltip.removeClass("-show");
	},

	show: function(pSourceComponent){
		if (pSourceComponent == null){return;}
		var xComponent = pSourceComponent;
		if (!(xComponent instanceof jQuery)){
			xComponent = $(pSourceComponent);
		}
		if (xComponent.length == 0){
			return false;
		}
		
		var xTooltip = xComponent.data("tooltip");
		if (xTooltip == null){return;}
		//Não exibi se tooltip estiver desabilitado
		if (xTooltip.hasClass("-disabled")){
			return false;
		}
		
		//Configura como localização default e depois verifica se ficou dentro dos limites da janela principal
        dbsfaces.tooltip.pvSetBestLocation(xTooltip);

        //Exibe tooltip  
		xTooltip.addClass("-show");

        return true;
	},
	
	
	//Encontra a melhor localização, considerando a localização desejada/default
	pvSetBestLocation: function(pTooltip){
		var xBestLocationCode = pTooltip.data("dl"); //Utiliza inicialmente o valor do default location
		var xLocationTested = Math.pow(2, (xBestLocationCode - 1)); //Converte para binário

		//Remove localização anterior se houver
		pTooltip.removeClass("-l1 -l2 -l3 -l4");

		//Loop pelas 4 posições posíveis(top,right,bottom,left)
		for (var xI=1; xI<=4; xI++){
	        //Verifica se localização default esta dentro dos limites 
	        xBestLocationCode = dbsfaces.tooltip.pvGetBestLocationCode(pTooltip, xBestLocationCode);
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
        //Adiciona class da localização ao componente
		pTooltip.addClass(dbsfaces.tooltip.pvGetLocationClass(xBestLocationCode));
 	},
	
	//Verifica e re configura posição caso tenha ultrapassado os limites da tela
	pvGetBestLocationCode: function(pTooltip, pLocation){
		//Posiciona tooltip, mas não exibe. É necessário para verificação abaixo se esta dentro dos limites
		dbsfaces.tooltip.pvPreShow(pTooltip, pLocation);

		var xContainer = pTooltip.data("container");
		var xLocations = 0;
		var xLocation = Math.pow(2, (pLocation - 1));//Converte para binário
		//Verifica top
		if (xContainer.get(0).getBoundingClientRect().top > 0){
			xLocations += 1;
		}
		//Verifica right
		if (xContainer.get(0).getBoundingClientRect().left + xContainer.outerWidth() < $(document).width()){
			xLocations += 2; 
		}
		//Verifica bottom
		if (xContainer.get(0).getBoundingClientRect().top + xContainer.outerHeight() < $(document).height()){
			xLocations += 4;
		}
		//Verifica left
		if (xContainer.get(0).getBoundingClientRect().left > 0){
			xLocations += 8;
		}
		
		//Se localizações não tiver a desejada
		if ((xLocations & xLocation) != xLocation){
			return 0;
		}
		
		//Top ou Bottom precisam ter esquera e direta ok
		if (xLocation & dbsfaces.tooltip.wTomBottom){
			//Se esquera e direta NÃO ok
			if ((xLocations & dbsfaces.tooltip.wRightLeft) != dbsfaces.tooltip.wRightLeft){
				return 0;
			}
		}
		//Esquera e direta precisam ter Top ou Bottom ok
		if (xLocation & dbsfaces.tooltip.wRightLeft){
			//Se Top ou Bottom NÃO ok
			if ((xLocations & dbsfaces.tooltip.wTomBottom) != dbsfaces.tooltip.wTomBottom){
				return 0;
			}
		}
		//Converte para inteiro
		return (Math.log(xLocation) / Math.log(2)) + 1;
	},
	
	pvPreShow: function(pTooltip, pLocationCode){
		var xArrowSize = 8;
		var xComponent = pTooltip.data("parent");
		var xComponentRect = xComponent.get(0).getBoundingClientRect();
		var xTooltipRect = pTooltip.get(0).getBoundingClientRect();
		var xTop = 0;
		var xLeft = 0;
		var xContainer = pTooltip.data("container");
		var xContent = pTooltip.data("content");
		if (pTooltip.data("setPos")){
			pTooltip.css("left", xComponentRect.left + (xComponentRect.width / 2))
				    .css("top", xComponentRect.top + xComponentRect.height);
//			console.log(xComponent.offset().left + "\t" + xComponent.offset().top);
//			console.log(xComponent.offsetParent().offset().left + "\t" + xComponent.offsetParent().offset().top);
		}

//		console.log(pComponent.get(0).getBoundingClientRect().top 
//			    + "\t" + pComponent.offset().top
//			    + "\t" + pComponent.scrollTop()
//			    + "\t" + pComponent.offsetParent().offset().top
//			    + "\t" + pComponent.parent().offset().top);
////		xTop = (pContainer.get(0).getBoundingClientRect().top + pComponent.scrollParent(false).scrollTop()) - pContainer.offset().top;

		//Top e Bottom
		if (pLocationCode == 1
		 || pLocationCode == 3){
			xLeft -= xContainer.outerWidth() / 2;
			//Top
			if (pLocationCode == 1){
				//Ajuste do componente e do conteúdo da tooltip
				xTop = -(xContainer.outerHeight() + xComponent.outerHeight() + xArrowSize);
			}else{
			//Bottom
				xTop = xArrowSize;
			}
		//Right e Left
		}else{
			//Ajuste do componente e do conteúdo da tooltip
			xTop = -((xComponent.outerHeight() + xContainer.outerHeight()) / 2);
			xLeft = xArrowSize + (xComponent.outerWidth() / 2);
			//Right
			if (pLocationCode == 2){
			}else{
			//Left
				xLeft = -xLeft - xContainer.outerWidth();
			}
		}
		
		dbsfaces.ui.cssAllBrowser(xContainer, "transform-origin", parseInt(-xLeft) + "px "  + parseInt(-xTop) + "px");
		dbsfaces.ui.cssTransform(xContainer, "translate3d(" + parseInt(xLeft) + "px, " + parseInt(xTop) + "px, 0)");
		dbsfaces.ui.cssAllBrowser(xContent, "transform-origin", parseInt(-xLeft) + "px "  + parseInt(-xTop) + "px");
	},
	

	//Retorna o código a partir no nome da class
	pvGetLocationCode: function(pLocationClass){
		pLocationClass = pLocationClass.trim().toLowerCase();
		if (pLocationClass == dbsfaces.tooltip.pvGetLocationClass(2)){
			return 2;
		}
		if (pLocationClass == dbsfaces.tooltip.pvGetLocationClass(3)){
			return 3;
		}
		if (pLocationClass == dbsfaces.tooltip.pvGetLocationClass(4)){
			return 4;
		}
		return 1;
	},
	
	//Retorna a class a partir do número do código
	pvGetLocationClass: function(pLocationCode){
		return "-l" + Number(pLocationCode);
	}
	

}
