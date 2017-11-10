/*Constantes*/
//                            stop   |    prevent     | prevent "same element"
//                          bubbling | default action | event handlers
//
//return false                 Yes           Yes             No
//preventDefault()             No            Yes             No
//stopPropagation()            Yes           No              No
//stopImmediatePropagation()   Yes           No              Yes

//SVG dimension (Fire fox temn problemas com o uso dos atributos abaixo)(verificar compatibilidade(29/set/2016)
//.height.baseVal.value;
//.textLength.baseVal.value;
//.clientHeight


//Exemplo de load assyncrono
//  (function(d, s, id) {
//    var js, fjs = d.getElementsByTagName(s)[0];
//    if (d.getElementById(id)) return;
//    js = d.createElement(s); js.id = id;
//    js.src = "//connect.facebook.net/en_US/sdk.js";
//    fjs.parentNode.insertBefore(js, fjs);
//  }(document, 'script', 'facebook-jssdk'));
//  

//DBSFACES===========================================================

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
		ON_ROW_SELECTED: "select.dataTable",
		ON_TRANSITION_START: "webkitTransitionStart otransitionStart oTransitionStart msTransitionStart transitionstart",
		ON_TRANSITION_END: "webkitTransitionEnd otransitionEnd oTransitionEnd msTransitionEnd transitionend",
		ON_ANIMATION_END: "webkitAnimationEnd oanimationEnd msAnimationEnd animationend",
		ON_ANIMATION_INTERATION: "webkitAnimationIteration animationiteration"
	},
	
	JAVAX: {
		VIEWSTATE: "javax.faces.ViewState",
		SOURCE: "javax.faces.source",
		PARTIAL_EXECUTE: "javax.faces.partial.execute",
		PARTIAL_RENDER: "javax.faces.partial.render",
		PARTIAL_AJAX: "javax.faces.partial.ajax"
	},
	
	locale: null,
	decimalSeparator: null,
	groupSeparator: null,
	
	setLocale: function(pLocale){
		this.locale = pLocale;
		this.decimalSeparator = dbsfaces.format.getDecimalSeparator();
		this.groupSeparator = dbsfaces.format.getGroupSeparator();
	}
}

var wAjaxTimeout;

//var evt = (evt) ? evt : ((event) ? event : null); 
//var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 

//Desabilita TECLAS ESPECÍFICAS para INPUTS ESPECÍFICOS
$(document).on("keydown", function(e){
	if ((e.which == 13) && //ENTER
		(e.target.type=="text")){
		$(e.target).blur();
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

//Inicializa tamanho do fonte quando for class for responsivo(-th_responsive)
$(document).ready(function() { 
	dbsfaces.ui.initializeTheme();
//	dbsfaces.ui.initializeResponsive();
//	$(window).resize(function() {
//		dbsfaces.ui.initializeResponsive();
//	});
});



String.prototype.replaceAll = function(target, replacement) {
  return this.split(target).join(replacement);
};


dbsfaces.sound = {
	beep : function(){
		$("#dbs_beep").remove();
		$("body").append('<audio id="dbs_beep" src="sound/alert.mp3" preload autoplay/>');
//		$("body").append('<embed id="dbs_beep" src="sound/alert.mp3" showcontrols="false" hidden="true" autostart="true" loop="false" />');
	}
}

dbsfaces.svg = {
	gsvg: function(pParentElement, pX, pY, pWidth, pHeight, pStyleClass, pStyle, pAttrs){
		var xG = dbsfaces.svg.g(pParentElement, pStyleClass, pStyle, pAttrs);
		return dbsfaces.svg.svg(xG, null, null, null, null, null, null, null);
	},
	 
	g: function(pParentElement, pStyleClass, pStyle, pAttrs){
		var xG = dbsfaces.svg.createElement(pParentElement, 'g', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xG, pStyleClass, pStyle);

		return xG;
	},

	svg: function(pParentElement, pX, pY, pWidth, pHeight, pStyleClass, pStyle, pAttrs){
		var xSVG = dbsfaces.svg.createElement(pParentElement, 'svg', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xSVG, pStyleClass, pStyle);

		xSVG.svgAttr("x", pX)
			.svgAttr("y", pY)
			.svgAttr("width", pWidth)
			.svgAttr("height", pHeight);

		return xSVG;
	},
	
	use: function(pParentElement, pHRef, pStyleClass, pStyle, pAttrs){
		var xUse = dbsfaces.svg.createElement(pParentElement, 'use', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xUse, pStyleClass, pStyle);
		dbsfaces.svg.setAttributeHRef(xUse, pHRef);
//		xUse.get(0).setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + pHRef);
//		xUse.svgAttr("xlink:href", "#" + pHRef);
		return xUse;
	},

	line: function(pParentElement, pX1, pY1, pX2, pY2, pStyleClass, pStyle, pAttrs){
		var xLine = dbsfaces.svg.createElement(pParentElement, 'line', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xLine, pStyleClass, pStyle);
		xLine.svgAttr("x1", pX1)
			 .svgAttr("y1", pY1)
			 .svgAttr("x2", pX2)
			 .svgAttr("y2", pY2);

		return xLine;
	},
	
	circle: function(pParentElement, pCX, pCY, pR, pStyleClass, pStyle, pAttrs){
		var xCircle = dbsfaces.svg.createElement(pParentElement, 'circle', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xCircle, pStyleClass, pStyle);

		xCircle.svgAttr("cx", pCX)
			   .svgAttr("cy", pCY)
			   .svgAttr("r", pR);

		return xCircle;
	},

	rect: function(pParentElement, pX, pY, pWidth, pHeight, pRX, pRY, pStyleClass, pStyle, pAttrs){
		var xRect = dbsfaces.svg.createElement(pParentElement, 'rect', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xRect, pStyleClass, pStyle);

		xRect.svgAttr("x", pX)
			 .svgAttr("y", pY)
			 .svgAttr("rx", pRX)
			 .svgAttr("ry", pRY)
			 .svgAttr("height", pHeight)
			 .svgAttr("width", pWidth);
		
		return xRect;
	},
	
	ellipse: function(pParentElement, pCX, pCY, pRX, pRY, pStyleClass, pStyle, pAttrs){
		var xEllipse = dbsfaces.svg.createElement(pParentElement, 'ellipse', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xEllipse, pStyleClass, pStyle);

		xEllipse.svgAttr("cx", pCX)
			    .svgAttr("cy", pCY)
			    .svgAttr("rx", pRX)
			    .svgAttr("ry", pRY);

		return xEllipse;
	},
	
	path: function(pParentElement, pData, pStyleClass, pStyle, pAttrs){
		var xPath = dbsfaces.svg.createElement(pParentElement, 'path', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xPath, pStyleClass, pStyle);
		
		xPath.svgAttr("d", pData);
		
		return xPath;
	},

	text: function(pParentElement, pX, pY, pText, pStyleClass, pStyle, pAttrs){
		var xText = dbsfaces.svg.createElement(pParentElement, 'text', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xText, pStyleClass, pStyle);

		xText.svgAttr("x", pX)
			 .svgAttr("y", pY);
		xText.text(pText);

		return xText;
	},

	tspan: function(pParentElement, pText, pStyleClass, pStyle, pAttrs){
		var xTspan = dbsfaces.svg.createElement(pParentElement, 'tspan', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xTspan, pStyleClass, pStyle);

		xTspan.text(pText);

		return xTspan;
	},

	textPath: function(pParentElement, pHRef, pText, pStyleClass, pStyle, pAttrs){
		var xTextPath = dbsfaces.svg.createElement(pParentElement, 'textPath', pAttrs);
		dbsfaces.svg.setDefaultAttrs(xTextPath, pStyleClass, pStyle);

		xTextPath.text(pText);
		dbsfaces.svg.setAttributeHRef(xTextPath, pHRef);
		
		return xTextPath;
	},
	
	linearGradient: function(pParentElement, pId, pAttrs){
		var xElement = dbsfaces.svg.createElement(pParentElement, 'linearGradient', pAttrs);

		xElement.svgAttr("id", pId);
		
		return xElement;
	},
	
	marker: function(pParentElement, pId, pRefX, pRefY){
		var xElement = dbsfaces.svg.createElement(pParentElement, 'marker', null);

		xElement.svgAttr("id", pId)
				.svgAttr("refx", pRefX)
				.svgAttr("refy", pRefY);

		return xElement;
	},

	stop: function(pParentElement, pOffset, pStopColor){
		var xElement = dbsfaces.svg.createElement(pParentElement, 'stop', null);

		xElement.svgAttr("offset", pOffset)
			    .svgAttr("stop-color", pStopColor);

		return xElement;
	},
	
	createElement: function(pParentElement, pTag, pAttrs){
		var xElement = $(document.createElementNS('http://www.w3.org/2000/svg', pTag));
		//Seta atributos do componente.
		if (pAttrs != null){
			for (var xAttr in pAttrs){
				xElement.svgAttr(xAttr, pAttrs[xAttr]);
			}
		}
		pParentElement.append(xElement);
		return xElement;
	},

	setDefaultAttrs: function(pComponent, pStyleClass, pStyle){
		pComponent.svgAttr("class", pStyleClass)
				  .svgAttr("style", pStyle);
	},
	
	setAttributeHRef: function(pComponent, pHRef){
		var xNS = "http://www.w3.org/1999/xlink";
		pComponent.get(0).setAttributeNS(xNS, "xlink:href", "#" + pHRef);
	}
	
}

dbsfaces.url = {
	//Retorna os parametros da Url em um objeto
	getParams : function(){
		var xQueryString = {};
		var xQuery = window.location.search.substring(1);
		var xVars = xQuery.split("&");
		for (var i=0;i<xVars.length;i++) {
			var xPair = xVars[i].split("=");
			var xParam = xPair[0].toLowerCase();
		    // If first entry with this name
			if (typeof xQueryString[xParam] === "undefined") {
			  xQueryString[xParam] = decodeURIComponent(xPair[1]);
			    // If second entry with this name
			} else if (typeof xQueryString[xParam] === "string") {
			  var xArr = [ xQueryString[xParam],decodeURIComponent(xPair[1]) ];
			  xQueryString[xParam] = xArr;
			    // If third or later entry with this name
			} else {
			  xQueryString[xParam].push(decodeURIComponent(xPair[1]));
			}
		}
		return xQueryString;
	},
	
	//Copia os valore dos parametros da URL para os campos quando o id for iqual ao nome do parametro.
	setInputsValuesFromParams:function(){
		var wParams = dbsfaces.url.getParams();
		for (var xProperty in wParams) {
		    if (wParams.hasOwnProperty(xProperty)){
		    	var xInput = document.getElementById(xProperty);
		    	if (xInput != null){
		    		xInput.value = wParams[xProperty];
		    	}
		    }
		}
	}
}


dbsfaces.onView = {
	initialize: function(pElement){
		pElement.addClass("-th_onview");
		if ($("body").data("onview") == null){
			$(window).resize(function(e){
				dbsfaces.onView.event();
			});
			$(window).scroll(function(e){
				dbsfaces.onView.event();
			});
			pElement.scroll(function(e){
				dbsfaces.onView.event();
			});
		}
		$("body").data("onview", $(".-th_onview"));
	},
	
	/*Dispara eventos viewEnter, viewExit, viewScroll, viewTopIn, viewTopOut.*/
	event: function(){
		var xWindow = $(window);
		$("body").data("onview").each(function(){
			var xThis = $(this);
			if (xThis.length > 0){
				var xTotalTop = xThis[0].getBoundingClientRect().top + xThis[0].getBoundingClientRect().height;
				if (xTotalTop < 0 
				 || xThis[0].getBoundingClientRect().top >= xWindow.height()){
					if (xThis.data("onview") != null){
//						console.log("viewExit\t" + this.id);
						clearTimeout(xThis.data("onview"));
						xThis.data("onview", null);
						xThis.data("onviewTopIn", null);
						xThis.data("onviewTopOut", null);
						xThis.removeClass("-enter");
						xThis.trigger("viewExit");
					}
				}else{
					if (xThis.data("onview") == null){
//						console.log("viewEnter\t" + this.id);
						xThis.data("onview", true);
						xThis.addClass("-enter");
						xThis.trigger("viewEnter");
					}else{
//						console.log("viewScroll\t" + this.id);
						/*uso do setTimeout para evitar muitas chamadas consecutivas*/
						clearTimeout(xThis.data("onview"));
						xThis.data("onview", 
							setTimeout(function(e){
								if (xThis.data("onviewTopIn") == null 
								 && xThis[0].getBoundingClientRect().top > 0){
									xThis.data("onviewTopIn" , true);
									xThis.trigger("viewTopIn");
								}else if (xThis.data("onviewTopOut") == null 
								       && xThis[0].getBoundingClientRect().top < 0){
									xThis.data("onviewTopOut" , true);
									xThis.trigger("viewTopOut");
								}
									
								var xFactor = 1 - (xTotalTop / (xWindow.height() + xThis[0].getBoundingClientRect().height));
								var xFactorTop = 1 - (xThis[0].getBoundingClientRect().top / xWindow.height());
								var xEvent = $.Event("viewScroll", {dbs: true, source: xThis, factor:xFactor, factorTop:xFactorTop});
								xThis.trigger(xEvent);
							},10)
						);
						
					}
				}
			}
		});
	}
}

dbsfaces.component = {
	//Utilizado pelos componentes DBSUICommand para setar a existencia de mensagem
	setHasMessage: function(pId){
		var xComponent = $(pId);
		xComponent.data("hasmessage" , true);
		xComponent.attr("hasmessage" , true);
	},
	
	createElement: function(pTag, pAttrs){
		var xElement = $(document.createElement(pTag));
		//Seta atributos do componente.
		if (pAttrs != null){
			for (var xAttr in pAttrs){
				xElement.attr(xAttr, pAttrs[xAttr]);
			}
		}
		return xElement;
	}

}
dbsfaces.ui = {

	initializeTheme: function(){
		dbsfaces.ui.initializeThemeInput();
		dbsfaces.ui.initializeThemeCard();
	},
	
	initializeThemeInput: function(){
		var xColor = tinycolor($("body").css("color"));
		var xStyles = "";
		/*cor do fundo quando selecionado*/
		xColor.setAlpha(.05);
		xStyles += ".-th_input-data:FOCUS,.-th_input-data-FOCUS {background-color: "; 
		xStyles += xColor.toHslString();
		xStyles += ";} ";
		/*cor da borda*/
		xColor.setAlpha(.08);
		xStyles += ".-th_input-data[type=text],";
		xStyles += ".-th_input-data[type=password], ";
		xStyles += ".-th_input-data[type=email], ";
		xStyles += ".-th_input-data[type=search], ";
		xStyles += ".-th_input-data[type=tel], ";
		xStyles += ".-th_input-data[type=number], ";
		xStyles += ".-th_input-data[type=date], ";
		xStyles += ".-th_input-data[type=week], ";
		xStyles += ".-th_input-data[type=time], ";
		xStyles += ".-th_input-data[type=url], ";
		xStyles += "select.-th_input-data,  ";
		xStyles += "textarea.-th_input-data, ";
		xStyles += "span.-th_input-data, ";
		xStyles += "div.-th_input-data, ";
		xStyles += "label.-th_input-data, ";
		xStyles += ".-th_input-suggestion{ ";
		xStyles += "border: 1px ";
		xStyles += xColor.toHslString();
		xStyles += " solid;";
		xStyles += "}";
		
		var xStyleTag = document.createElement('style');
		 if (xStyleTag.styleSheet){
		     xStyleTag.styleSheet.cssText= Styles;
		 }else{
		     xStyleTag.appendChild(document.createTextNode(xStyles));
		 }
		 document.getElementsByTagName('head')[0].appendChild(xStyleTag);
	},
	
	initializeThemeCard: function(){
	},
	
	moveToFront: function(pElement, pMoveToElement){
		if (pElement == null || (typeof(pElement) == "undefined")){return;}
		var xE = pElement;
		var xToE;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		if (pMoveToElement == null){
			xToE = xE.parentElement; 
		}else{
			if (pMoveToElement instanceof jQuery){
				xToE = pMoveToElement.get(0);
			}else{
				xToE = pMoveToElement;
			}
		}
		if (xToE != null){
			xToE.appendChild(xE);
		}
	},
	
	moveToBack: function(pElement){
		if (pElement == null || (typeof(pElement) == "undefined")){return;}
		var xE = pElement;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		xE.parentElement.insertBefore(xE, xE.parentElement.childNodes[0]);
	},
	
	recreate: function(pElement){
		if (pElement == null || (typeof(pElement) == "undefined")){return;}
		var xE = pElement;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		var xI = xE.childElementCount;
		if (xI > 0){
//			xI--;
		}
		xE.parentElement.insertBefore(xE, xE.parentElement.childNodes[xI]);
	},
	
	getRectangle : function(obj) {
		var xE = obj;
		if (!(obj instanceof jQuery)){
			xE = $(obj);
		}
	   var off = xE.offset();
	
	   return {
	          top: off.top,
	          left: off.left,
	          height: xE.outerHeight(),
	          width: xE.outerWidth()
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
	
	//Posiciona do próximo campo dentro do container informado
	focusOnFirstInput: function(pContainer){
		//Seta foco no primeiro campo de input
		if (!(pContainer instanceof jQuery)){
			xE = $(pContainer);
		}
		$(document.activeElement).blur();
		var xEle = pContainer.find("input:first, select:first, textarea:first");
		if (xEle.length != 0 ){
			xEle[0].focus();
		}
	},
	//Seleciona todo o texto
	selectAll: function(pObj){
		var xE = pObj;
		if (pObj instanceof jQuery){
			xE = pObj[0];
		}
		xE.select();
//		//timeout para evitar que o click desmarque o item selecionado
//		setTimeout( function(){
//			dbsfaces.ui.selectRange(xE, 0, xE.value.length);
//		}, 1 );
	},
	//Retira a seleção de qualquer texto
	selectNone: function(pObj){
		dbsfaces.ui.selectRange(pObj, 0, 0);
	},
	//Seleciona da posição atual até o final do texto
	selectEnd: function(pObj){
		var xE = pObj;
		if (pObj instanceof jQuery){
			xE = pObj[0];
		}
		if (xE.value != null){
			dbsfaces.ui.selectRange(xE, xE.selectionStart, xE.value.length);
		}
	},
	//Seleciona intervalo
	selectRange: function(pObj, pStart, pEnd){
		var xE = pObj;
		if (pObj instanceof jQuery){
			xE = pObj[0];
		}
		if ($(xE).attr("type") == "email"){return;}
		if (xE.__proto__.hasOwnProperty("selectionStart")){
			xE.setSelectionRange(pStart, pEnd);
		}
	},
	
//	function insertTextAtCursor(text) {
//	    var sel, range, textNode;
//	    if (window.getSelection) {
//	        sel = window.getSelection();
//	        if (sel.getRangeAt && sel.rangeCount) {
//	            range = sel.getRangeAt(0).cloneRange();
//	            range.deleteContents();
//	            textNode = document.createTextNode(text);
//	            range.insertNode(textNode);
//
//	            // Move caret to the end of the newly inserted text node
//	            range.setStart(textNode, textNode.length);
//	            range.setEnd(textNode, textNode.length);
//	            sel.removeAllRanges();
//	            sel.addRange(range);
//	        }
//	    } else if (document.selection && document.selection.createRange) {
//	        range = document.selection.createRange();
//	        range.pasteHTML(text);
//	    }
//	}
	
	disableBackgroundInputs: function(pSourceId){
		var xE = pSourceId;
		if (!(pSourceId instanceof jQuery)){
			xE = $(pSourceId);
		}
		var xId = dbsfaces.util.jsid(xE[0].id);
//		xOP.find(".dbs_dialog").not(xId).attr('disabled', true);
	    $(".-th_action").not(xId + " .-th_action").attr('disabled', true);
		$(".dbs_menu").not(xId + " .dbs_menu").attr('disabled', true);
		$("input").not('[type="hidden"]').not(xId + " input").attr('disabled', true);
		$("button").not(xId + " button").attr('disabled', true);
		$("select").not(xId + " select").attr('disabled', true);
		$(".dbs_nav").not(xId + " nav").attr('disabled', true);
		$("a").not(xId + " a").attr('disabled', true);
		$("textarea").not(xId + " textarea").attr('disabled', true);

		dbsfaces.ui.enableForegroundInputs(xE);
	},
	
	enableForegroundInputs: function(pSourceId){
		var xE = pSourceId;
		if (!(pSourceId instanceof jQuery)){
			xE = $(pSourceId);
		}
		xE.find(".-th_action").not(".-disabled").attr('disabled', null);
		xE.find(".dbs_menu").not(".-disabled").attr('disabled', null);
//		xE.find(".dbs_dialog").not(".-disabled").attr('disabled', true);
		xE.find("input").not(".-disabled").attr('disabled', null);
		xE.find("a").not(".-disabled").attr('disabled', null);
		xE.find("button").not(".-disabled").attr('disabled', null);
		xE.find("select").not(".-disabled").attr('disabled', null);
		xE.find(".dbs_nav").not(".-disabled").attr('disabled', null);
		xE.find("textarea").not(".-disabled").attr('disabled', null);
	},

	cssAllBrowser: function(e, pAtribute, pValue){
		var xE = e;
		if (!(xE instanceof jQuery)){
			xE = $(e);
		}
		pAtribute = pAtribute.trim();
		pValue = pValue.trim();
		xE.css("-webkit-" + pAtribute, pValue)
		  .css("-moz-" + pAtribute, pValue)
		  .css("-ms-" + pAtribute, pValue)
		  .css("-o-" + pAtribute, pValue)
		  .css(pAtribute, pValue);
	},

	cssFilterDropShadow: function(e, pValue){
		var xValue = "drop-shadow(" + pValue + ")";
		dbsfaces.ui.cssFilter(e, xValue);
	},
	
	cssFilterBlur: function(e, pValue){
		var xValue = "blur(" + pValue + "px)";
		dbsfaces.ui.cssFilter(e, xValue);
	},

	cssFilterOpacity: function(e, pValue){
		var xValue = "opacity(" + pValue + ")";
		dbsfaces.ui.cssFilter(e, xValue);
	},

	cssFilter: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "filter", pValue);
	},
	
	cssTransform: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "transform", pValue);
	},
	
	cssTransition: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "transition", pValue);
	},

	cssAnimation: function(e, pValue){
		dbsfaces.ui.cssAllBrowser(e, "animation", pValue);
	},

	//Retorna valores do transform
	getTransform:function(e){
		var xE = e;
		if (xE instanceof jQuery){
			xE = $(e).get(0);
		}
		var xSt = window.getComputedStyle(xE, null);
		var xTr = xSt.getPropertyValue("-webkit-transform") ||
		          xSt.getPropertyValue("-moz-transform") ||
		          xSt.getPropertyValue("-ms-transform") ||
		          xSt.getPropertyValue("-o-transform") ||
		          xSt.getPropertyValue("transform") ||
		          "erro";
		return xTr;
	},
	//Retorna valores do Z definido no transform
	getTransformZ: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null
		 || xTr == ""){
			return 0;
		}else{
			xTr = xTr.slice(4,5);
			if (xTr == ""){
				xTr = 0;
			}
		}
		return parseFloat(xTr);
	},
	//Retorna valores do X definido no transform
	getTransformX: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null
		 || xTr == ""){
			return 0;
		}else{
			xTr = xTr.slice(2,3);
			if (xTr == ""){
				xTr = 0;
			}
		}
		return parseFloat(xTr);
	},
	//Retorna valores do Y definido no transform
	getTransformY: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null
		 || xTr == ""){
			return 0;
		}else{
			xTr = xTr.slice(3,4);
			if (xTr == ""){
				xTr = 0;
			}
		}
		return parseFloat(xTr);
	},

	getTransformMatrix3d: function(e){
		var xTr = this.getTransformMatrix(e);
		if (xTr == null){
			return "";
		}else{
			return xTr.slice(2,5);
		}
	},
	getTransformMatrix: function(e){
		var xTr = dbsfaces.ui.getTransform(e);
		xTr = xTr.match(/matrix(?:(3d)\(-{0,1}\d+\.?\d*(?:, -{0,1}\d+\.?\d*)*(?:, (-{0,1}\d+\.?\d*))(?:, (-{0,1}\d+\.?\d*))(?:, (-{0,1}\d+\.?\d*)), -{0,1}\d+\.?\d*\)|\(-{0,1}\d+\.?\d*(?:, -{0,1}\d+\.?\d*)*(?:, (-{0,1}\d+\.?\d*))(?:, (-{0,1}\d+\.?\d*))\))/);
		return xTr;
	},
	
	//	Retorna fator mínimo para ajuste de tamanho conforme largura e altura da tela atual em relação a tela desejada
	aspectRatio: function(pWindow, pBaseWidth, pBaseHeight){
		var xBaseRatio = pBaseWidth / pBaseHeight;
		var xWidth = pWindow.width();
		var xHeight = pWindow.height();
		var xUseWidth = ((xWidth / xHeight) < xBaseRatio); 
		var xRatio;
		if (xUseWidth){
			xRatio = xWidth / pBaseWidth;
		}else{
			xRatio = xHeight / pBaseHeight;
		}
//		console.log(xRatio + "\t" + xWidth + "\t" + xHeight + "\t" +  xBaseRatio + "\t" + pBaseWidth + "\t" + pBaseHeight);
		return xRatio;
	},

	getAllEvents: function(e) {
	    var xResult = [];
	    for (var xKey in e) {
	        if (xKey.indexOf('on') === 0) {
	        	xResult.push(xKey.slice(2));
	        }
	    }
	    return xResult.join(' ');
	},
	
	recreateElement: function(e){
		var xE = e;
		if (!(e instanceof jQuery)){
			xE = $(e);
		}
	    var xNew = xE.clone(true);
	    xE.before(xNew);       
	    xE.remove();
	    return xNew;
	},
	
	getTimeFromTextLength: function(pText){
		var xTime = pText.length;
		xTime = (xTime / 2) * 200;
		return xTime;
	},
	
	//	Verificar se elemento esta contido dentro de algum elemento com position:fixed
	pvIsParentFixed: function(pElement){
		var xE = pElement;
		if (xE instanceof jQuery){
			xE = pElement.get(0);
		}
		var xParent = xE.parentElement;
		if (xParent == null){
			return false;
		}
		var xStyle = window.getComputedStyle(xParent);
	    var xPosition = xStyle.getPropertyValue("position");
		if (xPosition == "fixed"){
			return true;
		}else{
			return dbsfaces.ui.pvIsParentFixed(xParent);
		}
	},
	
	//Dispara evento click
	ajaxTriggerClick: function(e){
		if ($(e.source).length == 0){
			return;
		}
		if (e.status == "success"){
			$(e.source).trigger("click");
		}
	},
	
	ajaxTriggerLoaded: function(e){
		if ($(e.source).length == 0){
			return;
		}
		if (e.status == "success"){
			$(e.source).trigger("loaded");
		}
	},	

	//Captura evento ajax dbsoft
	ajaxShowLoading : function(pSelector){
//		console.log("ajaxShowLoading:" + pSelector);
		var xEle = pSelector;
		if (!(xEle instanceof jQuery)){
			xEle = $(pSelector);
		}
		xEle.off(dbsfaces.EVENT.ON_AJAX_BEGIN);
		xEle.on(dbsfaces.EVENT.ON_AJAX_BEGIN, function(e){
			dbsfaces.ui.showLoading("main",true);
		});
		xEle.off(dbsfaces.EVENT.ON_AJAX_COMPLETE);
		xEle.on(dbsfaces.EVENT.ON_AJAX_COMPLETE, function(e){
			//Reinicia a contagem do timeout a cada complete, já que existe respostas ajax em andamento
			window.clearTimeout(wAjaxTimeout);
			wAjaxTimeout = window.setTimeout(function(e){
				dbsfaces.ui.showLoadingError("main");
			}, 1000); //Time de delay para efetuar a chamada acima(showLoadingError). A chamada será cancelada em caso de sucesso. 	
		});

		xEle.off(dbsfaces.EVENT.ON_AJAX_SUCCESS);
		xEle.on(dbsfaces.EVENT.ON_AJAX_SUCCESS, function(e){
			window.clearTimeout(wAjaxTimeout); //Cancela o timeout definido no evento COMPLETE, cancelando a respectiva chamada ao showLoadingError.
			dbsfaces.ui.showLoading("main",false);
		});
	},
	
	/*Exibe a imagem de que indica que está aguardando o recebimento dos dados*/
	showLoading : function(pId, pShow){
		var xId = dbsfaces.util.jsid(pId + "_loading");
		//Sempre remove loading se já existir 
		
		//Exibe loading
	    if (pShow){
			if ($(xId).length == 0){
				$('body').append("<span id='" + xId.replace("#","") + "' class='-loading -large'/>");
			}
	    }else{
			if ($(xId).length > 0){
		    	$(xId).remove();
			}
	    }
	},
	
	showLoadingError : function(pId){
		var xId = dbsfaces.util.jsid(pId + "_loading");
		$(xId).fadeOut(2000, function(){ 
			$(xId).remove();
			$(pId).removeClass("-ajaxBegin");
//			$("div .-loading").remove(); Comentado em 08/abr/15 - Ricardo: Excluid código até a certeza que código não é mais necessário.
		});
	},
	
	pointerEventToXY : function(e){
		var xXY = {x:0, y:0};
        if(e.type == 'touchstart' || e.type == 'touchmove' || e.type == 'touchend' || e.type == 'touchcancel'){
			var xTouch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
			xXY.x = xTouch.pageX - $(window).scrollLeft(); //Incluido em - 18-sep-17 - Ainda não testado
			xXY.y = xTouch.pageY - $(window).scrollTop(); //Incluido por posicionamento errado quando há scroll - 30-jun-17
        } else if (e.type == 'mousedown' || e.type == 'mouseup' || e.type == 'mousemove' || e.type == 'mouseover'|| e.type=='mouseout' || e.type=='mouseenter' || e.type=='mouseleave') {
//			xXY.x = e.offsetX;
//			xXY.y = e.offsetY;
			xXY.x = e.clientX;
			xXY.y = e.clientY;
        }
        return xXY;
     },
     
   	getRect : function(pSelector){
		var xRect = {width:0, height:0, top:0, bottom:0, left:0, right:0, x:0, y:0};
 		var xEle = pSelector;
		if (xEle instanceof jQuery){
			xEle = pSelector[0];
		}
		var xBCR = xEle.getBoundingClientRect();
		xRect.width = xBCR.width;
		xRect.height = xBCR.height;
		xRect.top = xBCR.top - $(window).scrollTop();
		xRect.bottom = xBCR.bottom;
		xRect.left = xBCR.left - $(window).scrollLeft();
		xRect.right = xBCR.right;
		xRect.x = xBCR.x;
		xRect.y = xBCR.y;
        return xRect;
     },
     
     getPosition: function(pSelector) { // crossbrowser version
 		var xEle = pSelector;
		if (xEle instanceof jQuery){
			xEle = pSelector[0];
		}
	    var xBox = xEle.getBoundingClientRect();

	    var xBody = document.body;
	    var xDocEl = document.documentElement;

	    var xScrollTop = window.pageYOffset || xDocEl.scrollTop || xBody.scrollTop;
	    var xScrollLeft = window.pageXOffset || xDocEl.scrollLeft || xBody.scrollLeft;

	    var xClientTop = xDocEl.clientTop || xBody.clientTop || 0;
	    var xClientLeft = xDocEl.clientLeft || xBody.clientLeft || 0;

	    var xTop  = xBox.top +  xScrollTop - xClientTop;
	    var xLeft = xBox.left + xScrollLeft - xClientLeft;

	    return { top: Math.round(xTop), left: Math.round(xLeft) };
    }

}

//Exibe janela ajustando a localização de form a não ultrapassar os limites da janela principal


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
		return pValue;
	},

	//Retorna vazio se for nulo
	getNotEmpty: function(pValue, pDefaultValue){
		if(dbsfaces.util.isEmpty(pValue)){
			return pDefaultValue;
		}
		return pValue;
	},
	//Retorna vazio se for nulo
	isUndefined: function(pValue){
		if(typeof(pValue) == "undefined"){
			return true;
		}
		return false;
	},

	//Retorna vazio se for nulo
	getNotUndefined: function(pValue, pDefaultValue){
		if(dbsfaces.util.isUndefined(pValue)){
			return pDefaultValue;
		}
		return pValue;
	},
	//Verifica se é nulo ou vazio
	isEmpty: function(pValue){
		if(pValue == null){
			return true;
		}
		if(pValue == ''){
			return true;
		}
		if(dbsfaces.util.isUndefined(pValue)){
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
	},
	
	isiOS: function(){
		var xNav = navigator.userAgent.toLowerCase();
		if(xNav.match(/iphone/i) || 
		   xNav.match(/ipod/i) ||
		   xNav.match(/ipad/i)) {
			return true;
		};
	    return false;
	},
	
	isAndroid: function(){
		var xNav = navigator.userAgent.toLowerCase();
		if(xNav.match(/android/i)) {
			return true;
		};
	    return false;
	},

	isBlackBerry: function(){
		var xNav = navigator.userAgent.toLowerCase();
		if(xNav.match(/blackberry/i)) {
			return true;
		};
	    return false;
	},

	isMobile: function(){
	    if (this.isiOS()
	     || this.isBlackBerry()
	     || this.isAndroid()){
	    	return true;
	    }
	    return false;
	},
	
	supports: function(pProp){
	   var div = document.createElement('div'),
	      vendors = 'Khtml Ms O Moz Webkit'.split(' '),
	      len = vendors.length;
	 
      if ( pProp in div.style ) return true;
 
      pProp = pProp.replace(/^[a-z]/, function(val) {
         return val.toUpperCase();
      });
 
      while(len--) {
         if ( vendors[len] + pProp in div.style ) {
            // browser supports box-shadow. Do what you need.
            // Or use a bang (!) to test if the browser doesn't.
            return true;
         } 
      }
      return false;
	}
}


dbsfaces.util.jsid = function(pClientId){
	if (pClientId == null
	 || typeof(pClientId) == "undefined"){
		return "";
	}
	//Retira dois pontos(:) do inicio
	pClientId = pClientId.trim().replace(/^(:)/,"");
	//Retira dois pontos(:) em escaped se houver
	pClientId = pClientId.replace(/\\:/g,":");
	//Convert os outros dois pontos(:)  em escaped caracter
	pClientId = pClientId.replace(/:/g,"\\:");
	//Incluir o "#" se não houver
	if (pClientId.length > 0
	 && pClientId.charAt(0) != "#"){
		pClientId = "#" + pClientId;
	}
	return pClientId;
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

dbsfaces.math = {
	PICircle: Math.PI * 2,
	PICircleFactor: (Math.PI * 2) / 100,

	round: function(pValue, pDecimals){
		if (isNaN(pValue)){return;}
		var xP = Math.pow(10, pDecimals);
		var xValue = Number(Math.round(pValue * xP).toPrecision(16)); //Precision para evitar ERRO ex: 123459 * 100 = 123458.99999999999
		xValue = Math[xValue < 0 ? 'ceil' : 'floor'](xValue);
		return xValue / xP;
	},
	
	trunc: function(pValue, pDecimals){
		if (isNaN(pValue)){return;}
		var xP = Math.pow(10, pDecimals);
		var xValue = Number((pValue * xP).toPrecision(16)); //Precision para evitar ERRO ex: 123459 * 100 = 123458.99999999999
		xValue = Math[xValue < 0 ? 'ceil' : 'floor'](xValue);
		return xValue / xP;
	},
	
	//Encontra coordenada x,y a partir do centro, do raio e angulo
	circlePointAngle: function(pCenter, pRadius, pAngle){
		if (isNaN(pAngle)){return;}
		if (isNaN(pRadius)){return;}
		if (pAngle == null){return;}
		pAngle = pAngle % 360;
		if (pAngle < 0){
			pAngle = 360 + pAngle;
		}
		var xPIPercentual = dbsfaces.math.PICircle * (pAngle / 360);
		return dbsfaces.math.circlePoint(pCenter, pRadius, xPIPercentual);
	},
	
	//Encontra coordenada x,y a partir do centro, do raio e percentual de PI(posição ao longo do arco)
	circlePoint: function(pCenter, pRadius, p2PIPercentual){
		var xPoint = {};
		xPoint.x = dbsfaces.math.round(pCenter.x + (pRadius * Math.sin(p2PIPercentual)), 6);
		xPoint.y = dbsfaces.math.round(pCenter.y - (pRadius * Math.cos(p2PIPercentual)), 6);
		return xPoint;
	},
	
	angleFromTreePoints: function(pP1, pP2, pVertex){
		var xV1 = Math.sqrt(Math.pow(pVertex.x - pP1.x,2) +
							Math.pow(pVertex.y - pP1.y, 2)); // P1 -> Vertex (b)   
		var xV2 = Math.sqrt(Math.pow(pVertex.x - pP2.x, 2) +
		                    Math.pow(pVertex.y - pP2.y, 2)); // p2 -> Vertex
		var xVV = Math.sqrt(Math.pow(pP2.x - pP1.x, 2) +
		                     Math.pow(pP2.y - pP1.y, 2)); // p1 > p2 
		if (xV1 == 0 && xV2 == 0 && xVV == 0){
			return 0;
		}
		return Math.acos(((xV2*xV2) + (xV1*xV1) - (xVV * xVV))/(2 * xV2 * xV1)) * (180 / Math.PI);
		
	},
	
	//pH=Hipotenuza; pC:cateto oposto
	rightTriangleVertexAngle: function(pHip, pCatOp){
		return Math.asin(pCatOp/pHip)*(180/Math.PI);
	},
	
	circleLength: function(pRadius, pAngle){
		return dbsfaces.math.PICircle * pRadius * (pAngle / 360);
	},
	
	circleLineIntersection: function(pCircleCenter, pCircleRadius, pLinePoint1, pLinePoint2){
		var xI1 = {x:0, y:0}; //Ponto inicio da linha
		var xI2 = {x:0, y:0}; //Ponto fim da linha

		var xLAB = Math.sqrt(
				    Math.pow(pLinePoint2.x - pLinePoint1.x, 2) + 
				    Math.pow(pLinePoint2.y - pLinePoint1.y, 2));

		// compute the direction vector D from A to B
		var xD = {x:(pLinePoint2.x - pLinePoint1.x) / xLAB,
				  y:(pLinePoint2.y - pLinePoint1.y) / xLAB};

		// compute the value t of the closest point to the circle center (Cx, Cy)
		var xT = xD.x * (pCircleCenter.x - pLinePoint1.x) + xD.y * (pCircleCenter.y - pLinePoint1.y);    

		// compute the coordinates of the point E on line and closest to C
		var xE = {x:xT * xD.x + pLinePoint1.x,
				  y:xT * xD.y + pLinePoint1.y};

		// compute the euclidean distance from E to C
		var xLEC = Math.sqrt(
					Math.pow(xE.x - pCircleCenter.x, 2) + 
					Math.pow(xE.y - pCircleCenter.y, 2));

		// test if the line intersects the circle
		if (xLEC < pCircleRadius){
		    // compute distance from t to circle intersection point
		    var xDt = Math.sqrt(Math.pow(pCircleRadius, 2) - Math.pow(xLEC, 2));

		    // compute first intersection point
		    xI1.x = (xT-xDt) * xD.x + pLinePoint1.x;
		    xI1.y = (xT-xDt) * xD.y + pLinePoint1.y;

		    // compute second intersection point
		    xI2.x = (xT+xDt) * xD.x + pLinePoint1.x;
		    xI2.y = (xT+xDt) * xD.y + pLinePoint1.y;
			return {point1:xI1, point2:xI2};
		//tangente
		}else if(xLEC == pCircleRadius){
			return {point1:xE, point2:xE};
		}else{
			return null;
		}
	},
	//Retorna lista com o valores binários(1,2,4,8) que compõem o valor informado
	//Bitwise operators treat their operands as a sequence of 32 bits (zeros and ones)
	getBits: function(pBinaryNumber){
		var xBitCount = 0;
		var xBits = [];
		var xTestNumber = pBinaryNumber;
		var xBinaryNumber = pBinaryNumber;
		while (xBinaryNumber > 0){
			//Testa sempre o primeiro bit(já shiftado). 
			if (xTestNumber & 1){
				var xBit = Math.pow(2, xBitCount);
				xBinaryNumber -= xBit;
				xBits.push(xBit);
			}
			xBitCount++; 
			//Shifta. Exclui os bits já testado do vaor original
			xTestNumber = pBinaryNumber >> xBitCount;
		}
		return xBits;
	},
	
	distanceBetweenTwoPoints: function(pX1, pY1, pX2, pY2){
		var xA;
		var xB;
		if (pX2 == null){
			xA = pX1.x - pY1.x;
			xB = pX1.y - pY1.y;
		}else{
			xA = pX1 - pX2
			xB = pY1 - pY2
		}
		return Math.sqrt(xA*xA + xB*xB);
	},
	
	sign: function(pValue){
		//Math.sign não funciona em browsers antigos
		if (pValue > 0){
			return 1;
		}else if (pValue < 0){
			return -1;
		}else{
			return 0;
		}
	}
};

dbsfaces.number = {
	isNumber: function(pVal){
		return !isNaN(dbsfaces.number.parseFloat(pVal)) && isFinite(pVal);
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
		return pValue.replace(/[^-\d\.]/g, '');
 	},
 	

 	//Converte para float verificando a pontuação quando for string
 	parseFloat: function(pValue){
 		if (Number(pValue) === pValue){
 			return pValue;
 		}else if (typeof pValue === 'string' || pValue instanceof String){
 			pValue = pValue.replaceAll(dbsfaces.groupSeparator, "");
 			pValue = pValue.replaceAll(dbsfaces.decimalSeparator, ".");
 			return parseFloat(pValue);
 		}
 	}
};

dbsfaces.format = {
	
	number: function(pValue, pDecimals, pSeparateThousand){
		pValue = dbsfaces.number.parseFloat(pValue);
		pValue = dbsfaces.math.round(pValue, pDecimals);
		var xOptions = {
			minimumFractionDigits: pDecimals,
			useGrouping: true
		}
		if (!(typeof pSeparateThousand == "undefined")){
			xOptions.useGrouping = pSeparateThousand;
		}
		return pValue.toLocaleString(dbsfaces.locale, xOptions);
	},
	
	//Retorna o número simplificado com mil, mi, bi, tri, quatri.
	numberSimplify: function(pValue, pDecimals){
		if (typeof pDecimals == "undefined"){
			pDecimals = 2;
		}
		var xVal = dbsfaces.number.parseFloat(pValue);
		//Salva o sinal
		var xSign = Math.sign(xVal) || 1;
		//Retina o sinal
		var xVal = Math.abs(xVal);
		var xLength = dbsfaces.math.round(xVal, 0).toString().length;
		if (xLength == 0){return;}
		var xSimple = (xVal / Math.pow(10, ((xLength -1) - ((xLength -1) % 3))));
		var xSuf = "";
		if (xLength > 15){
			xSuf = " quatri";
		}else if (xLength > 12){
			xSuf = " tri";
		}else if (xLength > 9){
			xSuf = " bi";
		}else if (xLength > 6){
			xSuf = " mi";
		}else if (xLength > 3){
			xSuf = " mil";
		}else{
			xSimple = xVal;
		}
		xSimple *= xSign;
		return dbsfaces.format.number(xSimple, pDecimals) + xSuf;
	},
	
	getDecimalSeparator: function(){
		if (dbsfaces.format.number(1.1, 1).indexOf(".") >= 0){
			return ".";
		}
		return ",";
	},

	getGroupSeparator: function(){
		if (dbsfaces.format.number(1.1, 1).indexOf(".") >= 0){
			return ",";
		}
		return ".";
	},
	
	splitNumber: function(pValue){
		var xSplit = {
			int: "",
			dec: ""
		}
		if (pValue != null){
			var xValue = dbsfaces.number.parseFloat(pValue);
			var xValueAbs = Math.abs(xValue);
			xSplit.int = parseInt(xValueAbs);
			xSplit.dec = String(dbsfaces.math.round(xValueAbs - xSplit.int, 2)).substring(2);
			if (xSplit.dec.length > 0){
				xSplit.dec = dbsfaces.decimalSeparator + xSplit.dec;
			}
			if (xValue < 0){
				xSplit.int = "-" + xSplit.int;
			}
		}
		return xSplit;
	},

	mask: function(pValue, pMask, pEmptyChr){
		if (pValue ==null 
		 || pEmptyChr == null){
			return "";
		}
		
		if (pMask == ""){
			return pValue;
		}
		
		//9=Numeric; a=Alpha; x=AlphaNumeric
		var xFV = "";
		var xValue = pValue;
		var xVI = 0;
		var xAchou;
		for (var xMI =0; xMI < pMask.length; xMI++){
			var xMC = pMask.substring(xMI, xMI+1).toUpperCase();
			if (xMC == "9" 
			 || xMC == "A"){
				//Busca próximo caracter válido dentro do valor informado, para preencher a respectivo campo na máscara
				xAchou = false;
				while (xVI < xValue.length){
					 var xVC = xValue.charAt(xVI);
					 xVI++;
//					 if (xVC.test(/[0-9A-Za-z]/)){
					if (/[0-9A-Za-z]/.test(xVC)){
						 xFV += xVC;
						 xAchou = true;
						 break;
					 }
				}
				//Se não achou um caracter válido, preenche com o caracter vázio
				if (!xAchou){
					xFV += pEmptyChr;
				}
			}else{
				//Incorpora o caracter da máscara ao valor
				xFV += xMC;
			}
		}
		return xFV;
	}

	

};

	
dbsfaces.string = {
	fromCharCode: function(pVal){
		//Ajuste para os códigos numéricos retornados pelo teclado estendido.
		return String.fromCharCode((96 <= pVal && pVal <= 105)? pVal-48 : pVal);
	},
	
	repeat: function(pString, pTimes){
//		return pString.repeat(pTimes);
		return Array(pTimes + 1).join(pString); //String.repeat não funciona em browsers antigos
	}
};

dbsfaces.ajax = {
	request: function(pSourceId, pExecuteIds, pRenderIds, pOnEvent, pOnError, pParams, pDelay, pTimeout){
		if (pSourceId == null){return;}
		
		pSourceId = dbsfaces.util.jsid(pSourceId);
		var xSource = $(pSourceId);

	    var xForm = xSource.closest("form");
	    if (xForm.length == 0){return;}
	    
	    var xUrl = xForm.attr("action");
	    if (xUrl == null){return;}
	    
	    if (pDelay == null || pDelay == "undefined"){
	    	pDelay = 0;
	    }
	    if (pTimeout == null || pTimeout == "undefined"){
	    	pTimeout = 0;
	    }
	    
		var xData = xForm.serialize();
		xData += "&" + dbsfaces.JAVAX.SOURCE + "=" + xSource[0].id; 
		xData += "&" + dbsfaces.JAVAX.PARTIAL_AJAX + "=true";
		//EXECUTE
		xData += "&" + dbsfaces.JAVAX.PARTIAL_EXECUTE + "=" + xSource[0].id;
		if (pExecuteIds != null){
			pExecuteIds = pExecuteIds.replace("@this", xSource[0].id);
			pExecuteIds = pExecuteIds.replace("@form", xForm[0].id);
			xData += " " + pExecuteIds;
		}
		//RENDER
		if (pRenderIds != null){
			pRenderIds = pRenderIds.replace("@this", xSource[0].id);
			pRenderIds = pRenderIds.replace("@form", xForm[0].id);
			xData += "&" + dbsfaces.JAVAX.PARTIAL_RENDER + "=" + pRenderIds;
		}
		if (pParams != "undefined" && pParams != null){
			xData += "&params=" + pParams;
		}
		
		var xEvent = {};  // data payload for function
		xEvent.source = pSourceId;
		
        var xhrOptions = {
	        url : xUrl,
	        async: true,
	        type : "POST",
	        cache : false,
	        timeout: 0,
	        crossDomain: true,
	        data : xData,
	        dataType : "xml",
	        xhr: function(){
				var xXhr = new XMLHttpRequest();
				xXhr.onprogress = this.onprogress;
				xXhr.addEventListener('readystatechange',function(pEvt){
					if (pOnEvent && typeof pOnEvent === 'function') {
						xEvent.type = "event";
						xEvent.status = "statechange";
						xEvent.readyState = pEvt.target.readyState;
						pOnEvent(xEvent);
					}
				}, false);
				return xXhr;
	        },
	        beforeSend: function(xhr) {
	        	xhr.setRequestHeader('Faces-Request', 'partial/ajax');
	        },
	        error: function(pXhr, pStatus, pErrorThrown) {
        		console.log("erro");
                if (pOnError && typeof pOnError === 'function') {
            		xEvent.type = "error";
            		xEvent.status = pStatus;
            		pOnError(xEvent);
	        	}
	        },
	        complete : function(pXhr, pStatus) {
               if (pOnEvent && typeof pOnEvent === 'function') {
               		xEvent.type = "event";
            		xEvent.status = pStatus;
            		pOnEvent(xEvent);
	        	}
	        },
	        success : function(pData, pStatus, pXhr) {
	        	dbsfaces.ajax.response(pData);
	        	if (pOnEvent && typeof pOnEvent === 'function') {
               		xEvent.type = "event";
            		xEvent.status = pStatus;
            		pOnEvent(xEvent);
	        	}
	        }
	    };
        xhrOptions.global = false;
		
        if (pDelay > 0){
        	clearTimeout(xSource.data("pushtimeout"));
        	xSource.data("pushtimeout", setTimeout(function(e){
        		$.ajax(xhrOptions);
        	}, pDelay));
        }else{
    		$.ajax(xhrOptions);
        }
	},
	
	response: function(pData){
	    xUpdates = $(pData).find('update');
	    for(var i=0; i < xUpdates.length; i++) {
	        var xUpdate = xUpdates.eq(i);
	        var xId = xUpdate.attr('id');
	        var xContent = xUpdate.text();
	        //Atualiza conteúdo do componente
	        var xTarget = $(dbsfaces.util.jsid(xId));
	        xTarget.replaceWith(xContent);
	        xTarget.trigger("loaded");
	        //Busca componente novamente já que foi substituido integralmente
	        xTarget = $(dbsfaces.util.jsid(xId));
	        //Se há controle para exibição gradativa posUpdate(DBSDiv)
	        if (xTarget.hasClass("-posUpdate")){
		        xTarget.css("opacity", "1");
		        xTarget.on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
		        	$(e.target).removeClass("-posUpdate");
		        });
	        }
	    }
	},
	
	onprogress: function(pData){
		
	}
	
}
		
//Monitora evento ajax recebido e dispara evento dbsoft
dbsfaces.onajax = function(e, pData){
	xEle = $(e.source);
	if (xEle.length == 0){
		return;
	}
	if (e.status == "begin"){
		xEle.addClass("-ajaxBegin");
		xEle.trigger(dbsfaces.EVENT.ON_AJAX_BEGIN, pData);
	}else if (e.status == "complete"){
		xEle.trigger(dbsfaces.EVENT.ON_AJAX_COMPLETE, pData);
	}else if (e.status == "success"){
		xEle.removeClass("-ajaxBegin");
		xEle.trigger(dbsfaces.EVENT.ON_AJAX_SUCCESS, pData);
	}
};

dbsfaces.onajaxerror = function(e){
	xEle = $(e.source);
	xEle.removeClass("-ajaxBegin");
	xEle.trigger(dbsfaces.EVENT.ON_AJAX_ERROR);
	return false;
};


if (dbsfaces.locale == null){
	dbsfaces.setLocale(window.navigator.language);
}

