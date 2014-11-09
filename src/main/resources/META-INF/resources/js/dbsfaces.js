/*Constantes*/
//                            stop   |    prevent     | prevent "same element"
//                          bubbling | default action | event handlers
//
//return false                 Yes           Yes             No
//preventDefault()             No            Yes             No
//stopPropagation()            Yes           No              No
//stopImmediatePropagation()   Yes           No              Yes

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
		ON_ROW_SELECTED: "dbs_ON_ROW_SELECTED"
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
	showLoading : function(pId, pStatus){
		var xId = "dbs_ajaximg_" + $.trim(pId);
		if ($("#" + xId).length > 0){
	    	$("#" + xId).remove();
		}
	    if (pStatus){
	        $('body').append("<span id='" + xId + "' class='dbs_loading'/>");
	    }
	},
	
	showLoadingError : function(pId){
		var xId = "dbs_ajaximg_" + $.trim(pId);
		$("#" + xId).css("background-color", "rgba(255,255,180,0.8)");
		$("#" + xId).fadeOut(2000, function(){ 
			$("#" + xId).remove();
			$("div .-loading").remove();
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
//		console.log("CAPTURE " + pSelector);
		$(pSelector).off(dbsfaces.EVENT.ON_AJAX_BEGIN + ".default");
		$(pSelector).on(dbsfaces.EVENT.ON_AJAX_BEGIN + ".default", function(e){
			dbsfaces.ui.showLoading("main",true);
		});
		$(pSelector).off(dbsfaces.EVENT.ON_AJAX_COMPLETE + ".default");
		$(pSelector).on(dbsfaces.EVENT.ON_AJAX_COMPLETE + ".default", function(e){
			//Reinicia a contagem do timeout a cada complete, já que existe respostas ajax em andamento
			window.clearTimeout(wAjaxTimeout);
			wAjaxTimeout = window.setTimeout(function(e){
				dbsfaces.ui.showLoadingError("main");
			}, 1000); //Time de delay para efetuar a chamada acima(showLoadingError). A chamada será cancelada em caso de sucesso. 	
		});

		$(pSelector).off(dbsfaces.EVENT.ON_AJAX_SUCCESS + ".default");
		$(pSelector).on(dbsfaces.EVENT.ON_AJAX_SUCCESS + ".default", function(e){
			window.clearTimeout(wAjaxTimeout); //Cancela o timeout definido no evento COMPLETE, cancelando a respectiva chamada ao showLoadingError.
			dbsfaces.ui.showLoading("main",false);
		});
	},
	
	focusOnFirstInput: function(pContainer){
		//Seta foco no primeiro campo de input
		var xEle = pContainer.find("input:first");
		//Se não achou input, procura por combobox.
		if (xEle.length == 0 ){
			xEle = pContainer.find("select:first");
			//Se não achou input, procura por botão.
			if (xEle.length == 0 ){
				xEle = pContainer.find("button:first");
			}
		}
		if (xEle.length != 0 ){
			xEle.focus();
		}
	},
	
	selectAll: function(pObj){
		//timeout para evitar que o click desmarque o item selecionado
		setTimeout( function(){
			pObj.setSelectionRange(0, pObj.value.length);
		}, 1 );
	},

	selectNone: function(pObj){
		//timeout para evitar que o click desmarque o item selecionado
		setTimeout( function(){
			pObj.setSelectionRange(0, 0);
		}, 1 );
	},
	
	ajaxTriggerChange: function(e){
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
