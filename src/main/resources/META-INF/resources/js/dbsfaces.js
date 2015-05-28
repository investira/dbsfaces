/*Constantes*/
//                            stop   |    prevent     | prevent "same element"
//                          bubbling | default action | event handlers
//
//return false                 Yes           Yes             No
//preventDefault()             No            Yes             No
//stopPropagation()            Yes           No              No
//stopImmediatePropagation()   Yes           No              Yes

//Exemplo de load assyncrono
//  (function(d, s, id) {
//    var js, fjs = d.getElementsByTagName(s)[0];
//    if (d.getElementById(id)) return;
//    js = d.createElement(s); js.id = id;
//    js.src = "//connect.facebook.net/en_US/sdk.js";
//    fjs.parentNode.insertBefore(js, fjs);
//  }(document, 'script', 'facebook-jssdk'));
//  
var wsAnimationTime = 200;   

//var evt = (evt) ? evt : ((event) ? event : null); 
//var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 

$(document).on("keydown", function(e){
	if ((e.which == 13) && //ENTER
		(e.target.type=="text"))  {
		return false;
	} 
	if ((e.which == 8 //BACKSPACE
	  || e.which == 46) && //DELETE
		(e.target.type=="select-one" ||
		 e.target.type=="radio" ||
		 e.target.type=="submit" ||
		 e.target.type=="checkbox"))  {
			return false;
	} 
});

String.prototype.replaceAll = function(target, replacement) {
  return this.split(target).join(replacement);
};

dbsfaces = {
	CSS : {
		MODIFIER : {
			CLOSABLE : " -closable ",
			CLOSED : " -closed ",
			SELECTED : " -selected ",
			SELECTABLE : " -selectable ",
			TITLE : " -title ",
			LABEL : " -label ",
			ICON : " -icon ",
			ICONCLOSE : " -iconclose ",
			DISABLED : " -disabled "
		}
	},
	
	ID: {
		DIALOG: "dialog",
		INPUTTEXT: "inputText"
	},
	
	EVENT: {
		ON_AJAX_BEGIN: "dbs_ON_AJAX_BEGIN",
		ON_AJAX_COMPLETE: "dbs_ON_AJAX_COMPLETE",
		ON_AJAX_SUCCESS: "dbs_ON_AJAX_SUCESS",
		ON_AJAX_ERROR: "dbs_ON_AJAX_ERROR",
		ON_ROW_SELECTED: "select.dataTable"
	}	

}

var wAjaxTimeout;

dbsfaces.sound = {
	beep : function(){
		$("#dbs_beep").remove();
		$("body").append('<audio id="dbs_beep" src="sound/alert.mp3" preload autoplay/>');
//		$("body").append('<embed id="dbs_beep" src="sound/alert.mp3" showcontrols="false" hidden="true" autostart="true" loop="false" />');
	}
}

dbsfaces.ui = {
	centerVertical : function(pId){
		var xH = $(pId).outerHeight() / 2;
		$(pId).css("margin-top",  -xH + "px")
		      .css("top", "50%")
		      .css("position", "absolute");
	},

	/*Centraliza via código. Procure utilizar o centralização por CSS*/
	centerWindow : function (pMinWidth, pMinHeight, pElement){
			var xParentWidth;
			var xParentHeight;
			if (window.innerWidth<pMinWidth){
				xParentWidth = pMinWidth;
			}else{
				xParentWidth = window.innerWidth;
			}
			if (window.innerHeight<pMinHeight){
				xParentHeight = pMinHeight;
			}else{
				xParentHeight = window.innerHeight;
			}
		    xHCenter = (xParentWidth - $(pElement).outerWidth()) / 2;
		    xVCenter = (xParentHeight - $(pElement).outerHeight()) / 2;
			$(pElement).css("left", xHCenter + "px")
					   .css("top", xVCenter + "px")
					   .css("position", "fixed");
	},
	
	/*Centaliza todos os elementos que possuem a classe dbsAlignCenterThis*/
	centerWindows : function(pMinWidth, pMinHeight, pSelector){
		$(pSelector).each(function(index, value) {
			dbsfaces.util.centerWindow(pMinWidth, pMinHeight, this);
		});
	},
	
	/*Exibe a imagem de que indica que está aguardando o recebimento dos dados*/
	showLoading : function(pId, pShow){
		
		var xId = "dbs_ajaximg_" + $.trim(pId);
		//Sempre remove loading se já existir 
		if ($("#" + xId).length > 0){
	    	$("#" + xId).remove();
		}
		
		//Exibe loading
	    if (pShow){
//			console.log("showLoading:" + pId);
	        $('body').append("<span id='" + xId + "' class='-loading -large'/>");
//	    }else{
//			console.log("HIDELoading:" + pId);
	    }
	},
	
	showLoadingError : function(pId){
		var xId = "dbs_ajaximg_" + $.trim(pId);
		$("#" + xId).fadeOut(2000, function(){ 
			$("#" + xId).remove();
//			$("div .-loading").remove(); Comentado em 08/abr/15 - Ricardo: Excluid código até a certeza que código não é mais necessário.
		});
	},
	
	getRectangle : function(obj) {
	   var off = obj.offset();

	   return {
	          top: off.top,
	          left: off.left,
	          height: obj.outerHeight(),
	          width: obj.outerWidth()
	   };
	},

	isInsideRectangle : function(pX, pY, pRect) {
        if ((pX > pRect.left && pX < (pRect.left + pRect.width))
            && (pY > pRect.top && pY < (pRect.top + pRect.height))){
        	return true;
        }else{
	        return false;
        }
	},	

	isOverlappingRectagle : function(pRectA, pRectB) {
		if (dbsfaces.ui.isInsideRectangle(pRectA.left, pRectA.top, pRectB) ||
			dbsfaces.ui.isInsideRectangle(pRectA.left + pRectA.width, pRectA.top, pRectB) ||
			dbsfaces.ui.isInsideRectangle(pRectA.left, pRectA.top + pRectA.height, pRectB) ||
			dbsfaces.ui.isInsideRectangle(pRectA.left + pRectA.width, pRectA.top + pRectA.height, pRectB)){
			return true;
		}else{
			return false;
		}
	},
	
	//Captura evento ajax dbsoft
	ajaxShowLoading : function(pSelector){
//		console.log("ajaxShowLoading:" + pSelector);
		$(pSelector).off(dbsfaces.EVENT.ON_AJAX_BEGIN);
		$(pSelector).on(dbsfaces.EVENT.ON_AJAX_BEGIN, function(e){
			dbsfaces.ui.showLoading("main",true);
		});
		$(pSelector).off(dbsfaces.EVENT.ON_AJAX_COMPLETE);
		$(pSelector).on(dbsfaces.EVENT.ON_AJAX_COMPLETE, function(e){
			//Reinicia a contagem do timeout a cada complete, já que existe respostas ajax em andamento
			window.clearTimeout(wAjaxTimeout);
			wAjaxTimeout = window.setTimeout(function(e){
				dbsfaces.ui.showLoadingError("main");
			}, 1000); //Time de delay para efetuar a chamada acima(showLoadingError). A chamada será cancelada em caso de sucesso. 	
		});

		$(pSelector).off(dbsfaces.EVENT.ON_AJAX_SUCCESS);
		$(pSelector).on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
			window.clearTimeout(wAjaxTimeout); //Cancela o timeout definido no evento COMPLETE, cancelando a respectiva chamada ao showLoadingError.
			dbsfaces.ui.showLoading("main",false);
		});
	},
	//Posiciona do próximo campo dentro do container informado
	focusOnFirstInput: function(pContainer){
		//Seta foco no primeiro campo de input
		var xEle = pContainer.find("input:first, select:first, textarea:first");
		if (xEle.length != 0 ){
			xEle.get(0).focus();
		}
	},
	//Seleciona todo o texto
	selectAll: function(pObj){
		//timeout para evitar que o click desmarque o item selecionado
		setTimeout( function(){
			dbsfaces.ui.selectRange(pObj, 0, $(pObj).get(0).value.length);
		}, 1 );
	},
	//Retira a seleção de qualquer texto
	selectNone: function(pObj){
		dbsfaces.ui.selectRange(pObj, 0, 0);
	},
	//Seleciona da posição atual até o final do texto
	selectEnd: function(pObj){
		dbsfaces.ui.selectRange(pObj, $(pObj).get(0).selectionStart, $(pObj).get(0).value.length);
	},
	//Seleciona intervalo
	selectRange: function(pObj, pStart, pEnd){
		$(pObj).get(0).setSelectionRange(pStart, pEnd);
	},

	//Dispara evento click
	ajaxTriggerClick: function(e){
		if ($(e.source).length == 0){
			return;
		}
		if (e.status == "success"){
			$(e.source).trigger("click");
		}
	}
	
}


dbsfaces.util = {
	/*Verifica se elemento existe*/
	exists: function(pId){
		if(!dbsfaces.util.isEmpty(pId)){
			if ($(pId).length>0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	},

	//Retorna vazio se for nulo
	getNotNull: function(pValue, pDefaultValue){
		if(pValue == null){
			return pDefaultValue;
		}
	},

	//Verifica se � nulo ou vazio
	isEmpty: function(pValue){
		if(pValue == null){
			return true;
		}
		if(pValue == ''){
			return true;
		}
		return false;
	},
	
	/*dispara evento para o elemento informado*/
	trigger: function (pEventName, pId){
		if (dbsfaces.util.exists(pId) && 
			!dbsfaces.util.isEmpty(pEventName)){
				//Cria evento com parametro para indicar que é dbsoft
				var xEvent = $.Event(pEventName, {dbs: true});
				//Dispara evento depois de fechar 
				$(pId).trigger(xEvent);
				return xEvent;
		}else{
			alert('Parametros inválidos! pId=' + pId + ' pEventName=' + pEventName + ' dbsFaces.trigger');
			return false;
		}
	}
}



dbsfaces.util.isiOS = function(){
    return (
        //Detect iPhone
        (navigator.platform.indexOf("iPhone") != -1) ||
        //Detect iPad
        (navigator.platform.indexOf("iPad") != -1)
    );
}

dbsfaces.util.isTablet = function(){
    return dbsfaces.util.isiOS();
}

dbsfaces.util.jsid = function(pClientId){
	return pClientId.replace(/:/g,"\\:");
}

dbsfaces.date = {
	isDate:function(pYear, pMonth, pDay, pHour, pSeconds, pMilliseconds){
		pMonth = pMonth - 1;
		var xD =  new Date(pYear, pMonth, pDay, pHour, pSeconds, pMilliseconds);
		if (xD.getDate() != pDay){
			return "";
		}
		if (xD.getMonth() != pMonth){
			return "";
		}
		if (xD.getFullYear() != pYear){
			return "";
		}
		return xD;
	}
}


dbsfaces.number = {
	isNumber: function(pVal){
		return !isNaN(parseFloat(pVal)) && isFinite(pVal);
	},
	
	sizeInBytes: function(pVal){
		if (typeof(pVal) != 'undefined'){
			var xVal = pVal.trim().toUpperCase();
			var xI = xVal.indexOf("KB");
			var xN = 1024;
			if (xI == -1){
				xI = xVal.indexOf("MB");
				if (xI != -1){
					xN *= xN;
				}else{
					xI = xVal.indexOf("GB");
					if (xI != -1){
						xN *= xN;
					}else{
						xI = xVal.indexOf("TB");
						if (xI != -1){
							xN *= xN;
						}else{
							xI = xVal.indexOf("B");
							if (xI == -1){
								xI = xVal.length;
							}
							xN = 1;
						}
					}
				}
			}
			xVal = xVal.substr(0, xI);
			if (dbsfaces.number.isNumber(xVal)){
				return xVal * xN;
			}
		}
		return 0;
	},
	
	getOnlyNumber: function(pValue){
		var xValue = "";
		for (var i = 0; i < pValue.length; i++){
			if (dbsfaces.number.isNumber(pValue.charAt(i))){
				xValue = xValue + pValue.charAt(i);
			}
		}
		return xValue;
 	}
};

dbsfaces.string = {
	fromCharCode: function(pVal){
		//Ajuste para os códigos numéricos retornados pelo teclado estendido.
		return String.fromCharCode((96 <= pVal && pVal <= 105)? pVal-48 : pVal);
	}	
}

		
//Monitora evento ajax recebido e dispara evento dbsoft
dbsfaces.onajax = function(e){
	if ($(e.source).length == 0){
		return;
	}
	if (e.status == "begin"){
		$(e.source).trigger(dbsfaces.EVENT.ON_AJAX_BEGIN);
	}else if (e.status == "complete"){
		$(e.source).trigger(dbsfaces.EVENT.ON_AJAX_COMPLETE);
	}else if (e.status == "success"){
		$(e.source).trigger(dbsfaces.EVENT.ON_AJAX_SUCCESS);
	}
};

dbsfaces.onajaxerror = function(e){
	$(e.source).trigger(dbsfaces.EVENT.ON_AJAX_ERROR);
	return false;
};

$.fn.focusNextInputField = function() {
    return this.each(function() {
        var fields = $(this).parents('form:eq(0),body').find('button,input,textarea,select');
        var index = fields.index( this );
        if ( index > -1 && ( index + 1 ) < fields.length ) {
            fields.eq( index).focus();
        }
        return false;
    });
};
